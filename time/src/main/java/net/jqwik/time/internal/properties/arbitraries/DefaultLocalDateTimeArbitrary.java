package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultLocalDateTimeArbitrary extends ArbitraryDecorator<LocalDateTime> implements LocalDateTimeArbitrary {

	private static final LocalDateTime DEFAULT_MIN = LocalDateTime.of(DefaultLocalDateArbitrary.DEFAULT_MIN_DATE, LocalTime.MIN);
	private static final LocalDateTime DEFAULT_MAX = LocalDateTime.of(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE, LocalTime.MAX);

	private LocalDateTime min = null;
	private LocalDateTime max = null;

	@Override
	protected Arbitrary<LocalDateTime> arbitrary() {

		LocalDateTime effectiveMin = min != null ? min : DEFAULT_MIN;
		LocalDateTime effectiveMax = max != null ? max : DEFAULT_MAX;

		LocalDateArbitrary dates = Dates.dates();
		LocalTimeArbitrary times = generateTimeArbitrary(effectiveMin, effectiveMax);

		dates = dates.atTheEarliest(effectiveMin.toLocalDate());
		dates = dates.atTheLatest(effectiveMax.toLocalDate());

		Arbitrary<LocalDateTime> dateTimes = Combinators.combine(dates, times).as(LocalDateTime::of);

		return dateTimes.filter(v -> !v.isBefore(effectiveMin) && !v.isAfter(effectiveMax));

	}

	private LocalTimeArbitrary generateTimeArbitrary(LocalDateTime effectiveMin, LocalDateTime effectiveMax) {
		LocalTimeArbitrary times = Times.times();
		if (effectiveMin.toLocalDate().isEqual(effectiveMax.toLocalDate())) {
			times = times.between(effectiveMin.toLocalTime(), effectiveMax.toLocalTime());
		}
		return times;
	}

	@Override
	public LocalDateTimeArbitrary atTheEarliest(LocalDateTime min) {
		if (min.getYear() <= 0) {
			throw new IllegalArgumentException("Minimum year in a date time must be > 0");
		}
		if ((max != null) && min.isAfter(max)) {
			throw new IllegalArgumentException("Minimum date time must not be after maximum date time");
		}

		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.min = min;
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary atTheLatest(LocalDateTime max) {
		if (max.getYear() <= 0) {
			throw new IllegalArgumentException("Minimum year in a date time must be > 0");
		}
		if ((min != null) && max.isBefore(min)) {
			throw new IllegalArgumentException("Maximum date time must not be before minimum date time");
		}

		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.max = max;
		return clone;
	}

}
