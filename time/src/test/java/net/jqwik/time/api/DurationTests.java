package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class DurationTests {

	@Provide
	Arbitrary<Duration> durations() {
		return Times.durations();
	}

	@Group
	class SimpleArbitraries {

		@Property
		void validDurationIsGenerated(@ForAll("durations") Duration duration) {
			assertThat(duration).isNotNull();
		}

	}

	@Group
	class SimpleAnnotations {

		@Property
		@Disabled("Not available.")
		void validDurationIsGenerated(@ForAll Duration duration) {
			assertThat(duration).isNotNull();
		}

	}

	@Group
	class DurationMethods {

		@Property
		void between(@ForAll("durations") Duration start, @ForAll("durations") Duration end, @ForAll Random random) {

			Assume.that(start.compareTo(end) <= 0);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			assertAllGenerated(durations.generator(1000), random, duration -> {
				assertThat(duration.compareTo(start)).isGreaterThanOrEqualTo(0);
				assertThat(duration.compareTo(end)).isLessThanOrEqualTo(0);
			});

		}

		@Property
		void betweenSame(@ForAll("durations") Duration durationSame, @ForAll Random random) {

			Arbitrary<Duration> durations = Times.durations().between(durationSame, durationSame);

			assertAllGenerated(durations.generator(1000), random, duration -> {
				assertThat(duration).isEqualTo(durationSame);
			});

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			DurationArbitrary durations = Times.durations();
			Duration value = falsifyThenShrink(durations, random);
			assertThat(value).isEqualTo(Duration.ofSeconds(0, 0));
		}

		@Property
		@Disabled("Failing at the moment.")
		void shrinksToSmallestFailingPositiveValue(@ForAll Random random) {
			DurationArbitrary durations = Times.durations();
			TestingFalsifier<Duration> falsifier = duration -> duration.compareTo(Duration.ofSeconds(999_392_192, 709_938_291)) < 0;
			Duration value = falsifyThenShrink(durations, random, falsifier);
			assertThat(value).isEqualTo(Duration.ofSeconds(999_392_192, 709_938_291));
		}

		@Property
		@Disabled("Failing at the moment.")
		void shrinksToSmallestFailingNegativeValue(@ForAll Random random) {
			DurationArbitrary durations = Times.durations();
			TestingFalsifier<Duration> falsifier = duration -> duration.compareTo(Duration.ofSeconds(-999_392_192, 709_938_291)) > 0;
			Duration value = falsifyThenShrink(durations, random, falsifier);
			assertThat(value).isEqualTo(Duration.ofSeconds(-999_392_192, 709_938_291));
		}

	}

	@Group
	class ExhaustiveGeneration {

		//TODO

	}

	@Group
	class EdgeCasesGeneration {

		//TODO

	}

	@Group
	class CheckEqualDistribution {

		//TODO

	}

}
