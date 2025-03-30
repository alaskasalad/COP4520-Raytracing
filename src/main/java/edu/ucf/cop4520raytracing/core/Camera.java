package edu.ucf.cop4520raytracing.core;

import edu.ucf.cop4520raytracing.core.util.Direction;
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
    
    public void addPitch(double pitch) {
        this.pitch += pitch;
    }
    
    public void addYaw(double yaw) {
        this.yaw += yaw;
    }
    
    public void move(Direction dir, double scale) {
        position.add(dir.x * scale, dir.y * scale, dir.z * scale);
    }
    
    public void rotateRelative(double pitch, double yaw) {
       addPitch(pitch);
       addYaw(yaw);
    }
}
