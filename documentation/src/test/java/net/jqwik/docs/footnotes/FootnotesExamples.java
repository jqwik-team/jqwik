package net.jqwik.docs.footnotes;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.footnotes.*;

@EnableFootnotes
public class FootnotesExamples {

	@Property
	void differenceShouldBeBelow42(@ForAll int number1, @ForAll int number2, Footnotes footnotes) {
		int difference = Math.abs(number1 - number2);
		footnotes.addFootnote(Integer.toString(difference));
		Assertions.assertThat(difference).isLessThan(42);
	}

	@Property
	void lazyFootnotes(@ForAll int anInt, @ForAll boolean shouldFail, Footnotes footnotes) {
		footnotes.addAfterFailure(() -> "anInt=" + anInt);
		footnotes.addAfterFailure(() -> "shouldFail=" + shouldFail);
		Assertions.assertThat(shouldFail).isFalse();
	}

}
