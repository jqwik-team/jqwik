package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.valueRanges.*;

import static java.time.Month.*;
import static java.time.temporal.ChronoUnit.*;
import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultLocalDateArbitrary extends ArbitraryDecorator<LocalDate> implements LocalDateArbitrary {

	public static final LocalDate DEFAULT_MIN_DATE = LocalDate.of(1900, 1, 1);
	public static final LocalDate DEFAULT_MAX_DATE = LocalDate.of(2500, 12, 31);

	private final LocalDateBetween dateBetween = new LocalDateBetween();
	private final DayOfMonthBetween dayOfMonthBetween = new DayOfMonthBetween();
	private final AllowedMonths allowedMonths = new AllowedMonths();
	private final AllowedDayOfWeeks allowedDayOfWeeks = new AllowedDayOfWeeks();

	@Override
	protected Arbitrary<LocalDate> arbitrary() {

		LocalDate effectiveMin = effectiveMinDate();
		LocalDate effectiveMax = effectiveMaxDate(effectiveMin);

		checkIfValuesArePossible(effectiveMin, effectiveMax);

		long days = DAYS.between(effectiveMin, effectiveMax);

		Arbitrary<Long> day =
			Arbitraries.longs()
					   .between(0, days)
					   .withDistribution(RandomDistribution.uniform())
					   .edgeCases(edgeCases -> {
						   edgeCases.includeOnly(0L, days);
						   Optional<Long> optionalLeapDay = firstLeapDayAfter(effectiveMin, days);
						   optionalLeapDay.ifPresent(edgeCases::add);
					   });

		Arbitrary<LocalDate> localDates = day.map(effectiveMin::plusDays);

		if (allowedMonths.get().size() < 12) {
			localDates = localDates.filter(date -> allowedMonths.get().contains(date.getMonth()));
		}

		if (allowedDayOfWeeks.get().size() < 7) {
			localDates = localDates.filter(date -> allowedDayOfWeeks.get().contains(date.getDayOfWeek()));
		}

		if (dayOfMonthBetween.getMax() != null
				&& dayOfMonthBetween.getMin() != null
				&& dayOfMonthBetween.getMax() - dayOfMonthBetween.getMin() != 30
		) {
			localDates = localDates.filter(date ->
											   date.getDayOfMonth() >= dayOfMonthBetween.getMin()
												   && date.getDayOfMonth() <= dayOfMonthBetween.getMax()
			);
		}

		return localDates;

	}

	private void checkIfValuesArePossible(LocalDate min, LocalDate max) {
		//TODO: Methode muss getestet werden!!!!! - Besonderer Testfall: z.B. Februar erlaubt, aber nur 31.
		if (max.getYear() - 2 >= min.getYear()) {
			return;
		}
		for (LocalDate date = min; !date.isAfter(max); date = date.plusDays(1)) {
			if (allowedMonths.get().contains(date.getMonth())) {
				return;
			}
		}
		throw new IllegalArgumentException("These min/max configurations cannot be used together: No values are possible.");
	}

	private LocalDate effectiveMaxDate(LocalDate effectiveMin) {
		LocalDate effective = dateBetween.getMax() == null ? DEFAULT_MAX_DATE : dateBetween.getMax();
		int earliestMonth = earliestAllowedMonth();
		int latestMonth = latestAllowedMonth();
		if (earliestMonth > effective.getMonth().getValue()) {
			effective = effective.withMonth(12).withDayOfMonth(31).minusYears(1);
		}
		if (latestMonth < effective.getMonth().getValue()) {
			effective = effective.withMonth(latestMonth + 1);
			effective = effective.minusDays(effective.getDayOfMonth());
		}
		if (dayOfMonthBetween.getMax() != null && dayOfMonthBetween.getMax() < effective.getDayOfMonth()) {
			effective = effective.withDayOfMonth(dayOfMonthBetween.getMax());
		}
		if (effectiveMin.isAfter(effective)) {
			throw new IllegalArgumentException("These min/max configurations cannot be used together: No values are possible.");
		}
		return effective;
	}

	private int latestAllowedMonth() {
		return allowedMonths.get()
							.stream()
							.sorted((m1, m2) -> -Integer.compare(m1.getValue(), m2.getValue()))
							.map(Month::getValue)
							.findFirst().orElse(12);
	}

	private LocalDate effectiveMinDate() {
		LocalDate effective = dateBetween.getMin() == null ? DEFAULT_MIN_DATE : dateBetween.getMin();
		int earliestMonth = earliestAllowedMonth();
		int latestMonth = latestAllowedMonth();
		if (latestMonth < effective.getMonth().getValue()) {
			effective = effective.withDayOfMonth(1).withMonth(1).plusYears(1);
		}
		if (earliestMonth > effective.getMonth().getValue()) {
			effective = effective.withDayOfMonth(1).withMonth(earliestMonth);
		}
		if (dayOfMonthBetween.getMin() != null && dayOfMonthBetween.getMin() > effective.getDayOfMonth()) {
			if (isValidDate(effective.getYear(), effective.getMonth(), dayOfMonthBetween.getMin())) {
				effective = effective.withDayOfMonth(dayOfMonthBetween.getMin());
			} else {
				effective = effective.plusMonths(1).withDayOfMonth(dayOfMonthBetween.getMin());
			}
		}
		return effective;
	}

	private boolean isValidDate(int year, Month month, int dayOfMonth) {
		return !(
			(dayOfMonth > 31 || dayOfMonth < 1)
				|| (dayOfMonth == 31 && (month == FEBRUARY || month == APRIL || month == JUNE || month == SEPTEMBER || month == NOVEMBER))
				|| (dayOfMonth == 30 && month == FEBRUARY)
				|| (dayOfMonth == 29 && month == FEBRUARY && !isLeapYear(year))
		);
	}

	private boolean isLeapYear(int year) {
		return new GregorianCalendar().isLeapYear(year);
	}

	private int earliestAllowedMonth() {
		return allowedMonths.get()
							.stream()
							.sorted(Comparator.comparing(Month::getValue))
							.map(Month::getValue)
							.findFirst().orElse(1);
	}

	private Optional<Long> firstLeapDayAfter(LocalDate date, long maxOffset) {
		long offset = nextLeapDayOffset(date, 0);
		if (offset > maxOffset) {
			return Optional.empty();
		} else {
			return Optional.of(offset);
		}
	}

	private long nextLeapDayOffset(LocalDate date, long base) {
		if (date.isLeapYear()) {
			if (date.getMonth().compareTo(FEBRUARY) <= 0) {
				LocalDate leapDaySameYear = date.withMonth(2).withDayOfMonth(29);
				long offset = DAYS.between(date, leapDaySameYear);
				return base + offset;
			}
		}
		int nextYear = date.getYear() + 1;
		if (nextYear > Year.MAX_VALUE) {
			return Long.MAX_VALUE;
		}
		LocalDate nextJan1 = LocalDate.of(nextYear, 1, 1);

		return nextLeapDayOffset(nextJan1, base + DAYS.between(date, nextJan1));
	}

	@Override
	public LocalDateArbitrary atTheEarliest(LocalDate min) {
		DefaultLocalDateArbitrary clone = typedClone();
		clone.dateBetween.set(min, null);
		return clone;
	}

	@Override
	public LocalDateArbitrary atTheLatest(LocalDate max) {
		DefaultLocalDateArbitrary clone = typedClone();
		clone.dateBetween.set(null, max);
		return clone;
	}

	@Override
	public LocalDateArbitrary yearBetween(Year min, Year max) {
		YearBetween yearBetween = (YearBetween) new YearBetween().set(min, max);
		DefaultLocalDateArbitrary clone = typedClone();
		clone.dateBetween.setYearBetween(yearBetween);
		return clone;
	}

	@Override
	public LocalDateArbitrary monthBetween(Month min, Month max) {
		MonthBetween monthBetween = (MonthBetween) new MonthBetween().set(min, max);
		DefaultLocalDateArbitrary clone = typedClone();
		clone.allowedMonths.set(monthBetween);
		return clone;
	}

	@Override
	public LocalDateArbitrary onlyMonths(Month... months) {
		DefaultLocalDateArbitrary clone = typedClone();
		clone.allowedMonths.set(months);
		return clone;
	}

	@Override
	public LocalDateArbitrary dayOfMonthBetween(int min, int max) {
		DefaultLocalDateArbitrary clone = typedClone();
		clone.dayOfMonthBetween.set(min, max);
		return clone;
	}

	@Override
	public LocalDateArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		DefaultLocalDateArbitrary clone = typedClone();
		clone.allowedDayOfWeeks.set(daysOfWeek);
		return clone;
	}

}
