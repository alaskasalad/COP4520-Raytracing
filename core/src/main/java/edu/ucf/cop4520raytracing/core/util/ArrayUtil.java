package edu.ucf.cop4520raytracing.core.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;

@UtilityClass
public class ArrayUtil {
    /**
     * Return a new array with the given array concatenated to the end.
     *
     * @param array the array
     * @param other the other array
     * @return the new array
     */
    @SuppressWarnings("unchecked")
    public <T> T[] concat(@Nullable T[] array, T[] other) {
        if (array == null) return other;

        T[] newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length + other.length);
        System.arraycopy(array, 0, newArray, 0, array.length);
        System.arraycopy(other, 0, newArray, array.length, other.length);
        return newArray;
    }
}
