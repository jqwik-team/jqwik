package net.jqwik.docs;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class AssumptionExamples {

	@Property
	boolean comparingUnequalStrings( //
			@ForAll @StringLength(min = 1, max = 10) String string1, //
			@ForAll @StringLength(min = 1, max = 10) String string2 //
	) {
		Assume.that(!string1.equals(string2));

		return string1.compareTo(string2) != 0;
	}

	@Property(maxDiscardRatio = 100)
	boolean findingContainedStrings( //
			@ForAll @StringLength(min = 1, max = 10) String container, //
			@ForAll @StringLength(min = 1, max = 5) String contained //
	) {
		Assume.that(container.contains(contained));

		return container.contains(contained);
	}

	@Property
	boolean findingContainedStrings_variant( //
			@ForAll @StringLength(min = 5, max = 10) String container, //
			@ForAll @IntRange(min = 1, max = 5) int length, //
			@ForAll @IntRange(min = 0, max = 9) int startIndex //
	) {
		Assume.that((length + startIndex) <= container.length());

		String contained = container.substring(startIndex, startIndex + length);
		return container.contains(contained);
	}
}
