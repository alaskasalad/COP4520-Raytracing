package edu.ucf.cop4520raytracing.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public final class Util {
    /**
     * Shutdown an executor service.
     */
    public static void shutdown(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // this works well with the JVM's runtime optimizer
    public static Stream<Coordinate> generateAllCoordPairs(int width, int height) {
        return IntStream.range(0, height).parallel()
                .mapToObj(y -> IntStream.range(0, width).parallel().mapToObj(x -> new Coordinate(x, y)))
                .flatMap(Function.identity());
    }

    public static <T> boolean inBounds(Collection<T> collection, int index) {
        return 0 <= index && index < collection.size();
    }
}
