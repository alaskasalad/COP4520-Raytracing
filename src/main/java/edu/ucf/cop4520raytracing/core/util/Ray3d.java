package edu.ucf.cop4520raytracing.core.util;

import org.joml.Vector3dc;

/**
 * A 3D ray.
 *
 * @param origin the origin
 * @param direction the direction
 */
public record Ray3d(Vector3dc origin, Vector3dc direction) {
}
