package edu.ucf.cop4520raytracing.core.util;

import lombok.Builder;
import lombok.Data;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * {@link KeyEvent} consumer.
 */
@Data
@Builder
public class KeyPress {

    /** When the key is pressed */
    @Builder.Default private final Consumer<KeyEvent> onKeyPress = $ -> {};
    /** When the key is released */
    @Builder.Default private final Consumer<KeyEvent> onKeyRelease = $ -> {};

    /**
     * Create a {@link KeyPress} with the given key press consumer and no-op key release consumer.
     *
     * @param onKeyPress the key press consumer
     * @return the key press
     */
    public static KeyPress withKeyPress(Consumer<KeyEvent> onKeyPress) {
        return KeyPress.builder().onKeyPress(onKeyPress).build();
    }
}
