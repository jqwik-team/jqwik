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
	private final AllowedMonths allowedMonths = new AllowedMonths();
	private final DayOfMonthBetween dayOfMonthBetween = new DayOfMonthBetween();
	private final AllowedDayOfWeeks allowedDayOfWeeks = new AllowedDayOfWeeks();

	private final LocalTimeBetween timeBetween = new LocalTimeBetween();
	private final HourBetween hourBetween = (HourBetween) new HourBetween().set(0, 23);
	private final MinuteBetween minuteBetween = (MinuteBetween) new MinuteBetween().set(0, 59);
	private final SecondBetween secondBetween = (SecondBetween) new SecondBetween().set(0, 59);
	private final OfPrecision ofPrecision = new OfPrecision();

	@Override
	protected Arbitrary<LocalDateTime> arbitrary() {

		LocalDateTime effectiveMin = calculateEffectiveMin();
		LocalDateTime effectiveMax = calculateEffectiveMax(effectiveMin);

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
		dates = dates.onlyMonths(allowedMonths.get().toArray(new Month[]{}));
		if (dayOfMonthBetween.getMin() != null && dayOfMonthBetween.getMax() != null) {
			dates = dates.dayOfMonthBetween(dayOfMonthBetween.getMin(), dayOfMonthBetween.getMax());
		}
		dates = dates.onlyDaysOfWeek(allowedDayOfWeeks.get().toArray(new DayOfWeek[]{}));
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
		DefaultLocalTimeArbitrary.checkTimeValueAndPrecision(effective.toLocalTime(), ofPrecision, true);
		LocalTime effectiveMinTime = DefaultLocalTimeArbitrary
										 .calculateEffectiveMin(timeBetween, hourBetween, minuteBetween, secondBetween, ofPrecision);
		LocalTime effectiveMaxTime = DefaultLocalTimeArbitrary
										 .calculateEffectiveMax(timeBetween, hourBetween, minuteBetween, secondBetween, ofPrecision);
		if (effectiveMinTime.isAfter(effectiveMaxTime)) {
			throw new IllegalArgumentException("These min/max values cannot be used with these time min/max values");
		}
		effective = calculateEffectiveMinWithMinTime(effective, effectiveMinTime, effectiveMaxTime);
		return effective;
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

	private LocalDateTime calculateEffectiveMinWithMinTime(LocalDateTime effective, LocalTime minTime, LocalTime maxTime) {
		if (effective.toLocalTime().isBefore(minTime)) {
			return LocalDateTime.of(effective.toLocalDate(), minTime);
		} else if (effective.toLocalTime().isAfter(maxTime)) {
			return LocalDateTime.of(effective.toLocalDate().plusDays(1), minTime);
		}
		return effective;
	}

	private LocalDateTime calculateEffectiveMax(LocalDateTime effectiveMin) {
		LocalDateTime effective = calculateEffectiveMaxDate(dateTimeBetween.getMax());
		effective = effective != null ? effective : LocalDateTime.of(
			DEFAULT_MAX.toLocalDate(), ofPrecision.calculateMaxPossibleLocalTime()
		);
		DefaultLocalTimeArbitrary.checkTimeValueAndPrecision(effective.toLocalTime(), ofPrecision, false);
		LocalTime effectiveMinTime = DefaultLocalTimeArbitrary
										 .calculateEffectiveMin(timeBetween, hourBetween, minuteBetween, secondBetween, ofPrecision);
		LocalTime effectiveMaxTime = DefaultLocalTimeArbitrary
										 .calculateEffectiveMax(timeBetween, hourBetween, minuteBetween, secondBetween, ofPrecision);
		effective = calculateEffectiveMaxWithMaxTime(effective, effectiveMinTime, effectiveMaxTime);
		if (effectiveMin.isAfter(effective)) {
			throw new IllegalArgumentException("These date time min/max values cannot be used with these date min/max values");
		}
		return effective;
	}

	private LocalDateTime calculateEffectiveMaxDate(LocalDateTime effective) {
		if (dateBetween.getMax() == null) {
			return effective;
		} else if (effective == null || dateBetween.getMax().isBefore(effective.toLocalDate())) {
			return LocalDateTime.of(dateBetween.getMax(), ofPrecision.calculateMaxPossibleLocalTime());
		} else {
			return effective;
		}
	}

	private LocalDateTime calculateEffectiveMaxWithMaxTime(LocalDateTime effective, LocalTime minTime, LocalTime maxTime) {
		if (effective.toLocalTime().isAfter(maxTime)) {
			return LocalDateTime.of(effective.toLocalDate(), maxTime);
		} else if (effective.toLocalTime().isBefore(minTime)) {
			return LocalDateTime.of(effective.toLocalDate().minusDays(1), minTime);
		}
		return effective;
	}

	private TimeArbitraries generateTimeArbitraries(LocalDateTime effectiveMin, LocalDateTime effectiveMax, OfPrecision ofPrecision) {
		boolean oneDay = false;
		TimeArbitraries times = new TimeArbitraries();
		if (effectiveMin.toLocalDate().isEqual(effectiveMax.toLocalDate())) {
			oneDay = true;
			times.firstDay = Times.times().between(effectiveMin.toLocalTime(), effectiveMax.toLocalTime()).ofPrecision(ofPrecision.get());
		} else {
			times.firstDay = Times.times().atTheEarliest(effectiveMin.toLocalTime()).ofPrecision(ofPrecision.get());
			times.daysBetween = Times.times().ofPrecision(ofPrecision.get());
			times.lastDay = Times.times().atTheLatest(effectiveMax.toLocalTime()).ofPrecision(ofPrecision.get());
		}
		setTimeParams(times, oneDay);
		return times;
	}

	private void setTimeParams(TimeArbitraries times, boolean oneDay) {
		if (timeBetween.getMin() != null && timeBetween.getMax() != null) {
			if (!oneDay) {
				times.firstDay = times.firstDay.atTheLatest(timeBetween.getMax());
				times.daysBetween = times.daysBetween.between(timeBetween.getMin(), timeBetween.getMax());
				times.lastDay = times.lastDay.atTheEarliest(timeBetween.getMin());
			}
		}
		times.firstDay = times.firstDay.hourBetween(hourBetween.getMin(), hourBetween.getMax())
									   .minuteBetween(minuteBetween.getMin(), minuteBetween.getMax())
									   .secondBetween(secondBetween.getMin(), secondBetween.getMax());
		if (!oneDay) {
			times.daysBetween = times.daysBetween.hourBetween(hourBetween.getMin(), hourBetween.getMax())
												 .minuteBetween(minuteBetween.getMin(), minuteBetween.getMax())
												 .secondBetween(secondBetween.getMin(), secondBetween.getMax());
			times.lastDay = times.lastDay.hourBetween(hourBetween.getMin(), hourBetween.getMax())
										 .minuteBetween(minuteBetween.getMin(), minuteBetween.getMax())
										 .secondBetween(secondBetween.getMin(), secondBetween.getMax());
		}
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
		MonthBetween monthBetween = (MonthBetween) new MonthBetween().set(min, max);
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.allowedMonths.set(monthBetween);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary onlyMonths(Month... months) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.allowedMonths.set(months);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary dayOfMonthBetween(int min, int max) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.dayOfMonthBetween.set(min, max);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.allowedDayOfWeeks.set(daysOfWeek);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary timeBetween(LocalTime min, LocalTime max) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.timeBetween.set(min, max);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary hourBetween(int min, int max) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.hourBetween.set(min, max);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary minuteBetween(int min, int max) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.minuteBetween.set(min, max);
		return clone;
	}

	@Override
	public LocalDateTimeArbitrary secondBetween(int min, int max) {
		DefaultLocalDateTimeArbitrary clone = typedClone();
		clone.secondBetween.set(min, max);
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
