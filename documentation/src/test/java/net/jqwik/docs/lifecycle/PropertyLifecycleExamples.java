package net.jqwik.docs.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.PerProperty.*;
import net.jqwik.api.statistics.Statistics;

import static org.assertj.core.api.Assertions.*;

public class PropertyLifecycleExamples {

	int maxLength = 0;

	@Property
	@PerProperty(CheckMaxLength.class)
	void maxStringLength(@ForAll String aString) {
		maxLength = Math.max(maxLength, aString.length());
	}

	private class CheckMaxLength implements PerPropertyLifecycle {
		@Override
		public void onSuccess() {
			assertThat(maxLength)
				.describedAs("max size of all generated strings")
				.isGreaterThan(10);
		}
	}

}
