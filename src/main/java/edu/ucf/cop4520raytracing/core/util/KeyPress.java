package edu.ucf.cop4520raytracing.core.util;

import edu.ucf.cop4520raytracing.core.Raytracer;
import lombok.Data;

import java.awt.event.KeyEvent;
import java.util.function.BiConsumer;

/**
 * {@link KeyEvent} consumer.
 */
@Data
public class KeyPress implements IKeyPress {
    /** When the key is pressed */
    private final BiConsumer<KeyEvent, Raytracer> onKeyPress;
    /** When the key is released */
    private final BiConsumer<KeyEvent, Raytracer> onKeyRelease;
    
    /**
     * Create a {@link KeyPress} with the given key press consumer and no-op key release consumer.
     *
     * @param onKeyPress the key press consumer
     * @return the key press
     */
    public static IKeyPress keyDownOnly(BiConsumer<KeyEvent, Raytracer> onKeyPress) {
        return new KeyPress(onKeyPress, KeyPress::defaultConsumer);
    }
    
    public static void defaultConsumer(KeyEvent evt, Raytracer rt) {
        // NO-OP
    }
    
    @Override
    public void onKeyPressed(KeyEvent evt, Raytracer rt) {
        onKeyPress.accept(evt, rt);
    }
    
    @Override
    public void onKeyReleased(KeyEvent evt, Raytracer rt) {
        onKeyRelease.accept(evt, rt);
    }
}
