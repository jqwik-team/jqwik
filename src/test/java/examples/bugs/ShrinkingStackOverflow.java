package examples.bugs;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import static org.assertj.core.data.Percentage.*;

public class ShrinkingStackOverflow {

	// Bug: Will run to stack overflow
	// Resolved in release 0.5.5
	@Property()
	void squareOfRootIsOriginalValue(@ForAll double aNumber) {
		double sqrt = Math.sqrt(aNumber);
		Assertions.assertThat(sqrt * sqrt).isCloseTo(aNumber, withPercentage(1));
	}

}
