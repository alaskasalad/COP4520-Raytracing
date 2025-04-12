package edu.ucf.cop4520raytracing.core.light;

import org.joml.Vector3dc;

import java.awt.Color;

/**
 * A light.
 */
public interface Light {

    /**
     * Apply lighting to a base color.
     *
     * @param baseColor the base color
     * @param position the position
     * @param normal the normal
     * @return the color
     */
    Color applyLighting(Color baseColor, Vector3dc position, Vector3dc normal);
}
