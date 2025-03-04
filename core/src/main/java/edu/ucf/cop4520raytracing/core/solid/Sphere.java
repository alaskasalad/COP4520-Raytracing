package edu.ucf.cop4520raytracing.core.solid;

import edu.ucf.cop4520raytracing.core.util.Ray3d;
import lombok.Data;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.awt.Color;

@Data
public class Sphere implements Solid {

    private final Vector3dc center;
    private final double radius;
    private final Color color;

    @Override
    public double intersect(Ray3d ray) {
        Vector3d originToCenter = new Vector3d(ray.origin()).sub(center);

        // Compute the discriminant
        double a = ray.direction().dot(ray.direction());
        double b = 2.0 * originToCenter.dot(ray.direction());
        double c = originToCenter.dot(originToCenter) - radius * radius;
        double discriminant = b * b - 4 * a * c;
        // If the discriminant is negative, there is no intersection
        if (discriminant < 0) return Solid.NO_HIT;
        // Compute the intersection distance
        return (-b - Math.sqrt(discriminant)) / (2.0 * a);
    }

    @Override
    public Vector3dc getNormal(Ray3d ray, double t) {
        // Compute the intersection point
        Vector3d hitPoint = new Vector3d(ray.origin()).add(new Vector3d(ray.direction()).mul(t));
        // Normal is the direction from the sphere center to the hit point
        return new Vector3d(hitPoint).sub(center).normalize();
    }
}
