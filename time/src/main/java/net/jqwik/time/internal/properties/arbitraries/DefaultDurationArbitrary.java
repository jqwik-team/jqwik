package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultDurationArbitrary extends ArbitraryDecorator<Duration> implements DurationArbitrary {

	Duration min = Duration.ofSeconds(Long.MIN_VALUE, 0);
	Duration max = Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);

	@Override
	protected Arbitrary<Duration> arbitrary() {

		long secondMin = min.getSeconds();
		long secondMax = max.getSeconds();

		int nanoMin = calculateMinNano();
		int nanoMax = calculateMaxNano();

		Arbitrary<Long> seconds = Arbitraries.longs().between(secondMin, secondMax);
		Arbitrary<Integer> nanos = Arbitraries.integers().between(nanoMin, nanoMax);

		Arbitrary<Duration> durations = Combinators.combine(seconds, nanos).as(Duration::ofSeconds);

		durations = durations.filter(v -> v.compareTo(min) >= 0 && v.compareTo(max) <= 0)
							 .edgeCases(edgeCases -> edgeCases.includeOnly(min, Duration.ofSeconds(0, 0), max));

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
