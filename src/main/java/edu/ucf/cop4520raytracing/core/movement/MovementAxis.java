//package edu.ucf.cop4520raytracing.core.movement;
//
//import org.jetbrains.annotations.Contract;
//import org.joml.Vector3d;
//import org.joml.Vector3dc;
//
//public enum MovementAxis {
//    Z/*(new Matrix3d(
//            1, 0, 0,
//            0, 1, 0,
//            0, 0, 1
//    ))*/,
//    Y/*(new Matrix3d(
//            1, 0, 0,
//            0, 0, -1,
//            0, 1, 0
//    ))*/,
//    X;/*(new Matrix3d(
//            0, 0, 1,
//            0, 1, 0,
//            -1, 0, 0
//    ))*/;
//
//    /**
//     * Get a vector's orthogonal on a given movement axis
//     */
//    @Contract("_->new")
//    public Vector3d orthoOnAxis(Vector3dc in) {
//        return switch (this) {
//            case Z -> new Vector3d(in);
//            case Y -> new Vector3d(in.x(), in.z(), -in.y());
//            case X -> new Vector3d(in.z(), in.y(), -in.x());
//        };
//    }
////
////    @Getter
////    private final Matrix3d transformer;
////
////    MovementAxis(Matrix3d transformer) {
////        this.transformer = transformer;
////    }
//}
