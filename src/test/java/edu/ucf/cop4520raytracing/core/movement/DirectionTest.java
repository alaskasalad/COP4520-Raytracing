//package edu.ucf.cop4520raytracing.core.movement;
//
//import org.joml.Vector3d;
//import org.joml.Vector3dc;
//import org.junit.jupiter.api.Test;
//
//import static edu.ucf.cop4520raytracing.core.movement.Direction.*;
//import static edu.ucf.cop4520raytracing.core.util.VectorUtil.vectorOf;
//import static edu.ucf.cop4520raytracing.core.util.VectorUtil.vectorOfU;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static testing.Matchers.equalishTo;
//
//class BasicDirectionTest {
//    final Vector3dc facing = vectorOf(0, 0, 1);
//
//    @Test
//    public void up() {
//        assertThat(UP.getRelativeDirectionVector(facing), equalishTo(vectorOf(0, 1, 0)));
//    }
//
//    @Test
//    public void down() {
//        assertThat(DOWN.getRelativeDirectionVector(facing), equalishTo(vectorOf(0, -1, 0)));
//    }
//
//    @Test
//    public void left() {
//        assertThat(LEFT.getRelativeDirectionVector(facing), equalishTo(vectorOf(1, 0, 0)));
//    }
//
//    @Test
//    public void right() {
//        assertThat(RIGHT.getRelativeDirectionVector(facing), equalishTo(vectorOf(-1, 0, 0)));
//    }
//
//    @Test
//    public void forward() {
//        assertThat(FORWARD.getRelativeDirectionVector(facing), equalishTo(vectorOf(0, 0, 1)));
//    }
//
//    @Test
//    public void backward() {
//        assertThat(BACKWARD.getRelativeDirectionVector(facing), equalishTo(vectorOf(0, 0, -1)));
//    }
//}
//
//class AngledDirectionTest {
//    // should be a 45° angle on the xz plane, or looking 45° upward from straight on
//    final Vector3dc facing = vectorOfU(0, 1, 1);
//
//    /// Root 2 over 2
//    static final double rtot = Math.sqrt(2) / 2;
//    static final double piov2 = Math.PI / 2; // I hate radians in computing
//
//    @Test
//    public void up() {
//        assertThat(UP.getRelativeDirectionVector(facing), equalishTo(vectorOfU(0, 1, -1)));
//    }
//
//    @Test
//    public void down() {
//        assertThat(DOWN.getRelativeDirectionVector(facing), equalishTo(vectorOfU(0, -1, 1)));
//    }
//
//    // should be exactly the same as normal, since we have not changed our X position
//    @Test
//    public void left() {
//        assertThat(LEFT.getRelativeDirectionVector(facing), equalishTo(vectorOf(1, 0, 0)));
//    }
//
//    @Test
//    public void right() {
//        assertThat(RIGHT.getRelativeDirectionVector(facing), equalishTo(vectorOf(-1, 0, 0)));
//    }
//
//    @Test
//    public void forward() {
//        assertThat(FORWARD.getRelativeDirectionVector(facing), equalishTo(vectorOfU(0, 1, 1)));
//    }
//
//    @Test
//    public void backward() {
//        assertThat(BACKWARD.getRelativeDirectionVector(facing), equalishTo(vectorOfU(0, -1, -1)));
//    }
//}
//
