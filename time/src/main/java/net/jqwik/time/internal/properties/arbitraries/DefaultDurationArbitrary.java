package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultDurationArbitrary extends ArbitraryDecorator<Duration> implements DurationArbitrary {

	public static final Duration DEFAULT_MIN = Duration.ofSeconds(Long.MIN_VALUE, 0);
	public static final Duration DEFAULT_MAX = Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);

	Duration min = DEFAULT_MIN;
	Duration max = DEFAULT_MAX;

	@Override
	protected Arbitrary<Duration> arbitrary() {

		//TODO: Negative 0 values?

		long secondMin = min.getSeconds();
		long secondMax = max.getSeconds();
		Arbitrary<Integer> nanos;

		Arbitrary<Long> seconds = Arbitraries.longs().between(secondMin, secondMax).withDistribution(RandomDistribution.uniform());

		if (min.getSeconds() + 1 != max.getSeconds() || min.getNano() <= max.getNano()) {

			int nanoMin = calculateMinNano();
			int nanoMax = calculateMaxNano();

			nanos = Arbitraries.integers().between(nanoMin, nanoMax).withDistribution(RandomDistribution.uniform());

		} else {

			int nanoAMin = 0;
			int nanoAMax = max.getNano();

			int nanoBMin = min.getNano();
			int nanoBMax = 999_999_999;

			Arbitrary<Integer> nanosA = Arbitraries.integers().between(nanoAMin, nanoAMax).withDistribution(RandomDistribution.uniform());
			Arbitrary<Integer> nanosB = Arbitraries.integers().between(nanoBMin, nanoBMax).withDistribution(RandomDistribution.uniform());

			nanos = Arbitraries.oneOf(nanosA, nanosB);

		}

		Arbitrary<Duration> durations = Combinators.combine(seconds, nanos).as(Duration::ofSeconds);

		durations = durations.filter(v -> v.compareTo(min) >= 0 && v.compareTo(max) <= 0)
							 .edgeCases(edgeCases -> edgeCases.includeOnly(min, Duration.ZERO, max));

		return durations;

	}

	private int calculateMinNano() {
		if (min.getSeconds() == max.getSeconds()) {
			return min.getNano();
		}
		return 0;
	}

	private int calculateMaxNano() {
		if (min.getSeconds() == max.getSeconds()) {
			return max.getNano();
		}
		return 999_999_999;
	}

	@Override
	public DurationArbitrary between(Duration min, Duration max) {
		if (min.compareTo(max) > 0) {
			Duration remember = min;
			min = max;
			max = remember;
		}
		DefaultDurationArbitrary clone = typedClone();
		clone.min = min;
		clone.max = max;
		return clone;
	}

}
