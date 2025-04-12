package edu.ucf.cop4520raytracing.core.util;

import edu.ucf.cop4520raytracing.core.Raytracer;

import java.awt.event.KeyEvent;

public interface IKeyPress {
    void onKeyPressed(KeyEvent evt, Raytracer rt);
    void onKeyReleased(KeyEvent evt, Raytracer rt);

    /**
     *
     * @param mask Modifier mask for action to be performed. (e.g. ALT_DOWN_MASK | SHIFT_DOWN_MASK for Alt+Shift)
     * @param action The action to be performed if this modifier is held.
     * @return A wrapped IKeyPress handler that responds to the modifier key.
     */
    @SuppressWarnings("unchecked")
    default IKeyPress withModifier(int mask, IKeyPress action) {
        final var wrappedAction = this;

        return new IKeyPress() {
            @Override
            public void onKeyPressed(KeyEvent evt, Raytracer rt) {
                var modifiers = evt.getModifiersEx();

                if ((mask & modifiers) == mask) {
                    // if modifiers match exactly, do thing
                    action.onKeyPressed(evt, rt);
                } else {
                    wrappedAction.onKeyPressed(evt, rt);
                }
            }

            @Override
            public void onKeyReleased(KeyEvent evt, Raytracer rt) {
                var modifiers = evt.getModifiersEx();

                if ((mask & modifiers) == mask) {
                    // if modifiers match exactly, do thing
                    action.onKeyReleased(evt, rt);
                } else {
                    wrappedAction.onKeyReleased(evt, rt);
                }
            }
        };
    }
}
