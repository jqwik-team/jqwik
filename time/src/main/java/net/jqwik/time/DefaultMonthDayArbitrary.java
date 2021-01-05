package net.jqwik.time;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultMonthDayArbitrary extends ArbitraryDecorator<MonthDay> implements MonthDayArbitrary {

	private static final int YEAR = 2000;

	private DateArbitrary dateArbitrary = Dates.dates().yearBetween(YEAR, YEAR);

	@Override
	protected Arbitrary<MonthDay> arbitrary() {
		return dateArbitrary.map(v -> MonthDay.of(v.getMonth(), v.getDayOfMonth()));
	}

	@Override
	public MonthDayArbitrary atTheEarliest(MonthDay min) {
		DefaultMonthDayArbitrary clone = typedClone();
		LocalDate minDate = LocalDate.of(YEAR, min.getMonth(), min.getDayOfMonth());
		clone.dateArbitrary = dateArbitrary.atTheEarliest(minDate);
		return clone;
	}

	@Override
	public MonthDayArbitrary atTheLatest(MonthDay max) {
		DefaultMonthDayArbitrary clone = typedClone();
		LocalDate maxDate = LocalDate.of(YEAR, max.getMonth(), max.getDayOfMonth());
		clone.dateArbitrary = dateArbitrary.atTheLatest(maxDate);
		return clone;
	}

	@Override
	public MonthDayArbitrary monthBetween(Month min, Month max) {
		DefaultMonthDayArbitrary clone = typedClone();
		clone.dateArbitrary = dateArbitrary.monthBetween(min, max);
		return clone;
	}

	@Override
	public MonthDayArbitrary onlyMonths(Month... months) {
		DefaultMonthDayArbitrary clone = typedClone();
		clone.dateArbitrary = dateArbitrary.onlyMonths(months);
		return clone;
	}

	@Override
	public MonthDayArbitrary dayOfMonthBetween(int min, int max) {
		DefaultMonthDayArbitrary clone = typedClone();
		clone.dateArbitrary = dateArbitrary.dayOfMonthBetween(min, max);
		return clone;
	}

}
