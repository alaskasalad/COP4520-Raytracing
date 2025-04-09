package edu.ucf.cop4520raytracing.core.util;

import edu.ucf.cop4520raytracing.core.Camera;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

import static edu.ucf.cop4520raytracing.core.util.VectorUtil.vectorOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static testing.Matchers.*;

class VectorUtilTest {

//    @Test
//    void normalizePixelCoordinate() {
//    }

    @Test
    void getRayDirection_lookStraight() {
        var cam = Camera.builder().position(new Vector3d()).pitch(0).yaw(0).build();
        var coord = new Vector2d(0, 0);
        Vector3d rayDirection = VectorUtil.getRayDirection(coord, cam);
        assertEquals(new Vector3d(0, 0, 1), rayDirection);
    }

    @Test
    void getRayDirection_lookUp() {
        var cam = Camera.builder().position(new Vector3d()).pitch(0).yaw(Math.PI / 2).build();
        var coord = new Vector2d(0, 0);
        Vector3d rayDirection = VectorUtil.getRayDirection(coord, cam);

        assertThat(rayDirection, equalishTo(new Vector3d(0, 1, 0)));
    }

    @Test
    void rotate_yawOnly() {
        var vec = VectorUtil.rotateToMatchPitchYaw(vectorOf(0, 0, 1), 0, Math.PI / 2);
        assertThat(vec, equalishTo(vectorOf(0, 1, 0)));
    }

    @Test
    void rotate_pitchOnly() {
        var vec = VectorUtil.rotateToMatchPitchYaw(vectorOf(0, 0, 1), Math.PI / 2, 0);
        assertThat(vec, equalishTo(vectorOf(1, 0, 0)));
    }
}