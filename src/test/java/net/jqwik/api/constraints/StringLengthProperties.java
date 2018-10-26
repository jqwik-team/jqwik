package net.jqwik.api.constraints;

import net.jqwik.api.*;

class StringLengthProperties {

	@Property
	boolean aStringWithConstrainedLength(@ForAll @StringLength(min = 2, max = 7) String aString) {
		return aString.length() >= 2 && aString.length() <= 7;
	}

	@Property
	boolean fixedSize(@ForAll @StringLength(5) String aString) {
		return aString.length() == 5;
	}

	@Property
	boolean minCanBeUsedWithoutMax(@ForAll @StringLength(min = 2) String aString) {
		return aString.length() >= 2;
	}

	@Property
	boolean maxCanBeUsedWithoutMin(@ForAll @StringLength(max = 7) String aString) {
		return aString.length() <= 7;
	}

}
