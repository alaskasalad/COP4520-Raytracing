package edu.ucf.cop4520raytracing.core.util;

import edu.ucf.cop4520raytracing.core.Camera;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joml.Vector2d;
import org.joml.Vector3d;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public final class VectorUtil {
    public static Vector2d normalizePixelCoordinate(Coordinate xy, int width, int height) {
        double xNorm = (xy.x() - width / 2.0) / width;
        double yNorm = (xy.y() - height / 2.0) / height;
        return new Vector2d(xNorm, yNorm);
    }

    public static Vector3d getRayDirection(Vector2d relativeCoord, Camera camera) {
        var dir = new Vector3d(relativeCoord, -1).normalize();
        dir.rotateX(camera.getPitch());
        dir.rotateY(camera.getYaw());
        return dir;
    }
}
