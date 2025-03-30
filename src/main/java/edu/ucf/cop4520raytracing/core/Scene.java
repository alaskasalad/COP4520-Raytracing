package edu.ucf.cop4520raytracing.core;

import edu.ucf.cop4520raytracing.core.light.DirectionalLight;
import edu.ucf.cop4520raytracing.core.light.Light;
import edu.ucf.cop4520raytracing.core.solid.Plane;
import edu.ucf.cop4520raytracing.core.solid.Solid;
import edu.ucf.cop4520raytracing.core.solid.Sphere;
import edu.ucf.cop4520raytracing.core.util.ArrayUtil;
import edu.ucf.cop4520raytracing.core.util.Ray3d;
import it.unimi.dsi.fastutil.Pair;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Builder
@Data
public class Scene {
	public static final Scene DEFAULT = Scene.builder()
	                                         .renderExecutor(Executors.newFixedThreadPool(8))
	                                         .skyboxGenerator(dir -> new Color(182, 227, 229))
	                                         .solids(
			                                         new Plane(new Vector3d(0, -5, 0), new Vector3d(0, 1, 0), Color.GRAY),
			                                         new Sphere(new Vector3d(0, 0, -5), 1, Color.RED),
			                                         new Sphere(new Vector3d(-2, 0, -4), 0.4, Color.GREEN),
			                                         new Sphere(new Vector3d(2, -2, -6), 2.2, Color.CYAN)
	                                         )
	                                         .lights(
			                                         new DirectionalLight(new Vector3d(0, 2, 0), Color.WHITE, 0.02)
	                                         )
	                                         .build();
	
	
	/**
	 * The camera
	 */
	@NonNull @Default private final Camera camera = Camera.builder().build();
	/**
	 * The solids
	 */
	@NonNull private final Solid[] solids;
	/**
	 * The lights
	 */
	@NonNull private final Light[] lights;
	/**
	 * The skybox generator-- (Direction) -> Color
	 */
	@NonNull private final Function<Vector3dc, Color> skyboxGenerator;
	/**
	 * The render executor. This is managed and automatically closed
	 */
	@NonNull private final ExecutorService renderExecutor;
	
	@Default private int width = 800;
	@Default private int height = 600;
	
	/**
	 * We swap between two rasters and copy the entire rendered image to the display in one go,
	 * so we don't get screen tearing from the render being caught in the middle of initializing a frame
	 */
	@Default private ImageDisplay image = new ImageDisplay(800, 600);
	
	
	private static class ImageDisplay {
		@Getter
		private final BufferedImage image;
		
		private final WritableRaster buffer1;
		private final WritableRaster buffer2;
		
		private WritableRaster currentBuffer;
		
		private ImageDisplay(int width, int height) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			buffer1 = image.getRaster().createCompatibleWritableRaster();
			buffer2 = image.getRaster().createCompatibleWritableRaster();
			currentBuffer = buffer1;
		}
		
		public void setPixel(int x, int y, Color color) {
			currentBuffer.setPixel(x, y, new int[] {color.getRed(), color.getGreen(), color.getBlue()});
		}
		
		private void swapBuffers() {
			if (currentBuffer == buffer1) {
				currentBuffer = buffer2;
			} else {
				currentBuffer = buffer1;
			}
		}
		
		public void initNextFrame() {
			image.getRaster().setDataElements(0, 0, currentBuffer);
			swapBuffers();
		}
	}
	
	public BufferedImage getImage() {
		return image.getImage();
	}
	
	
	public void render() {
		var stream = getCoordProducts(width, height);
		stream.parallel()
		      .map(coord -> Pair.of(coord, normalizeCoordinate(coord, width, height)))
		      .map(coord_vec -> Pair.of(coord_vec.left(), new Ray3d(camera.getPosition(), coord_vec.right())))
		      .forEach(this::castRay);
		
		image.initNextFrame();
		/*
		for (int y = 0; y < height; y++) {
			int finalY = y;
			renderExecutor.execute(() -> {
				for (int x = 0; x < width; x++) {
					// Convert the pixel (screen) coordinates to world coordinates
					double xNorm = (x - width / 2.0) / width;
					double yNorm = (finalY - height / 2.0) / height;
					Vector3d dir = new Vector3d(xNorm, yNorm, -1).normalize();
					dir.rotateY(Math.toRadians(camera.getYaw()));
					dir.rotateX(Math.toRadians(camera.getPitch()));

					// Compute the ray
					Ray3d ray = new Ray3d(camera.getPosition(), dir);
					// Compute the color of the skybox using this ray
					Color pixelColor = skyboxGenerator.apply(ray.direction());
					// Compute intersection & hit context
					double closestT = Double.MAX_VALUE;
					Vector3dc hitNormal = null;
					Vector3dc hitPosition = null;
					Solid hitSolid = null;

					for (Solid solid : solids) {
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
						for (Light light : lights) {
							pixelColor = light.applyLighting(pixelColor, hitPosition, hitNormal);
						}
					}

					// & set this data on the image
					image.setRGB(x, finalY, pixelColor.getRGB());
				}
			});
		}*/
	}
	
	// this works well with the JVM's runtime optimizer
	private Stream<Coordinate> getCoordProducts(int width, int height) {
		return IntStream.range(0, height).parallel()
		                .mapToObj(y -> IntStream.range(0, width).parallel().mapToObj(x -> new Coordinate(x, y)))
		                .flatMap(Function.identity());
	}
	
	private Vector3d normalizeCoordinate(Coordinate xy, int width, int height) {
		double xNorm = (xy.x() - width / 2.0) / width;
		double yNorm = (xy.y() - height / 2.0) / height;
		Vector3d dir = new Vector3d(xNorm, yNorm, -1).normalize();
		dir.rotateY(Math.toRadians(camera.getYaw()));
		dir.rotateX(Math.toRadians(camera.getPitch()));
		return dir;
	}
	
	private void castRay(Pair<Coordinate, Ray3d> coord_ray) {
		var coord = coord_ray.left();
		var ray = coord_ray.right();
		// Compute the color of the skybox using this ray
		Color pixelColor = skyboxGenerator.apply(ray.direction());
		// Compute intersection & hit context
		double closestT = Double.MAX_VALUE;
		Vector3dc hitNormal = null;
		Vector3dc hitPosition = null;
		Solid hitSolid = null;
		
		for (Solid solid : solids) {
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
			for (Light light : lights) {
				pixelColor = light.applyLighting(pixelColor, hitPosition, hitNormal);
			}
		}
		
		// & set this data on the image
		image.setPixel(coord.x(), coord.y(), pixelColor);
	}
	
	private record Coordinate(int x, int y) {}
	
	public static class SceneBuilder {
		/**
		 * See {@link Scene#solids}
		 */
		public SceneBuilder solids(Solid... solids) {
			this.solids = ArrayUtil.concat(this.solids, solids);
			return this;
		}
		
		/**
		 * See {@link Scene#lights}
		 */
		public SceneBuilder lights(Light... lights) {
			this.lights = ArrayUtil.concat(this.lights, lights);
			return this;
		}
	}
}
