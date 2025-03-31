package edu.ucf.cop4520raytracing.core;

import edu.ucf.cop4520raytracing.core.util.Direction;
import edu.ucf.cop4520raytracing.core.util.IKeyPress;
import edu.ucf.cop4520raytracing.core.util.KeyPress;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.NonNull;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Raytracer extends JPanel implements KeyListener, AutoCloseable {
	private static final int DEFAULT_FRAMERATE = 30;
	
	
	/**
	 * The main renderer executor. This is managed and automatically closed
	 */
	private final ScheduledExecutorService renderExecutor = Executors.newSingleThreadScheduledExecutor();
	/**
	 * The scene
	 */
	@NonNull private Scene scene;
	/**
	 * Target FPS
	 */
	private final int fps;
	/**
	 * Whether it is rendering
	 */
	private AtomicBoolean running = new AtomicBoolean(true);
	/**
	 * The key bindings. i.e. w -> move camera forward
	 */
	private final Int2ObjectMap<IKeyPress> keyBindings;
	
	protected Set<Consumer<Camera>> activeCameraMovementModifiers;
	
	
	public Raytracer(int fps, Map<Integer, IKeyPress> keyBindings) {
		super(true);
		
		this.fps = fps;
		
		// overwrite any default keybindings with the provided alternates
		this.keyBindings = getDefaultKeybinds();
		this.keyBindings.putAll(keyBindings);
		
		// set scene to default
		this.scene = Scene.DEFAULT;
		
		this.activeCameraMovementModifiers = new ObjectOpenHashSet<>(this.keyBindings.size(), Hash.VERY_FAST_LOAD_FACTOR);
		
	}
	
	public Raytracer(Map<Integer, IKeyPress> keyBindings) {
		this(DEFAULT_FRAMERATE, keyBindings);
	}
	
	public Raytracer() {
		this(Map.of());
	}
	
	public void start() {
		renderExecutor.scheduleAtFixedRate(this::renderFrame, 0, 1000 / fps, TimeUnit.MILLISECONDS);
	}
	
	public void setScene(@NonNull Scene scene) {
		this.scene = scene;
	}
	
	// Initialize the application context
	public void initJFrame() {
		JFrame frame = new JFrame("Ray Tracer");
		frame.add(this);
		frame.setSize(scene.getImage().getWidth(), scene.getImage().getHeight());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		setFocusable(true);
		requestFocusInWindow();
		
		frame.addKeyListener(this);
		addKeyListener(this);
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
	
	
	public void renderFrame() {
		if (!this.running.get()) {
			return;
		}
		
		try {
			for (Consumer<Camera> mover : activeCameraMovementModifiers) {
				mover.accept(scene.getCamera());
			}
			
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
	
	private static final double MOVEMENT_PER_FRAME = 0.04;
	private static final double ROT_SENSITIVITY = 0.04;
	
	public static Int2ObjectMap<IKeyPress> getDefaultKeybinds() {
		var binds = new Int2ObjectOpenHashMap<>(
				Map.of(
						// Application related
						KeyEvent.VK_ESCAPE, KeyPress.keyDownOnly((evt, rt) -> rt.close()),
						KeyEvent.VK_R, KeyPress.keyDownOnly((evt, rt) -> rt.running.set(!rt.running.get())),
						// Movement related
						KeyEvent.VK_SPACE, new Camera.Mover(Direction.UP, MOVEMENT_PER_FRAME),
						KeyEvent.VK_SHIFT, new Camera.Mover(Direction.DOWN, MOVEMENT_PER_FRAME),
						
						KeyEvent.VK_W, new Camera.Mover(Direction.FORWARD, MOVEMENT_PER_FRAME),
						KeyEvent.VK_S, new Camera.Mover(Direction.BACKWARD, MOVEMENT_PER_FRAME),
						KeyEvent.VK_A, new Camera.Mover(Direction.LEFT, MOVEMENT_PER_FRAME),
						KeyEvent.VK_D, new Camera.Mover(Direction.RIGHT, MOVEMENT_PER_FRAME)
				), Hash.FAST_LOAD_FACTOR
		);
		
		// Map.of only has 10 slots :(
		binds.putAll(Map.of(
				KeyEvent.VK_LEFT, new Camera.Rotater(0, ROT_SENSITIVITY),
				KeyEvent.VK_RIGHT, new Camera.Rotater(0, -ROT_SENSITIVITY),
				KeyEvent.VK_UP, new Camera.Rotater(ROT_SENSITIVITY, 0),
				KeyEvent.VK_DOWN, new Camera.Rotater(-ROT_SENSITIVITY, 0)
		));
		
		return binds;
	}
}
