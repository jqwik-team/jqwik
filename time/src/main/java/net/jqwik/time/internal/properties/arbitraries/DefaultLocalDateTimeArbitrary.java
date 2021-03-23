package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;
import java.time.temporal.*;

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

	private ChronoUnit ofPrecision = DefaultLocalTimeArbitrary.DEFAULT_PRECISION;
	private boolean ofPrecisionSet = false;

	@Override
	protected Arbitrary<LocalDateTime> arbitrary() {

		LocalDateTime effectiveMin = calculateEffectiveMin();
		LocalDateTime effectiveMax = calculateEffectiveMax();
		if (effectiveMax.isBefore(effectiveMin)) {
			throw new IllegalArgumentException("The maximum date time is too soon after the minimum date time.");
		}

		LocalDateArbitrary dates = Dates.dates();
		TimeArbitraries times = generateTimeArbitraries(effectiveMin, effectiveMax, ofPrecision);

		dates = dates.atTheEarliest(effectiveMin.toLocalDate());
		dates = dates.atTheLatest(effectiveMax.toLocalDate());

		LocalDate effectiveMinDate = effectiveMin.toLocalDate();
		LocalDate effectiveMaxDate = effectiveMax.toLocalDate();

		return dates.flatMap(date -> timesByDate(times, date, effectiveMinDate, effectiveMaxDate)
											 .map(time -> LocalDateTime.of(date, time)));

	}

	private LocalTimeArbitrary timesByDate(TimeArbitraries times, LocalDate date, LocalDate effectiveMin, LocalDate effectiveMax) {
		if (date.isEqual(effectiveMin)) {
			return times.firstDay;
		} else if (date.isEqual(effectiveMax)) {
			return times.lastDay;
		} else {
			return times.daysBetween;
		}
	}

	private LocalDateTime calculateEffectiveMin() {
		LocalDateTime effective = min != null ? min : DEFAULT_MIN;
		return calculateEffectiveMinWithPrecision(effective);
	}

	private LocalDateTime calculateEffectiveMinWithPrecision(LocalDateTime effective) {
		LocalDate date = effective.toLocalDate();
		LocalTime time = effective.toLocalTime();
		try {
			time = DefaultLocalTimeArbitrary.calculateEffectiveMinWithPrecision(time, ofPrecision);
		} catch (IllegalArgumentException e) {
			time = LocalTime.MIN;
			LocalDate effectiveDate;
			try {
				effectiveDate = date.plusDays(1);
			} catch (DateTimeException dateTimeException) {
				throw e;
			}
			date = effectiveDate;
		}
		return LocalDateTime.of(date, time);
	}

	private LocalDateTime calculateEffectiveMax() {
		LocalDateTime effective = max != null ? max : DEFAULT_MAX;
		return calculateEffectiveMaxWithPrecision(effective);
	}

	private LocalDateTime calculateEffectiveMaxWithPrecision(LocalDateTime effective) {
		LocalDate date = effective.toLocalDate();
		LocalTime time = effective.toLocalTime();
		time = DefaultLocalTimeArbitrary.calculateEffectiveMaxWithPrecision(time, ofPrecision);
		return LocalDateTime.of(date, time);
	}

	private TimeArbitraries generateTimeArbitraries(LocalDateTime effectiveMin, LocalDateTime effectiveMax, ChronoUnit ofPrecision) {
		TimeArbitraries times = new TimeArbitraries();
		if (effectiveMin.toLocalDate().isEqual(effectiveMax.toLocalDate())) {
			times.firstDay = Times.times().between(effectiveMin.toLocalTime(), effectiveMax.toLocalTime()).ofPrecision(ofPrecision);
		} else {
			times.firstDay = Times.times().atTheEarliest(effectiveMin.toLocalTime()).ofPrecision(ofPrecision);
			times.daysBetween = Times.times().ofPrecision(ofPrecision);
			times.lastDay = Times.times().atTheLatest(effectiveMax.toLocalTime()).ofPrecision(ofPrecision);
		}
		return times;
	}

	private void setOfPrecisionImplicitly(DefaultLocalDateTimeArbitrary clone, LocalDateTime dateTime) {
		if (clone.ofPrecisionSet) {
			return;
		}
		ChronoUnit ofPrecision = DefaultLocalTimeArbitrary.calculateOfPrecisionFromTime(dateTime.toLocalTime());
		if (clone.ofPrecision.compareTo(ofPrecision) > 0) {
			clone.ofPrecision = ofPrecision;
		}
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
		setOfPrecisionImplicitly(clone, min);
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
		setOfPrecisionImplicitly(clone, max);
		clone.max = max;
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary ofPrecision(ChronoUnit ofPrecision) {
		if (!DefaultLocalTimeArbitrary.ALLOWED_PRECISIONS.contains(ofPrecision)) {
			throw new IllegalArgumentException("Precision value must be one of these ChronoUnit values: HOURS, MINUTES, SECONDS, MILLIS, MICROS, NANOS");
		}

		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.ofPrecisionSet = true;
		clone.ofPrecision = ofPrecision;
		return clone;
	}

	private class TimeArbitraries {

		private LocalTimeArbitrary firstDay;
		private LocalTimeArbitrary daysBetween;
		private LocalTimeArbitrary lastDay;

	}

}
