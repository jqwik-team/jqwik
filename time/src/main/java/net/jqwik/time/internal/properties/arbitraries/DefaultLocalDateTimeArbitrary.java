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

	private final LocalDateTimeBetween dateTimeBetween = new LocalDateTimeBetween();
	private final LocalDateBetween dateBetween = new LocalDateBetween();
	private final DayOfMonthBetween dayOfMonthBetween = new DayOfMonthBetween();
	private final MonthBetween monthBetween = new MonthBetween();
	private final OfPrecision ofPrecision = new OfPrecision();

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
		if (dayOfMonthBetween.getMin() != null && dayOfMonthBetween.getMax() != null) {
			dates = dates.dayOfMonthBetween(dayOfMonthBetween.getMin(), dayOfMonthBetween.getMax());
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
		LocalDateTime effective = calculateEffectiveMinDate(dateTimeBetween.getMin());
		effective = effective != null ? effective : DEFAULT_MIN;
		return calculateEffectiveMinWithPrecision(effective);
	}

	private LocalDateTime calculateEffectiveMinDate(LocalDateTime effective) {
		if (dateBetween.getMin() == null) {
			return effective;
		} else if (effective == null || dateBetween.getMin().isAfter(effective.toLocalDate())) {
			return LocalDateTime.of(dateBetween.getMin(), LocalTime.MIN);
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
		LocalDateTime effective = calculateEffectiveMaxDate(dateTimeBetween.getMax());
		effective = effective != null ? effective : DEFAULT_MAX;
		if (effectiveMin.isAfter(effective)) {
			throw new IllegalArgumentException("These date time min/max values cannot be used with these date min/max values");
		}
		return calculateEffectiveMaxWithPrecision(effective);
	}

	private LocalDateTime calculateEffectiveMaxDate(LocalDateTime effective) {
		if (dateBetween.getMax() == null) {
			return effective;
		} else if (effective == null || dateBetween.getMax().isBefore(effective.toLocalDate())) {
			return LocalDateTime.of(dateBetween.getMax(), LocalTime.MAX);
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

	private TimeArbitraries generateTimeArbitraries(LocalDateTime effectiveMin, LocalDateTime effectiveMax, OfPrecision ofPrecision) {
		TimeArbitraries times = new TimeArbitraries();
		if (effectiveMin.toLocalDate().isEqual(effectiveMax.toLocalDate())) {
			times.firstDay = Times.times().between(effectiveMin.toLocalTime(), effectiveMax.toLocalTime()).ofPrecision(ofPrecision.get());
		} else {
			times.firstDay = Times.times().atTheEarliest(effectiveMin.toLocalTime()).ofPrecision(ofPrecision.get());
			times.daysBetween = Times.times().ofPrecision(ofPrecision.get());
			times.lastDay = Times.times().atTheLatest(effectiveMax.toLocalTime()).ofPrecision(ofPrecision.get());
		}
		return times;
	}

	private void setOfPrecisionImplicitly(DefaultLocalDateTimeArbitrary clone, LocalDateTime dateTime) {
		if (clone.ofPrecision.isSet()) {
			return;
		}
		ChronoUnit ofPrecision = DefaultLocalTimeArbitrary.calculateOfPrecisionFromTime(dateTime.toLocalTime());
		if (clone.ofPrecision.get().compareTo(ofPrecision) > 0) {
			clone.ofPrecision.setProgrammatically(ofPrecision);
		}
	}

	@Override
	public LocalDateTimeArbitrary atTheEarliest(LocalDateTime min) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.dateTimeBetween.set(min, null);
		setOfPrecisionImplicitly(clone, min);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary atTheLatest(LocalDateTime max) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.dateTimeBetween.set(null, max);
		setOfPrecisionImplicitly(clone, max);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary dateBetween(LocalDate min, LocalDate max) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.dateBetween.set(min, max);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary yearBetween(Year min, Year max) {
		YearBetween yearBetween = (YearBetween) new YearBetween().set(min, max);
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.dateBetween.setYearBetween(yearBetween);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary monthBetween(Month min, Month max) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.monthBetween.set(min, max);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary dayOfMonthBetween(int min, int max) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.dayOfMonthBetween.set(min, max);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary ofPrecision(ChronoUnit ofPrecision) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.ofPrecision.set(ofPrecision);
		return clone;
	}

	private static class TimeArbitraries {

		private LocalTimeArbitrary firstDay;
		private LocalTimeArbitrary daysBetween;
		private LocalTimeArbitrary lastDay;

	}

}
