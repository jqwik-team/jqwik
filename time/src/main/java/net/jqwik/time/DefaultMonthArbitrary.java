package net.jqwik.time;

import java.time.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultMonthArbitrary extends ArbitraryDecorator<Month> implements MonthArbitrary {

	private Month min = Month.JANUARY;
	private Month max = Month.DECEMBER;
	private Month[] allowedMonths = new Month[]{Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER};

	@Override
	protected Arbitrary<Month> arbitrary() {
		List<Month> values = Arrays.asList(Month.class.getEnumConstants());
		final int indexMin = values.indexOf(min);
		final int indexMax = values.indexOf(max);

		return Arbitraries.integers()
						  .between(indexMin, indexMax)
						  .edgeCases(integerConfig -> integerConfig.includeOnly(indexMin, indexMax))
						  .map(values::get)
						  .filter(this::isInAllowedMonths);
	}

	private boolean isInAllowedMonths(Month month) {
		if (allowedMonths == null) {
			return false;
		}
		for (Month m : allowedMonths) {
			if (m.equals(month)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public MonthArbitrary between(Month min, Month max) {
		DefaultMonthArbitrary clone = typedClone();
		clone.min = min;
		clone.max = max;
		return clone;
	}

	@Override
	public MonthArbitrary only(Month... months) {
		DefaultMonthArbitrary clone = typedClone();
		clone.allowedMonths = months;
		return clone;
	}

}
