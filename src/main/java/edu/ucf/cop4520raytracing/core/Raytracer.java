package edu.ucf.cop4520raytracing.core;

import edu.ucf.cop4520raytracing.core.util.Direction;
import edu.ucf.cop4520raytracing.core.util.KeyPress;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Builder;
import lombok.NonNull;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Builder
public class Raytracer extends JPanel implements KeyListener, Runnable, AutoCloseable {

    /** The main renderer executor. This is managed and automatically closed */
    @NonNull private final ScheduledExecutorService renderExecutor;
    /** The scene */
    @NonNull private final Scene scene;
    /** Target FPS */
    @Builder.Default private final int fps = 30;
    /** Whether it is rendering */
    @Builder.Default private AtomicBoolean running = new AtomicBoolean(true);
    /** The key bindings. i.e. w -> move camera forward */
    private /* lateinit / @MonotonicNonNull */ Int2ObjectMap<KeyPress> keyBindings;

    // collides with JComponent#enable
    public void Raytracer$enable() {
        // Set default keybindings if they weren't provided
        if (keyBindings == null) {
            this.keyBindings = new Int2ObjectOpenHashMap<>(Map.of(
                    // Application related
                    KeyEvent.VK_ESCAPE, KeyPress.withKeyPress(e -> close()),
                    KeyEvent.VK_R, KeyPress.withKeyPress(e -> running.set(!running.get())),
                    // Movement related
                    KeyEvent.VK_SPACE, KeyPress.withKeyPress(e -> scene.getCamera().move(Direction.UP, 1)),
                    KeyEvent.VK_SHIFT, KeyPress.withKeyPress(e -> scene.getCamera().move(Direction.DOWN, 1)),
                    KeyEvent.VK_W, KeyPress.withKeyPress(e -> scene.getCamera().move(Direction.FORWARD, 1)),
                    KeyEvent.VK_S, KeyPress.withKeyPress(e -> scene.getCamera().move(Direction.BACKWARD, 1)),
                    KeyEvent.VK_A, KeyPress.withKeyPress(e -> scene.getCamera().move(Direction.LEFT, 1)),
                    KeyEvent.VK_D, KeyPress.withKeyPress(e -> scene.getCamera().move(Direction.RIGHT, 1)),
                    KeyEvent.VK_Q, KeyPress.withKeyPress(e -> scene.getCamera().addYaw(-1)),
                    KeyEvent.VK_E, KeyPress.withKeyPress(e -> scene.getCamera().addYaw(1))
            ));
        }

        // Initialize the application context
        JFrame frame = new JFrame("Ray Tracer");
        frame.add(this);
        frame.setSize(scene.getImage().getWidth(), scene.getImage().getHeight());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        setFocusable(true);
        requestFocusInWindow();

        frame.addKeyListener(this);
        addKeyListener(this);

        // Start rendering
        renderExecutor.scheduleAtFixedRate(this, 0, 1000 / fps, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Render the image
        g.drawImage(scene.getImage(), 0, 0, null);
    }

    // region KeyListener
    @Override
    public void keyTyped(KeyEvent keyEvent) {
        // no-op
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        KeyPress keyPress = keyBindings.get(keyEvent.getKeyCode());
        if (keyPress == null) return;
        keyPress.getOnKeyPress().accept(keyEvent);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        KeyPress keyPress = keyBindings.get(keyEvent.getKeyCode());
        if (keyPress == null) return;
        keyPress.getOnKeyRelease().accept(keyEvent);
    }
    // endregion

    @Override
    public void run() {
        if (!this.running.get()) {
            return;
        }
        
        try {
//            long startTime = System.nanoTime();
            scene.render();
            this.repaint();
//            long timeElapsed = System.nanoTime() - startTime;
//            if (timeElapsed < getFrameIntervalNanos()) {
//                Thread.sleep(getFrameIntervalNanos() - timeElapsed); // sleep until next frame
//            }
        } catch (Throwable rock) {
            // Kill the entire thing if it blows up
//            running.set(false);
            //noinspection CallToPrintStackTrace
            rock.printStackTrace();
            close();
        }
        
    }

    @Override
    public void close() {
        // Pause rendering
        running.set(false);

        // Kill executors
        shutdown(renderExecutor);
        shutdown(scene.getRenderExecutor());

        // Exit the application
        System.exit(0);
    }

    /**
     * Shutdown an executor service.
     */
    private static void shutdown(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static class RaytracerBuilder {
        public RaytracerBuilder scene(Consumer<Scene.SceneBuilder> block) {
            Scene.SceneBuilder builder = Scene.builder();
            block.accept(builder);
            this.scene = builder.build();
            return this;
        }
    }
}
