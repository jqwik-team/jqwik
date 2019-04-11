package net.jqwik.api.constraints;

import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;

@Group
class WhitespaceProperties {

	@Property
	boolean stringWithWhitespace(@ForAll @Whitespace String aString) {
		return aString.chars().allMatch(Character::isWhitespace);
	}

	@Property
	boolean characterWithWhitespace(@ForAll @Whitespace Character aChar) {
		return Character.isWhitespace(aChar);
	}

	@Property
	boolean charWithWhitespace(@ForAll @Whitespace char aChar) {
		return Character.isWhitespace(aChar);
	}
}
