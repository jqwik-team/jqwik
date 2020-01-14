package net.jqwik.docs.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

public class PropertyLifecycleExamples {

	@Property
	void stringLength(@ForAll String aString) {
		String lengthLabel = "length > 200";
		Statistics.label(lengthLabel).collect(aString.length() > 200);

		PropertyLifecycle.after(((executionResult, context) -> {
			assertThat(Statistics.label(lengthLabel).percentage(true))
				.describedAs("coverage of '%s'", lengthLabel)
				.isGreaterThan(5.0);
			return PropertyExecutionResult.successful();
		}));
	}
}
