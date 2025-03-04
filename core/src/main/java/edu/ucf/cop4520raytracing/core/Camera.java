package edu.ucf.cop4520raytracing.core;

import lombok.Builder;
import lombok.Data;
import org.joml.Vector3d;

@Data
@Builder
public class Camera {

    /** The position of the camera (mutable) */
    @Builder.Default private final Vector3d position = new Vector3d(0, 0, 0);
    /** The yaw */
    @Builder.Default private double yaw = 0;
    /** The pitch */
    @Builder.Default private double pitch = 0;
}
