package net.jqwik.docs.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

public class PropertyLifecycleExamples {

	@Property
	void stringLength(@ForAll String aString) {
		Statistics.collect(aString.length() > 200);

		PropertyLifecycle.after(((executionResult, context) -> {
			assertThat(Statistics.percentage(true))
				.describedAs("coverage of string length > 200")
				.isGreaterThan(2.0);
			return PropertyExecutionResult.successful();
		}));
	}

	@Property
	void stringLength_usingOnSuccess(@ForAll String aString) {
		Statistics.collect(aString.length() > 200);

		PropertyLifecycle.onSuccess(
			() -> assertThat(Statistics.percentage(true))
					  .describedAs("coverage of string length > 200")
					  .isGreaterThan(5.0)
		);

		PropertyLifecycle.onSuccess((() -> {
			System.out.println("SHOULD NOT BE CALLED");
		}));
	}

	@Property
	void stringLengthWithLabel(@ForAll String aString) {
		String lengthLabel = "length > 200";
		Statistics.label(lengthLabel).collect(aString.length() > 200);

		PropertyLifecycle.after(((executionResult, context) -> {
			assertThat(Statistics.label(lengthLabel).percentage(true))
				.describedAs("coverage of [%s]", lengthLabel)
				.isGreaterThan(1.5);
			assertThat(Statistics.label(lengthLabel).count(true))
				.describedAs("count of [%s]", lengthLabel)
				.isGreaterThan(25);
			return PropertyExecutionResult.successful();
		}));
	}
}
