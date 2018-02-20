package net.jqwik.api.constraints;

import net.jqwik.api.*;

class StringLengthProperties {

	@Property
	boolean aStringWithConstrainedLength(@ForAll @StringLength(min = 2, max = 7) String aString) {
		return aString.length() >= 2 && aString.length() <= 7;
	}
}
