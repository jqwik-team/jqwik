package net.jqwik.time.api.dates.period;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class ExhaustiveGenerationTests {

	@Example
	void between() {
		Optional<ExhaustiveGenerator<Period>> optionalGenerator =
			Dates.periods().between(Period.of(200, 5, 20), Period.of(200, 5, 24))
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Period> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(5);
		assertThat(generator).containsExactly(
			Period.of(200, 5, 20),
			Period.of(200, 5, 21),
			Period.of(200, 5, 22),
			Period.of(200, 5, 23),
			Period.of(200, 5, 24)
		);
	}

}
