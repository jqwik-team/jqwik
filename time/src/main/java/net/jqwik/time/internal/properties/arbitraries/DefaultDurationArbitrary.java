package net.jqwik.time.internal.properties.arbitraries;

import java.math.*;
import java.time.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultDurationArbitrary extends ArbitraryDecorator<Duration> implements DurationArbitrary {

	public static final Duration DEFAULT_MIN = Duration.ofSeconds(Long.MIN_VALUE, 0);
	public static final Duration DEFAULT_MAX = Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);

	private Duration min = DEFAULT_MIN;
	private Duration max = DEFAULT_MAX;
	private ChronoUnit ofPrecision = SECONDS;

	@Override
	protected Arbitrary<Duration> arbitrary() {

		Duration effectiveMin = calculateEffectiveMin();
		Duration effectiveMax = calculateEffectiveMax();

		BigInteger min = calculateValue(effectiveMin);
		BigInteger max = calculateValue(effectiveMax);

		//TODO: min > max

		Arbitrary<BigInteger> bigIntegers = Arbitraries.bigIntegers()
													   .between(min, max)
													   .withDistribution(RandomDistribution.uniform())
													   .edgeCases(edgeCases -> edgeCases.includeOnly(min, BigInteger.ZERO, max));

		return bigIntegers.map(this::calculateDuration);

	}

	private Duration calculateEffectiveMin() {
		try {
			Duration effective = min;
			int compareVal = calculateCompareValue();
			if (compareVal >= 1) {
				if (effective.getNano() % 1_000 != 0) {
					effective = effective.plusNanos(1_000 - (effective.getNano() % 1_000));
				}
				if (compareVal >= 2) {
					if (effective.getNano() % 1_000_000 != 0) {
						effective = effective.plusNanos(1_000_000 - (effective.getNano() % 1_000_000));
					}
					if (compareVal >= 3) {
						if (effective.getNano() != 0) {
							effective = effective.plusNanos(1_000_000_000 - effective.getNano());
						}
						if (compareVal >= 4) {
							int seconds = (int) (effective.getSeconds() % 60);
							if (seconds < 0) {
								seconds += 60;
							}
							if (seconds != 0) {
								effective = effective.plusSeconds(60 - seconds);
							}
							if (compareVal >= 5) {
								int minutes = (int) ((effective.getSeconds() % 3600) / 60);
								if (minutes < 0) {
									minutes += 60;
								}
								if (minutes != 0) {
									effective = effective.plusMinutes(60 - minutes);
								}
							}
						}
					}
				}
			}
			return effective;
		} catch (ArithmeticException e) {
			throw new IllegalArgumentException("Min value must be increased but results in a " + e.getMessage());
		}
	}

	private Duration calculateEffectiveMax() {
		try {
			Duration effective = max;
			int compareVal = calculateCompareValue();
			if (compareVal >= 1) {
				if (effective.getNano() % 1_000 != 0) {
					effective = effective.plusNanos(-(effective.getNano() % 1_000));
				}
				if (compareVal >= 2) {
					if (effective.getNano() % 1_000_000 != 0) {
						effective = effective.plusNanos(-(effective.getNano() % 1_000_000));
					}
					if (compareVal >= 3) {
						if (effective.getNano() != 0) {
							effective = effective.plusNanos(-effective.getNano());
						}
						if (compareVal >= 4) {
							int seconds = (int) (effective.getSeconds() % 60);
							if (seconds < 0) {
								seconds += 60;
							}
							if (seconds != 0) {
								effective = effective.plusSeconds(-seconds);
							}
							if (compareVal >= 5) {
								int minutes = (int) ((effective.getSeconds() % 3600) / 60);
								if (minutes < 0) {
									minutes += 60;
								}
								if (minutes != 0) {
									effective = effective.plusMinutes(-minutes);
								}
							}
						}
					}
				}
			}
			return effective;
		} catch (ArithmeticException e) {
			throw new IllegalArgumentException("Max value must be decreased but results in a " + e.getMessage());
		}
	}

	private int calculateCompareValue() {
		switch (ofPrecision) {
			case HOURS:
				return 5;
			case MINUTES:
				return 4;
			case SECONDS:
				return 3;
			case MILLIS:
				return 2;
			case MICROS:
				return 1;
			default:
				return 0;
		}
	}

	private BigInteger calculateValue(Duration effective) {

		int compareValue = calculateCompareValue();

		BigInteger helperMultiply = new BigInteger(1_000_000_000 + "");
		BigInteger helperDivide1000 = new BigInteger(1_000 + "");
		BigInteger helperDivide60 = new BigInteger("60");

		BigInteger bigInteger = new BigInteger(effective.getSeconds() + "");
		bigInteger = bigInteger.multiply(helperMultiply);
		bigInteger = bigInteger.add(new BigInteger(effective.getNano() + ""));

		if (compareValue >= 1) {
			bigInteger = bigInteger.divide(helperDivide1000);
			if (compareValue >= 2) {
				bigInteger = bigInteger.divide(helperDivide1000);
				if (compareValue >= 3) {
					bigInteger = bigInteger.divide(helperDivide1000);
					if (compareValue >= 4) {
						bigInteger = bigInteger.divide(helperDivide60);
						if (compareValue >= 5) {
							bigInteger = bigInteger.divide(helperDivide60);
						}
					}
				}
			}
		}

		return bigInteger;

	}

	private Duration calculateDuration(BigInteger bigInteger) {

		BigInteger helperDivide = new BigInteger(1_000_000_000 + "");
		BigInteger helperMultiply1000 = new BigInteger(1_000 + "");
		BigInteger helperMultiply60 = new BigInteger("60");

		switch (ofPrecision) {
			case HOURS:
				bigInteger = bigInteger.multiply(helperMultiply60);
			case MINUTES:
				bigInteger = bigInteger.multiply(helperMultiply60);
			case SECONDS:
				bigInteger = bigInteger.multiply(helperMultiply1000);
			case MILLIS:
				bigInteger = bigInteger.multiply(helperMultiply1000);
			case MICROS:
				bigInteger = bigInteger.multiply(helperMultiply1000);
		}

		BigInteger bigIntegerSeconds = bigInteger.divide(helperDivide);
		long seconds = bigIntegerSeconds.longValue();
		int nanos = bigInteger.subtract(bigIntegerSeconds.multiply(helperDivide)).intValue();

		return Duration.ofSeconds(seconds, nanos);

	}

	@Override
	public DurationArbitrary between(Duration min, Duration max) {
		if (min.compareTo(max) > 0) {
			Duration remember = min;
			min = max;
			max = remember;
		}
		DefaultDurationArbitrary clone = typedClone();
		clone.min = min;
		clone.max = max;
		return clone;
	}

	@Override
	public DurationArbitrary ofPrecision(ChronoUnit ofPrecision) {
		if (!(ofPrecision.equals(HOURS) || ofPrecision.equals(MINUTES) || ofPrecision.equals(SECONDS) || ofPrecision
																												 .equals(MILLIS) || ofPrecision
																																			.equals(MICROS) || ofPrecision
																																									   .equals(NANOS))) {
			throw new IllegalArgumentException("Precision value must be one of these ChronoUnit values: HOURS, MINUTES, SECONDS, MILLIS, MICROS, NANOS");
		}

		DefaultDurationArbitrary clone = typedClone();
		clone.ofPrecision = ofPrecision;
		return clone;
	}

}
