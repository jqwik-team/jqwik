package net.jqwik.time;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultDateArbitrary extends ArbitraryDecorator<LocalDate> implements DateArbitrary {

	private static final LocalDate DEFAULT_MIN_DATE = LocalDate.of(1900, 1, 1);
	private static final LocalDate DEFAULT_MAX_DATE = LocalDate.of(2500, 12, 31);

	private LocalDate dateMin = null;
	private LocalDate dateMax = null;

	private Set<Month> allowedMonths = new HashSet<>(Arrays.asList(Month.values()));
	private Set<DayOfWeek> allowedDayOfWeeks = new HashSet<>(Arrays.asList(DayOfWeek.values()));

	private int dayOfMonthMin = 1;
	private int dayOfMonthMax = 31;

	@Override
	protected Arbitrary<LocalDate> arbitrary() {

		LocalDate effectiveMin = dateMin == null ? DEFAULT_MIN_DATE : dateMin;
		LocalDate effectiveMax = dateMax == null ? DEFAULT_MAX_DATE : dateMax;

		long days = ChronoUnit.DAYS.between(effectiveMin, effectiveMax);

		Arbitrary<Long> day =
				Arbitraries.longs()
						   .between(0, days)
						   .withDistribution(RandomDistribution.uniform())
						   .edgeCases(edgeCases -> edgeCases.includeOnly(0L, days));
		Arbitrary<LocalDate> localDates = day.map(effectiveMin::plusDays);

		if (allowedMonths.size() < 12) {
			localDates = localDates.filter(date -> allowedMonths.contains(date.getMonth()));
		}

		if (allowedDayOfWeeks.size() < 7) {
			localDates = localDates.filter(date -> allowedDayOfWeeks.contains(date.getDayOfWeek()));
		}

		if (dayOfMonthMax - dayOfMonthMin != 30) {
			localDates = localDates.filter(date -> date.getDayOfMonth() >= dayOfMonthMin && date.getDayOfMonth() <= dayOfMonthMax);
		}

		return localDates;

	}

	@Override
	public DateArbitrary atTheEarliest(LocalDate min) {
		if (min.getYear() <= 0) {
			throw new IllegalArgumentException("Minimum year in a date must be > 0");
		}
		if ((dateMax != null) && min.isAfter(dateMax)) {
			throw new IllegalArgumentException("Minimum date must not be after maximum date");
		}
		DefaultDateArbitrary clone = typedClone();
		clone.dateMin = min;
		return clone;
	}

	@Override
	public DateArbitrary atTheLatest(LocalDate max) {
		if (max.getYear() <= 0) {
			throw new IllegalArgumentException("Maximum year in a date must be > 0");
		}
		if ((dateMin != null) && max.isBefore(dateMin)) {
			throw new IllegalArgumentException("Maximum date must not be before minimum date");
		}

		DefaultDateArbitrary clone = typedClone();
		clone.dateMax = max;
		return clone;
	}

	@Override
	public DateArbitrary yearBetween(Year min, Year max) {
		if (!min.isBefore(max)) {
			Year remember = min;
			min = max;
			max = remember;
		}

		LocalDate minDate = LocalDate.of(min.getValue(), 1, 1);
		LocalDate maxDate = LocalDate.of(max.getValue(), 12, 31);
		return between(minDate, maxDate);
	}

	@Override
	public DateArbitrary monthBetween(Month min, Month max) {
		if (min.compareTo(max) > 0) {
			throw new IllegalArgumentException("Minimum month cannot be after maximum month");
		}

		DefaultDateArbitrary clone = typedClone();
		clone.allowedMonths = Arrays.stream(Month.values())
									.filter(m -> m.compareTo(min) >= 0 && m.compareTo(max) <= 0)
									.collect(Collectors.toSet());
		return clone;
	}

	@Override
	public DateArbitrary onlyMonths(Month... months) {
		DefaultDateArbitrary clone = typedClone();
		clone.allowedMonths = new HashSet<>(Arrays.asList(months));
		return clone;
	}

	@Override
	public DateArbitrary dayOfMonthBetween(int min, int max) {
		if (min >max) {
			int remember = min;
			min = max;
			max = remember;
		}
		DefaultDateArbitrary clone = typedClone();
		clone.dayOfMonthMin = Math.max(1, min);
		clone.dayOfMonthMax = Math.min(31, max);
		return clone;
	}

	@Override
	public DateArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		DefaultDateArbitrary clone = typedClone();
		clone.allowedDayOfWeeks = new HashSet<>(Arrays.asList(daysOfWeek));
		return clone;
	}

}
