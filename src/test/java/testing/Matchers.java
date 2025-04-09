package testing;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import testing.matchers.IsWithinError;

public class Matchers {
    public static IsWithinError equalishTo(Vector3dc expected) {
        return new IsWithinError(expected);
    }
}
