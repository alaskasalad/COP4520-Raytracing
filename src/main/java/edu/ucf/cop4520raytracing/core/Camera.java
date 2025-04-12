package edu.ucf.cop4520raytracing.core;

import edu.ucf.cop4520raytracing.core.movement.Direction;
import edu.ucf.cop4520raytracing.core.util.*;
import lombok.Builder;
import lombok.Data;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

@Data
@Builder
public class Camera {
    /** The position of the camera (mutable) */
    @Builder.Default private final Vector3d position = new Vector3d(0, 0, 0);
    /** The yaw, stored in radians. 0 = looking straight */
    @Builder.Default private double yaw = 0;
    /** The pitch, stored in radians. 0 = <0, 0, 1> */
    @Builder.Default private double pitch = 0;


    // region Movement
    /**
     * @param pitch The pitch (horizontal rotation) to add, in radians. Can be negative.
     */
    public void addPitch(double pitch) {
        this.pitch += pitch;
//        facing.rotateX(pitch);
    }

    /**
     * @param yaw The yaw (vertical rotation) to add, in radians. Can be negative.
     */
    public void addYaw(double yaw) {
        this.yaw += yaw;
//        facing.rotateY(yaw);
    }

    public void move(Direction dir, double scale) {
        var facing = getFacingVector();
        var movementVector = switch (dir) {
            case FORWARD -> facing;
            case BACKWARD -> facing.negate();
            default -> {
                var normal = getNormalVector(facing);
                yield switch (dir) {
                    case UP -> normal;
                    case DOWN -> normal.negate();
                    default -> {
                        var xAxis = facing.cross(normal, new Vector3d());
                        yield switch (dir) {
                            case LEFT -> xAxis;
                            case RIGHT -> xAxis.negate();
                            default -> throw new IllegalArgumentException();
                        };
                    }
                };
            }
        };
        position.add(movementVector.mul(scale));
    }

    public Vector3d getFacingVector() {
        return VectorUtil.rotateToMatchPitchYaw(new Vector3d(0, 0, 1), pitch, yaw).normalize();
    }

    /**
     * The vector representing the upwardly direction
     * @param facing
     * @return
     */
    private Vector3d getNormalVector(Vector3dc facing) {
        // project the absolute y axis onto the facing vector, then subtract that from the y axis
        Vector3d yAxis = new Vector3d(0, 1, 0);
        return yAxis.sub(VectorUtil.projectOnto(yAxis, facing)).normalize();
    }

    public void rotateRelative(double pitch, double yaw) {
       addPitch(pitch);
       addYaw(yaw);
    }
    // endregion

    // region Mutators
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
    // endregion
}
