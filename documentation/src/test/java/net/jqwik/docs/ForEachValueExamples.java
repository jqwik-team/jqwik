package net.jqwik.docs;

import org.assertj.core.api.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class ForEachValueExamples {
	
	@Property
	void canPressAnyKeyOnKeyboard(@ForAll Keyboard keyboard, @ForAll Key key) {
		keyboard.press(key);
		assertThat(keyboard.isPressed(key));

		Arbitrary<Key> unpressedKeys = Arbitraries.of(keyboard.allKeys()).filter(k -> !k.equals(key));
		unpressedKeys.forEachValue(k -> assertThat(keyboard.isPressed(k)).isFalse());
	}
}

class Keyboard {

	public void press(Key key) {
	}

	public boolean isPressed(Key key) {
		return false;
	}

	public Key[] allKeys() {
		return new Key[0];
	}
}

class Key {
	
}
