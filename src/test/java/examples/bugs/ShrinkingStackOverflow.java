package examples.bugs;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;

public class ShrinkingStackOverflow {
	@Property
	void squareOfRootIsOriginalValue(@ForAll double aNumber) {
		double sqrt = Math.sqrt(aNumber);
		Assertions.assertThat(sqrt * sqrt).isCloseTo(aNumber, Percentage.withPercentage(10));
	}

}
