package testing.util;

public class TestUtils {
    public static final double ERROR = 1e-10;

    /// Account for floating-point rounding errors, especially with Math.PI
    public static boolean withinError(double expected, double actual) {
        return Math.abs(actual - expected) < ERROR;
    }
}
