package net.jqwik.docs.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

public class PropertyLifecycleExamples {

	@Property
	@PerProperty(CheckSuccess.class)
	void test() {

	}

	class CheckSuccess implements PerProperty.PerPropertyLifecycle {
		@Override
		public void before() {

		}
		@Override
		public void onSuccess() {

		}
	}

	int maxLength = 0;

//	@Property
//	void maxStringLength(@ForAll String aString) {
//
//		maxLength = Math.max(maxLength, aString.length());
//
//		PropertyLifecycle.after(((executionResult, context) -> {
//			assertThat(maxLength)
//				.describedAs("max size of all generated strings")
//				.isGreaterThan(10);
//			return executionResult.mapToSuccessful();
//		}));
//	}

	long aggregatedLength = 0;

	@Property
	void aggregatedStringLength(@ForAll @StringLength(min = 1) String aString) {

		aggregatedLength += aString.length();

		PropertyLifecycle.onSuccess(
			() -> assertThat(aggregatedLength)
					  .describedAs("aggregated length of all generated strings")
					  .isGreaterThanOrEqualTo(100)
		);

		PropertyLifecycle.onSuccess((() -> {
			System.out.println("SHOULD NOT BE CALLED");
		}));
	}

}
