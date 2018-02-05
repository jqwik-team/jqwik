package examples.docs;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.*;

class ShrinkingExamples {

	@Property(reporting = Reporting.FALSIFIED)
	boolean stringShouldBeShrunkToAA(@ForAll @AlphaChars String aString) {
		return aString.length() > 5 || aString.length() < 2;
	}

	@Property
	boolean shrinkingCanTakeLong(@ForAll("first") String first, @ForAll("second") String second) {
		String aString = first + second;
		return aString.length() > 5 || aString.length() < 2;
	}

	@Provide
	Arbitrary<String> first() {
		return Arbitraries.strings('a', 'z', 1, 10).filter(string -> string.endsWith("h"));
	}

	@Provide
	Arbitrary<String> second() {
		return Arbitraries.strings('0', '9', 0, 10).filter(string -> string.length() >= 1);
	}

	@Property(shrinking = ShrinkingMode.OFF)
	void aPropertyWithLongShrinkingTimes(@ForAll List<Set<String>> list1, @ForAll List<Set<String>> list2) {
	}
}
