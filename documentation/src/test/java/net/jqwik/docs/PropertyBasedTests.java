package net.jqwik.docs;

import net.jqwik.api.*;
import org.assertj.core.api.*;

class PropertyBasedTests {

	@Property
	boolean absoluteValueOfAllNumbersIsPositive(@ForAll int anInteger) {
		return Math.abs(anInteger) >= 0;
	}

	@Property
	void lengthOfConcatenatedStringIsGreaterThanLengthOfEach(
		@ForAll String string1, @ForAll String string2
	) {
		String conc = string1 + string2;
		Assertions.assertThat(conc.length()).isGreaterThan(string1.length());
		Assertions.assertThat(conc.length()).isGreaterThan(string2.length());
	}
}
