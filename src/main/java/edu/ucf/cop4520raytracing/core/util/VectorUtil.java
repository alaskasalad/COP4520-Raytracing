package edu.ucf.cop4520raytracing.core.util;

import edu.ucf.cop4520raytracing.core.Camera;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3dc;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public final class VectorUtil {
    public static Vector3d projectOnto(Vector3dc a, Vector3dc b) {
        return b.mul(a.dot(b) / a.lengthSquared(), new Vector3d());
    }

    public static Vector3d vectorOf(double x, double y, double z) {
        return new Vector3d(x, y, z);
    }

    public static Vector2d vectorOf(double x, double y) {
        return new Vector2d(x, y);
    }
    public static Vector3d vectorOfU(double x, double y, double z) {
        return new Vector3d(x, y, z).normalize();
    }

    public static Vector2d vectorOfU(double x, double y) {
        return new Vector2d(x, y).normalize();
    }


    public static Vector2d normalizePixelCoordinate(Coordinate xy, int width, int height) {
        double xNorm = ((xy.x() - width / 2.0) / width);
        xNorm *= ((double) width / height); // aspect ratio
        double yNorm = (xy.y() - height / 2.0) / height;
        return new Vector2d(xNorm, yNorm);
    }

    public static Vector3d getRayDirection(Vector2d relativeCoord, Camera camera) {
        var dir = new Vector3d(relativeCoord, 1).normalize();
        return VectorUtil.rotateToMatchPitchYaw(dir, camera.getPitch(), camera.getYaw());
    }

    @Contract(pure=false, mutates="param1")
    public static Vector3d rotateToMatchPitchYaw(Vector3d original, double pitch, double yaw) {
        return original.rotateY(pitch).rotateX(-yaw); // WHY DO WE HAVE TO FLIP THEM?????????????? WHY DOES IT ROTATE CLOCKWISE???
    }
}
