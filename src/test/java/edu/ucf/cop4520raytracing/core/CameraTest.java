package edu.ucf.cop4520raytracing.core;

import edu.ucf.cop4520raytracing.core.movement.Direction;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static edu.ucf.cop4520raytracing.core.movement.Direction.*;
class CameraTest {
    final Vector3d startPos = new Vector3d(0, 0, 0);

    final Camera camera = Camera.builder()
                          .position(new Vector3d(startPos))
                          .build();


    @Test
    void move_left() {
        move(camera, LEFT);
        assertEquals(startPos.add(-1, 0, 0), camera.getPosition());
    }

    @Test
    void move_right() {
        move(camera, RIGHT);
        assertEquals(startPos.add(1, 0, 0), camera.getPosition());
    }

    @Test
    void move_up() {
        move(camera, UP);
        assertEquals(startPos.add(0, 1, 0), camera.getPosition());
    }

    @Test
    void move_down() {
        move(camera, DOWN);
        assertEquals(startPos.add(0, -1, 0), camera.getPosition());
    }

    @Test
    void move_forward() {
        move(camera, FORWARD);
        assertEquals(startPos.add(0, 0, 1), camera.getPosition());
    }

    @Test
    void move_backward() {
        move(camera, BACKWARD);
        assertEquals(startPos.add(0, 0, -1), camera.getPosition());
    }

    //region Movement Util
    public static void move(Camera camera, Direction direction) {
        camera.move(direction, 1);
    }
    //endregion

}

class TiltedYTest {
    final Vector3d startPos = new Vector3d(0, 0, 0);

    final double angle = Math.PI / 3;// tilted 60° upwards

    final Camera camera = Camera.builder()
                                .position(new Vector3d(startPos))
                                .yaw(angle)
                                .build();


    // left and right behavior should NOT CHANGE if y is tilted
    @Test
    void move_left() {
        move(camera, LEFT);
        assertEquals(startPos.add(-1, 0, 0), camera.getPosition());
    }

    @Test
    void move_right() {
        move(camera, RIGHT);
        assertEquals(startPos.add(1, 0, 0), camera.getPosition());
    }

    @Test
    void move_up() { // up and down should change according to the pitch, it should move exactly one Y axis rotated the yaw
        move(camera, UP);
        assertEquals(startPos.add(new Vector3d(0, 1, 0).rotateX(-angle)), camera.getPosition());
    }

    @Test
    void move_down() {
        move(camera, DOWN);
        assertEquals(startPos.add(new Vector3d(0, 1, 0).rotateX(-angle).negate()), camera.getPosition());
    }



    @Test
    void move_forward() {
        move(camera, FORWARD);
        assertEquals(startPos.add(new Vector3d(0, 0, 1).rotateX(-angle)), camera.getPosition());
    }

    @Test
    void move_backward() {
        move(camera, BACKWARD);
        assertEquals(startPos.add(new Vector3d(0, 0, 1).rotateX(-angle).negate()), camera.getPosition());
    }

    //region Movement Util
    public static void move(Camera camera, Direction direction) {
        camera.move(direction, 1);
    }
    //endregion

}