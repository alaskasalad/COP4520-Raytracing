package edu.ucf.cop4520raytracing.core.util;

import edu.ucf.cop4520raytracing.core.Raytracer;
import it.unimi.dsi.fastutil.ints.IntObjectPair;

import java.awt.event.KeyEvent;

public interface IKeyPress {
	void onKeyPressed(KeyEvent evt, Raytracer rt);
	void onKeyReleased(KeyEvent evt, Raytracer rt);
	
	/**
	 *
	 * @param modsToActions Pairs of modifier MASKS to actions performed.  Combinations (e.g. `ALT_MASK | SHIFT_MASK`) should come sequentially before single values (e.g. `ALT_MASK`)
	 * @return A wrapped IKeyPress handler that responds to modifier keys being held.
	 */
	@SuppressWarnings("unchecked")
    default IKeyPress withModifiers(IntObjectPair<IKeyPress>... modsToActions) {
		for (IntObjectPair it : modsToActions) {
			assert(it.right() instanceof IKeyPress); // actually i hate java
		}

		var original = this;
		
		return new IKeyPress() {
			private final IKeyPress originalAction = original;
			private final IntObjectPair<IKeyPress>[] mods_actions = modsToActions;
			
			@Override
			public void onKeyPressed(KeyEvent evt, Raytracer rt) {
				var modifiers = evt.getModifiersEx();
				
				if (modifiers == 0) {
					originalAction.onKeyPressed(evt, rt);
					return;
				}
				
				for (IntObjectPair<IKeyPress> modsAction : mods_actions) {
					if ((modsAction.leftInt() & modifiers) == modsAction.leftInt()) {
						// if modifiers match exactly, do thing
						modsAction.right().onKeyPressed(evt, rt);
						return;
					}
				}
				
			}
			
			@Override
			public void onKeyReleased(KeyEvent evt, Raytracer rt) {
				var modifiers = evt.getModifiersEx();
				
				if (modifiers == 0) {
					originalAction.onKeyPressed(evt, rt);
					return;
				}
				
				for (IntObjectPair<IKeyPress> modsAction : mods_actions) {
					if ((modsAction.leftInt() & modifiers) == modsAction.leftInt()) {
						// if modifiers match exactly, do thing then short-circuit
						modsAction.right().onKeyReleased(evt, rt);
						return;
					}
				}
			}
		};
	}
}
