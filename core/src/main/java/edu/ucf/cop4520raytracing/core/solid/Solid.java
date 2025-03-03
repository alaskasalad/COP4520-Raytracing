package edu.ucf.cop4520raytracing.core.solid;

import edu.ucf.cop4520raytracing.core.util.Ray3d;
import org.joml.Vector3dc;

import java.awt.Color;

/**
 * A solid.
 */
public interface Solid {

    /**
     * Indicates that there is no intersection.
     */
    double NO_HIT = -1;

    /**
     * Intersect the solid with a ray.
     *
     * @param ray the ray
     * @return the intersection distance or {@link #NO_HIT} if there is no intersection
     */
    double intersect(Ray3d ray);

    /**
     * Get the normal at the intersection point.
     *
     * @param ray the ray
     * @param t the intersection distance
     * @return the normal
     */
    Vector3dc getNormal(Ray3d ray, double t);

    /**
     * Get the color of the solid.
     *
     * @return the color
     */
    Color getColor();
}
