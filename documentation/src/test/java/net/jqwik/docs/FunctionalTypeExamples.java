package net.jqwik.docs;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

class FunctionalTypeExamples {

	@Property
	void fromIntToString(@ForAll Function<Integer, @StringLength(5) String> function) {
		assertThat(function.apply(42)).hasSize(5);
		assertThat(function.apply(1)).isEqualTo(function.apply(1));
	}

	@Property
	void emptyStringsAlwaysTestFalse(@ForAll("predicates") Predicate<String> predicate) {
		assertThat(predicate.test("")).isFalse();
		// System.out.println(predicate.test("any string"));
	}

	@Provide
	Arbitrary<Predicate<String>> predicates() {
		return Functions
			.function(Predicate.class)
			.returns(Arbitraries.of(true, false))
			.when(parameters -> parameters.get(0).equals(""), parameters -> false);
	}

}
