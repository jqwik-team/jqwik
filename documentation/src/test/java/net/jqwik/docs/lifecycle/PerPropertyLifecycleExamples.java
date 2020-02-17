package net.jqwik.docs.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.PerProperty.*;

import static org.assertj.core.api.Assertions.*;

public class PerPropertyLifecycleExamples {

	int maxLength = 0;

	@Property
	@PerProperty(CheckMaxLength.class)
	void maxStringLength(@ForAll String aString) {
		maxLength = Math.max(maxLength, aString.length());
	}

	private class CheckMaxLength implements Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(maxLength)
				.describedAs("max size of all generated strings")
				.isGreaterThan(10);
		}
	}

}
