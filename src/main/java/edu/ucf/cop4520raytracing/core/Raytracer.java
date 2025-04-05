package edu.ucf.cop4520raytracing.core;

import edu.ucf.cop4520raytracing.core.light.Light;
import edu.ucf.cop4520raytracing.core.rendering.ImageDisplay;
import edu.ucf.cop4520raytracing.core.solid.Solid;
import edu.ucf.cop4520raytracing.core.util.*;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static edu.ucf.cop4520raytracing.core.util.KeyPress.keyDownOnly;
import static edu.ucf.cop4520raytracing.core.util.Util.generateAllCoordPairs;

public class Raytracer extends JPanel implements KeyListener, AutoCloseable {
    private static final int DEFAULT_FRAMERATE = 30;


    /**
     * The main renderer executor. This is managed and automatically closed.
     */
    private final ScheduledExecutorService renderExecutor = Executors.newSingleThreadScheduledExecutor();
    /**
     * The scene being rendered.
     */
    @NonNull
    @Setter
    @Getter
    private Scene scene;
    /**
     * Target FPS
     */
    private final int fps;
    /**
     * Whether it is rendering
     */
    private AtomicBoolean running = new AtomicBoolean(true);
    /**
     * The key bindings. i.e. w -> move camera forward.
     */
    private final Int2ObjectMap<IKeyPress> keyBindings;

    /**
     * The buffer being drawn to.
     */
    private ImageDisplay image = new ImageDisplay(800, 600);

    @Getter
    private final CameraController cameraController;

    // region Constructors
    public Raytracer(int fps, Map<Integer, IKeyPress> keyBindings) {
        super(true);

        if (GraphicsEnvironment.isHeadless()) {
            throw new RuntimeException("You are running this program in a headless environment which does not support GUIs, such as WSL, SSH, or another terminal-only OS.  Please try again in a non-headless environment.");
        }

        this.fps = fps;

        // overwrite any default keybindings with the provided alternates
        this.keyBindings = getDefaultKeybinds();
        this.keyBindings.putAll(keyBindings);

        // set scene to default
        this.scene = Scene.DEFAULT;

        this.cameraController = new CameraController();
        cameraController.activeCameraMovementModifiers = new ObjectOpenHashSet<>(this.keyBindings.size(), Hash.VERY_FAST_LOAD_FACTOR);
    }

    public Raytracer(int fps) {
        this(fps, Map.of());
    }

    public Raytracer(Map<Integer, IKeyPress> keyBindings) {
        this(DEFAULT_FRAMERATE, keyBindings);
    }

    public Raytracer() {
        this(Map.of());
    }
    // endregion


    // region Swing
    // Initialize the application context
    public void initDefaultJFrame() {
        JFrame frame = new JFrame("Ray Tracer");
        frame.add(this);
        frame.setSize(this.image.width, this.image.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setFocusable(true);
        requestFocusInWindow();

        frame.addKeyListener(this);
        addKeyListener(this);

        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Render the image
        g.drawImage(this.image.getImage(), 0, 0, null);
    }
    // endregion


    // region KeyListener
    @Override
    public void keyTyped(KeyEvent keyEvent) {
        // no-op
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        IKeyPress keyPress = keyBindings.get(keyEvent.getKeyCode());
        if (keyPress == null)
            return;
        keyPress.onKeyPressed(keyEvent, this);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        IKeyPress keyPress = keyBindings.get(keyEvent.getKeyCode());
        if (keyPress == null)
            return;
        keyPress.onKeyReleased(keyEvent, this);
    }
    // endregion

    // region Action
    public void start() {
        renderExecutor.scheduleAtFixedRate(this::nextFrame, 0, 1000 / fps, TimeUnit.MILLISECONDS);
    }

    public void nextFrame() {
        if (!this.running.get()) {
            return;
        }

        try {
            cameraController.onTick();

            render(scene);
            this.repaint();
        } catch (Throwable rock) {
            // Kill the entire thing if it blows up
            running.set(false);
            //noinspection CallToPrintStackTrace
            rock.printStackTrace();
            close();
        }
    }
    // endregion


    private void castRay(Scene scene, Pair<Coordinate, Ray3d> coord_ray) {
        var coord = coord_ray.left();
        var ray = coord_ray.right();
        // Compute the color of the skybox using this ray
        Color pixelColor = scene.getSkyboxGenerator().apply(ray.direction());
        // Compute intersection & hit context
        double closestT = Double.MAX_VALUE;
        Vector3dc hitNormal = null;
        Vector3dc hitPosition = null;
        Solid hitSolid = null;

        for (Solid solid : scene.getSolids()) {
            double t = solid.intersect(ray);
            if (t != Solid.NO_HIT && t < closestT) {
                closestT = t;
                hitSolid = solid;
                hitPosition = new Vector3d(ray.origin()).add(new Vector3d(ray.direction()).mul(t));
                hitNormal = solid.getNormal(ray, t);
                pixelColor = solid.getColor();
            }
        }

        // If we hit something, apply lighting to it
        if (hitSolid != null) {
            for (Light light : scene.getLights()) {
                pixelColor = light.applyLighting(pixelColor, hitPosition, hitNormal);
            }
        }

        // & set this data on the image
        image.setPixel(coord.x(), coord.y(), pixelColor);
    }

    public void render(Scene scene) {
        var coords = generateAllCoordPairs(image.width, image.height);

        coords.parallel()
              .map(coord -> Pair.of(coord, VectorUtil.normalizePixelCoordinate(coord, image.width, image.height)))
              .map(coord_relcoord -> Pair.of(coord_relcoord.left(), VectorUtil.getRayDirection(coord_relcoord.right(), cameraController.getActiveCamera())))
              .map(coord_vec -> Pair.of(coord_vec.left(), new Ray3d(cameraController.getActiveCamera().getPosition(), coord_vec.right())))
              .forEach(it -> this.castRay(scene, it));

        image.initNextFrame();
    }


    @Override
    public void close() {
        // Pause rendering
        running.set(false);

        // Kill executors
        Util.shutdown(renderExecutor);
        Util.shutdown(scene.getRenderExecutor());

        // Exit the application
        System.exit(0);
    }


    // region Controls
    protected static double MOVEMENT_PER_FRAME = 0.05;
    /// The amount that the camera controls should rotate the camera per frame, in radians.
    /// Default is 5 degrees per frame.
    protected static double ROT_SENSITIVITY = Math.toRadians(5);

    /// How much the binds that increase rotation/movement speed should change it per press.
    private static final double ROT_SENS_CHANGE = Math.toRadians(2.5);
    private static final double MVMT_SENS_CHANGE = 0.05;

    public static Int2ObjectMap<IKeyPress> getDefaultKeybinds() {
        // Map.of only has 10 slots, so we have to split it up :(
        var binds = new Int2ObjectOpenHashMap<>(
                Map.of(
                        // Application related
                        KeyEvent.VK_ESCAPE, keyDownOnly((evt, rt) -> rt.close()),
                        KeyEvent.VK_R, keyDownOnly((evt, rt) -> rt.running.set(!rt.running.get())),
                        // Movement related
                        KeyEvent.VK_SPACE, new Camera.Mover(Direction.UP, MOVEMENT_PER_FRAME),
                        KeyEvent.VK_SHIFT, new Camera.Mover(Direction.DOWN, MOVEMENT_PER_FRAME),

                        KeyEvent.VK_W, new Camera.Mover(Direction.FORWARD, MOVEMENT_PER_FRAME),
                        KeyEvent.VK_S, new Camera.Mover(Direction.BACKWARD, MOVEMENT_PER_FRAME),
                        KeyEvent.VK_A, new Camera.Mover(Direction.LEFT, MOVEMENT_PER_FRAME),
                        KeyEvent.VK_D, new Camera.Mover(Direction.RIGHT, MOVEMENT_PER_FRAME)
                ), Hash.FAST_LOAD_FACTOR
        );

        binds.putAll(Map.of(
                KeyEvent.VK_LEFT, new Camera.Rotater(0, ROT_SENSITIVITY)
                        .withModifier(
                                KeyEvent.ALT_DOWN_MASK,
                                keyDownOnly((_evt, rt) -> Raytracer.ROT_SENSITIVITY -= ROT_SENS_CHANGE)),
                KeyEvent.VK_RIGHT, new Camera.Rotater(0, -ROT_SENSITIVITY)
                        .withModifier(
                                KeyEvent.ALT_DOWN_MASK,
                                keyDownOnly((_evt, rt) -> Raytracer.ROT_SENSITIVITY += ROT_SENS_CHANGE)),
                KeyEvent.VK_UP, new Camera.Rotater(ROT_SENSITIVITY, 0)
                        .withModifier(
                                KeyEvent.ALT_DOWN_MASK,
                                keyDownOnly((_evt, rt) -> Raytracer.MOVEMENT_PER_FRAME += MVMT_SENS_CHANGE)),
                KeyEvent.VK_DOWN, new Camera.Rotater(-ROT_SENSITIVITY, 0)
                        .withModifier(
                                KeyEvent.ALT_DOWN_MASK,
                                keyDownOnly((_evt, rt) -> Raytracer.MOVEMENT_PER_FRAME -= MVMT_SENS_CHANGE))
        ));

        binds.trim();
        return binds;
    }
    // endregion


    public static class CameraController {
        /**
         * All cameras currently active in the scene.
         */
        private final List<Camera> cameras = new ArrayList<>();

        {
            cameras.add(Camera.builder().build());
        }

        /**
         * Reference to the current rendering camera. Can be changed.
         */
        private Camera currentlyActiveCamera = cameras.getFirst();

        /**
         * Actions to perform on the camera every frame, such as movement. Set by keybind closures.
         */
        protected Set<Consumer<Camera>> activeCameraMovementModifiers;

        public void onTick() {
            for (Consumer<Camera> mover : this.activeCameraMovementModifiers) {
                mover.accept(this.currentlyActiveCamera);
            }
        }

        public void setActiveCamera(int id) {
            if (id < cameras.size()) {
                this.currentlyActiveCamera = cameras.get(id);
            }
        }

        public Camera getActiveCamera() {
            return this.currentlyActiveCamera;
        }

        /**
         * @return ID of the newly-added camera
         */
        public int addCamera(Camera it) {
            cameras.add(it);
            return cameras.size() - 1;
        }

        public void addTickAction(Consumer<Camera> it) {
            activeCameraMovementModifiers.add(it);
        }

        public void removeTickAction(Consumer<Camera> it) {
            activeCameraMovementModifiers.remove(it);
        }
    }
}
