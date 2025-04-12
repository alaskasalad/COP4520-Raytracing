package testing.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import static testing.util.TestUtils.withinError;

public class IsWithinError extends TypeSafeMatcher<Vector3d> {
    private final Vector3dc expected;

    public IsWithinError(Vector3dc expected) {
        this.expected = expected;
    }

    @Override
    protected boolean matchesSafely(Vector3d item) {
        return withinError(expected.x(), item.x())
                && withinError(expected.y(), item.y())
                && withinError(expected.z(), item.z());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("equals ").appendValue(expected);
    }
}
