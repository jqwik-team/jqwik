package net.jqwik.time;

import java.time.*;
import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultYearMonthArbitrary extends ArbitraryDecorator<YearMonth> implements YearMonthArbitrary {


	private static final YearMonth DEFAULT_MIN = YearMonth.of(1900, 1);
	private static final YearMonth DEFAULT_MAX = YearMonth.of(2500, 12);

	private YearMonth yearMonthMin = null;
	private YearMonth yearMonthMax = null;

	private Set<Month> allowedMonths = new HashSet<>(Arrays.asList(Month.values()));

	@Override
	protected Arbitrary<YearMonth> arbitrary() {

		YearMonth effectiveMin = yearMonthMin == null ? DEFAULT_MIN : yearMonthMin;
		YearMonth effectiveMax = yearMonthMax == null ? DEFAULT_MAX : yearMonthMax;

		long months = MONTHS.between(effectiveMin, effectiveMax);

		Arbitrary<Long> month =
				Arbitraries.longs()
						   .between(0, months)
						   .withDistribution(RandomDistribution.uniform())
						   .edgeCases(edgeCases -> edgeCases.includeOnly(0L, months));

		Arbitrary<YearMonth> yearMonths = month.map(effectiveMin::plusMonths);

		if (allowedMonths.size() < 12) {
			yearMonths = yearMonths.filter(yearMonth -> allowedMonths.contains(yearMonth.getMonth()));
		}

		return yearMonths;
	}

	@Override
	public YearMonthArbitrary atTheEarliest(YearMonth min) {
		if (min.getYear() <= 0) {
			throw new IllegalArgumentException("Minimum year in a year-month must be > 0");
		}
		if ((yearMonthMax != null) && min.isAfter(yearMonthMax)) {
			throw new IllegalArgumentException("Minimum year-month must not be after maximum year-month");
		}
		DefaultYearMonthArbitrary clone = typedClone();
		clone.yearMonthMin = min;
		return clone;
	}

	@Override
	public YearMonthArbitrary atTheLatest(YearMonth max) {
		if (max.getYear() <= 0) {
			throw new IllegalArgumentException("Maximum year in a date year-month be > 0");
		}
		if ((yearMonthMin != null) && max.isBefore(yearMonthMin)) {
			throw new IllegalArgumentException("Maximum year-month must not be before minimum year-month");
		}
		DefaultYearMonthArbitrary clone = typedClone();
		clone.yearMonthMax = max;
		return clone;
	}

	@Override
	public YearMonthArbitrary yearBetween(Year min, Year max) {
		if (!min.isBefore(max)) {
			Year remember = min;
			min = max;
			max = remember;
		}

		YearMonth minDate = YearMonth.of(min.getValue(), 1);
		YearMonth maxDate = YearMonth.of(max.getValue(), 12);
		return between(minDate, maxDate);
	}

	@Override
	public YearMonthArbitrary monthBetween(Month min, Month max) {
		if (min.compareTo(max) > 0) {
			throw new IllegalArgumentException("Minimum month cannot be after maximum month");
		}

		DefaultYearMonthArbitrary clone = typedClone();
		clone.allowedMonths = Arrays.stream(Month.values())
									.filter(m -> m.compareTo(min) >= 0 && m.compareTo(max) <= 0)
									.collect(Collectors.toSet());
		return clone;
	}

	@Override
	public YearMonthArbitrary onlyMonths(Month... months) {
		DefaultYearMonthArbitrary clone = typedClone();
		clone.allowedMonths = new HashSet<>(Arrays.asList(months));
		return clone;
	}

}
