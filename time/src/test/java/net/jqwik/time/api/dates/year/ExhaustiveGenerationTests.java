package net.jqwik.time.api.dates.year;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class ExhaustiveGenerationTests {

	@Example
	void between() {
		Optional<ExhaustiveGenerator<Year>> optionalGenerator = Dates.years().between(-5, 5).exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Year> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(11); // Cannot know the number of filtered elements in advance
		assertThat(generator).containsExactly(
			Year.of(-5),
			Year.of(-4),
			Year.of(-3),
			Year.of(-2),
			Year.of(-1),
			Year.of(1),
			Year.of(2),
			Year.of(3),
			Year.of(4),
			Year.of(5)
		);
	}

}
