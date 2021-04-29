package net.jqwik.time.api.times.duration.exhaustiveGeneration.precision.precisionImplicitly;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SecondsTests {

	@Example
	void betweenPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(91, 0),
					 Duration.ofSeconds(94, 0)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(91, 0),
			Duration.ofSeconds(92, 0),
			Duration.ofSeconds(93, 0),
			Duration.ofSeconds(94, 0)
		);
	}

	@Example
	void betweenNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(-94, 0),
					 Duration.ofSeconds(-91, 0)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactlyInAnyOrder(
			Duration.ofSeconds(-91, 0),
			Duration.ofSeconds(-92, 0),
			Duration.ofSeconds(-93, 0),
			Duration.ofSeconds(-94, 0)
		);
	}

	@Example
	void betweenTheEarliestPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(117, 0),
					 Duration.ofSeconds(120, 0)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(117, 0),
			Duration.ofSeconds(118, 0),
			Duration.ofSeconds(119, 0),
			Duration.ofSeconds(120, 0)
		);
	}

	@Example
	void betweenTheEarliestNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(-123, 0),
					 Duration.ofSeconds(-120, 0)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactlyInAnyOrder(
			Duration.ofSeconds(-120, 0),
			Duration.ofSeconds(-121, 0),
			Duration.ofSeconds(-122, 0),
			Duration.ofSeconds(-123, 0)
		);
	}

	@Example
	void betweenTheLatestPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(120, 0),
					 Duration.ofSeconds(123, 0)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(120, 0),
			Duration.ofSeconds(121, 0),
			Duration.ofSeconds(122, 0),
			Duration.ofSeconds(123, 0)
		);
	}

	@Example
	void betweenTheLatestNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(-120, 0),
					 Duration.ofSeconds(-117, 0)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactlyInAnyOrder(
			Duration.ofSeconds(-117, 0),
			Duration.ofSeconds(-118, 0),
			Duration.ofSeconds(-119, 0),
			Duration.ofSeconds(-120, 0)
		);
	}

}
