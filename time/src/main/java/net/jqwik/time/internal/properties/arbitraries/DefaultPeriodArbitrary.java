package net.jqwik.time.internal.properties.arbitraries;

import java.math.*;
import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultPeriodArbitrary extends ArbitraryDecorator<Period> implements PeriodArbitrary {

	private int yearsMin = Integer.MIN_VALUE;
	private int yearsMax = Integer.MAX_VALUE;
	private BigInteger yearPeriod = null;

	private int monthsMin = 0;
	private int monthsMax = 11;
	private BigInteger monthPeriod = null;

	private int daysMin = 0;
	private int daysMax = 30;
	private BigInteger dayPeriod = null;

	private BigInteger helperMonthDay = null;

	@Override
	protected Arbitrary<Period> arbitrary() {

		BigInteger bigIntegerStart = BigInteger.ZERO;
		BigInteger bigIntegerEnd = calculateBigIntegerEnd();

		Arbitrary<BigInteger> numbers = Arbitraries.bigIntegers()
												   .withDistribution(RandomDistribution.uniform())
												   .between(bigIntegerStart, bigIntegerEnd)
												   .edgeCases(edgeCases -> {
													   edgeCases.includeOnly(bigIntegerStart, bigIntegerEnd);
												   });

		Arbitrary<Period> periodArbitrary = numbers.map(this::calculatePeriod);

		periodArbitrary = periodArbitrary.edgeCases(edgeCases -> {
			edgeCases.includeOnly(Period.of(yearsMin, monthsMin, daysMin), Period.of(yearsMax, monthsMax, daysMax));
			if (yearsMin <= 0 && yearsMax >= 0 && monthsMin <= 0 && monthsMax >= 0 && daysMin <= 0 && daysMax >= 0) {
				edgeCases.add(Period.of(0, 0, 0));
			}
		});

		return periodArbitrary;

	}

	private Period calculatePeriod(BigInteger bigInteger) {

		int years = yearsMin;
		int months = monthsMin;
		int days = daysMin;

		BigInteger yearAdd = bigInteger.divide(helperMonthDay);
		years += yearAdd.intValue();

		BigInteger monthDay = bigInteger.subtract(yearAdd.multiply(helperMonthDay));

		BigInteger monthAdd = monthDay.divide(dayPeriod);
		months += monthAdd.intValue();

		BigInteger dayAdd = monthDay.subtract(monthAdd.multiply(dayPeriod));
		days += dayAdd.intValue();

		return Period.of(years, months, days);

	}

	private BigInteger calculateBigIntegerEnd() {

		BigInteger bigInteger = BigInteger.ONE;

		yearPeriod = BigInteger.ONE;
		yearPeriod = yearPeriod.add(new BigInteger(yearsMax + ""));
		yearPeriod = yearPeriod.subtract(new BigInteger(yearsMin + ""));
		bigInteger = bigInteger.multiply(yearPeriod);

		monthPeriod = BigInteger.ONE;
		monthPeriod = monthPeriod.add(new BigInteger(monthsMax + ""));
		monthPeriod = monthPeriod.subtract(new BigInteger(monthsMin + ""));
		bigInteger = bigInteger.multiply(monthPeriod);

		dayPeriod = BigInteger.ONE;
		dayPeriod = dayPeriod.add(new BigInteger(daysMax + ""));
		dayPeriod = dayPeriod.subtract(new BigInteger(daysMin + ""));
		bigInteger = bigInteger.multiply(dayPeriod);

		bigInteger = bigInteger.subtract(BigInteger.ONE);

		helperMonthDay = monthPeriod.multiply(dayPeriod);

		return bigInteger;

	}

	@Override
	public PeriodArbitrary yearsBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}
		DefaultPeriodArbitrary clone = typedClone();
		clone.yearsMin = min;
		clone.yearsMax = max;
		return clone;
	}

	@Override
	public PeriodArbitrary monthsBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}
		DefaultPeriodArbitrary clone = typedClone();
		clone.monthsMin = min;
		clone.monthsMax = max;
		return clone;
	}

	@Override
	public PeriodArbitrary daysBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}
		DefaultPeriodArbitrary clone = typedClone();
		clone.daysMin = min;
		clone.daysMax = max;
		return clone;
	}

}
