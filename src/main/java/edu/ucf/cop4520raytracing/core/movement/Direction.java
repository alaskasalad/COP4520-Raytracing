package edu.ucf.cop4520raytracing.core.movement;

import org.jetbrains.annotations.Contract;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public enum Direction {
//    UP(MovementAxis.Y, true),
//    DOWN(MovementAxis.Y, false),
//    LEFT(MovementAxis.X, true),
//    RIGHT(MovementAxis.X, false),
//    FORWARD(MovementAxis.Z, true),
//    BACKWARD(MovementAxis.Z, false);

    UP,DOWN,LEFT,RIGHT,FORWARD,BACKWARD;

//    private final MovementAxis movementAxis;
//    private final boolean isPositive;
//
//    Direction(MovementAxis movementAxis, boolean isPositive) {
//        this.movementAxis = movementAxis;
//        this.isPositive = isPositive;
//    }
//
//    /**
//     * Assumes you're facing in the positive Z direction
//     */
//    @Contract("_->new")
//    public Vector3d getRelativeDirectionVector(Vector3dc facing) {
//        Vector3d newVec = movementAxis.orthoOnAxis(facing);
//        if (!isPositive) {
//            newVec.mul(-1);
//        }
//        return newVec;
//    }
}
