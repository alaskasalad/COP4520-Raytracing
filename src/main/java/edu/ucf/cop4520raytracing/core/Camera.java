package edu.ucf.cop4520raytracing.core;

import edu.ucf.cop4520raytracing.core.util.Direction;
import edu.ucf.cop4520raytracing.core.util.IKeyPress;
import lombok.Builder;
import lombok.Data;
import org.joml.Vector3d;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

@Data
@Builder
public class Camera {
    /** The position of the camera (mutable) */
    @Builder.Default private final Vector3d position = new Vector3d(0, 0, 0);
    /** The yaw */
    @Builder.Default private double yaw = 0;
    /** The pitch */
    @Builder.Default private double pitch = 0;


    // region Movement
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
    // endregion

    public static class Mover implements IKeyPress {
        // Can't be a method reference on this class because we're using *identity* in the set, so it needs to be a singular object
        private final Consumer<Camera> movementAction;

        public Mover(Direction movementDirection, double scale) {
            this.movementAction = (camera) -> camera.move(movementDirection, scale);
        }

        @Override
        public void onKeyPressed(KeyEvent evt, Raytracer rt) {
            rt.getCameraController().addTickAction(this.movementAction);
        }

        @Override
        public void onKeyReleased(KeyEvent evt, Raytracer rt) {
            rt.getCameraController().removeTickAction(this.movementAction);
        }
    }

    public static class Rotater implements IKeyPress {
        // Can't be a method on this class because we're using identity in the set, so it needs to be a single Consumer object
        private final Consumer<Camera> movementAction;

        public Rotater(double pitch, double yaw) {
            this.movementAction = (camera) -> camera.rotateRelative(pitch, yaw);
        }

        @Override
        public void onKeyPressed(KeyEvent evt, Raytracer rt) {
            rt.getCameraController().addTickAction(this.movementAction);
        }

        @Override
        public void onKeyReleased(KeyEvent evt, Raytracer rt) {
            rt.getCameraController().removeTickAction(this.movementAction);
        }
    }
}
