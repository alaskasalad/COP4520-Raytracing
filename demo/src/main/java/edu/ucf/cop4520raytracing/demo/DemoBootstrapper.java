package edu.ucf.cop4520raytracing.demo;

import edu.ucf.cop4520raytracing.core.Raytracer;
import edu.ucf.cop4520raytracing.core.light.DirectionalLight;
import edu.ucf.cop4520raytracing.core.solid.Plane;
import edu.ucf.cop4520raytracing.core.solid.Sphere;
import org.joml.Vector3d;

import java.awt.Color;
import java.util.concurrent.Executors;

public class DemoBootstrapper {

    public static void main(String[] args) {
        Raytracer.builder()
                .renderExecutor(Executors.newSingleThreadScheduledExecutor())
                .scene(scene -> scene
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
                )
                .build()
                .Raytracer$enable();
    }
}
