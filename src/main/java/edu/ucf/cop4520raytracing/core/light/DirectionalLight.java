package edu.ucf.cop4520raytracing.core.light;

import lombok.Data;
import org.joml.Vector3dc;

import java.awt.Color;

@Data
public class DirectionalLight implements Light {

    private final Vector3dc direction;
    private final Color color;
    private final double ambientIntensity;

    @Override
    public Color applyLighting(Color baseColor, Vector3dc position, Vector3dc normal) {
        // Compute diffuse intensity
        double diffuseIntensity = Math.max(normal.dot(direction), 0.0);

        // Blend colors based on diffuse and ambient components
        int r = (int) (baseColor.getRed() * (ambientIntensity + diffuseIntensity * (color.getRed() / 255.0)));
        int g = (int) (baseColor.getGreen() * (ambientIntensity + diffuseIntensity * (color.getGreen() / 255.0)));
        int b = (int) (baseColor.getBlue() * (ambientIntensity + diffuseIntensity * (color.getBlue() / 255.0)));

        // Clamp values to valid color range
        return new Color(Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }
}
