package examples.bugs;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;

import net.jqwik.api.*;

public class ShrinkingStackOverflow {

	// Bug: Will run to stack overflow
	@Property
	void squareOfRootIsOriginalValue(@ForAll double aNumber) {
		double sqrt = Math.sqrt(aNumber);
		Assertions.assertThat(sqrt * sqrt).isCloseTo(aNumber, Percentage.withPercentage(10));
	}
}
