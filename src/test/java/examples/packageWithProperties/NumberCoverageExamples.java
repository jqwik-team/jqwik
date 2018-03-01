package examples.packageWithProperties;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberCoverageExamples {


	public static final Condition<Long> EVEN = new Condition<>(x -> x % 2 == 0, "even");
	public static final Condition<Long> SMALL = new Condition<>(x -> x < 2000, "< 2000");

	@Provide
	Arbitrary<Long> evenNumbers() {
		return Arbitraries.longs().filter(l -> l % 2 == 0);
	}

	@Property
	void evenNumbersAreEven(@ForAll("evenNumbers") long evenNumber) {
		assertThat(evenNumber)
			.is(EVEN);
	}

	@Property
	void evenNumbersAreEvenAndSmall(@ForAll("evenNumbers") long evenNumber) {
		//System.out.println(evenNumber);
		assertThat(evenNumber)
			.is(EVEN)
			.is(SMALL);
	}

	@Provide
	Arbitrary<Float> evenFloats() {
		return Arbitraries.floats().filter(f -> f.longValue() % 2 == 0);
	}

	@Property(reporting = Reporting.FALSIFIED)
	void floatsAreSmall(@ForAll("evenFloats") float evenNumber) {
		assertThat((long) evenNumber).is(SMALL);
	}
}
