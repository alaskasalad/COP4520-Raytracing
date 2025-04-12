package edu.ucf.cop4520raytracing.core.solid;

import edu.ucf.cop4520raytracing.core.util.Ray3d;
import lombok.Data;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.awt.Color;

@Data
public class Plane implements Solid {

    private static final double EPSILON = 1e-6;
    private final Vector3dc position;
    private final Vector3dc normal;
    private final Color color;

    @Override
    public double intersect(Ray3d ray) {
        // Check if the ray & plane are parallel
        double denominator = normal.dot(ray.direction());
        // If they are (or are close enough to parallel), there is no intersection
        if (Math.abs(denominator) < EPSILON) return Solid.NO_HIT;

        // Compute the intersection point
        Vector3d orignToPlane = new Vector3d(ray.origin()).sub(position);
        double t = orignToPlane.dot(normal) / denominator;
        // If it's behind the plane, there is no intersection
        return t >= 0 ? t : Solid.NO_HIT;
    }

    @Override
    public Vector3dc getNormal(Ray3d ray, double t) {
        // Normal is constant
        return normal;
    }
}
