package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.valueRanges.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultLocalDateTimeArbitrary extends ArbitraryDecorator<LocalDateTime> implements LocalDateTimeArbitrary {

	private static final LocalDateTime DEFAULT_MIN = LocalDateTime.of(DefaultLocalDateArbitrary.DEFAULT_MIN_DATE, LocalTime.MIN);
	private static final LocalDateTime DEFAULT_MAX = LocalDateTime.of(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE, LocalTime.MAX);

	private LocalDateTime min = null;
	private LocalDateTime max = null;

	private LocalDate minDate = null;
	private LocalDate maxDate = null;
	private int minDayOfMonth = -1;
	private int maxDayOfMonth = -1;

	private ChronoUnit ofPrecision = DefaultLocalTimeArbitrary.DEFAULT_PRECISION;
	private boolean ofPrecisionSet = false;

	private MonthBetween monthBetween;

	@Override
	protected Arbitrary<LocalDateTime> arbitrary() {

		LocalDateTime effectiveMin = calculateEffectiveMin();
		LocalDateTime effectiveMax = calculateEffectiveMax(effectiveMin);
		if (effectiveMax.isBefore(effectiveMin)) {
			throw new IllegalArgumentException("The maximum date time is too soon after the minimum date time.");
		}

		LocalDateArbitrary dates = Dates.dates();
		TimeArbitraries times = generateTimeArbitraries(effectiveMin, effectiveMax, ofPrecision);

		dates = dates.atTheEarliest(effectiveMin.toLocalDate());
		dates = dates.atTheLatest(effectiveMax.toLocalDate());

		dates = setDateParams(dates);

		LocalDate effectiveMinDate = effectiveMin.toLocalDate();
		LocalDate effectiveMaxDate = effectiveMax.toLocalDate();

		return dates.flatMap(date -> timesByDate(times, date, effectiveMinDate, effectiveMaxDate)
											 .map(time -> LocalDateTime.of(date, time)));

	}

	private LocalDateArbitrary setDateParams(LocalDateArbitrary dates) {
		if (monthBetween.getMin() != null && monthBetween.getMax() != null) {
			dates = dates.monthBetween(monthBetween.getMin(), monthBetween.getMax());
		}
		if (minDayOfMonth != -1 && maxDayOfMonth != -1) {
			dates = dates.dayOfMonthBetween(minDayOfMonth, maxDayOfMonth);
		}
		return dates;
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
		LocalDateTime effective = calculateEffectiveMinDate(min);
		effective = effective != null ? effective : DEFAULT_MIN;
		return calculateEffectiveMinWithPrecision(effective);
	}

	private LocalDateTime calculateEffectiveMinDate(LocalDateTime effective) {
		if (minDate == null) {
			return effective;
		} else if (effective == null || minDate.isAfter(effective.toLocalDate())) {
			return LocalDateTime.of(minDate, LocalTime.MIN);
		} else {
			return effective;
		}
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

	private LocalDateTime calculateEffectiveMax(LocalDateTime effectiveMin) {
		LocalDateTime effective = calculateEffectiveMaxDate(max);
		effective = effective != null ? effective : DEFAULT_MAX;
		if (effectiveMin.isAfter(effective)) {
			throw new IllegalArgumentException("These date time min/max values cannot be used with these date min/max values");
		}
		return calculateEffectiveMaxWithPrecision(effective);
	}

	private LocalDateTime calculateEffectiveMaxDate(LocalDateTime effective) {
		if (maxDate == null) {
			return effective;
		} else if (effective == null || maxDate.isBefore(effective.toLocalDate())) {
			return LocalDateTime.of(maxDate, LocalTime.MAX);
		} else {
			return effective;
		}
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
	public LocalDateTimeArbitrary dateBetween(LocalDate min, LocalDate max) {
		if (min.isAfter(max)) {
			throw new IllegalArgumentException("Minimum date must not be after maximum date");
		}
		if (min.getYear() <= 0 || max.getYear() <= 0) {
			throw new IllegalArgumentException("Minimum year in a date must be > 0");
		}

		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.minDate = min;
		clone.maxDate = max;
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary yearBetween(Year min, Year max) {
		if (min.isAfter(max)) {
			Year remember = min;
			min = max;
			max = remember;
		}

		if (min.getValue() <= 0 || max.getValue() <= 0) {
			throw new IllegalArgumentException("Minimum year in a date time must be > 0");
		}

		LocalDate minDate = LocalDate.of(min.getValue(), 1, 1);
		LocalDate maxDate = LocalDate.of(max.getValue(), 12, 31);
		return dateBetween(minDate, maxDate);
	}

	@Override
	public LocalDateTimeArbitrary monthBetween(Month min, Month max) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.monthBetween.set(min, max);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary dayOfMonthBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.minDayOfMonth = Math.max(1, Math.min(31, min));
		clone.maxDayOfMonth = Math.max(1, Math.min(31, max));
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

	private static class TimeArbitraries {

		private LocalTimeArbitrary firstDay;
		private LocalTimeArbitrary daysBetween;
		private LocalTimeArbitrary lastDay;

	}

}
