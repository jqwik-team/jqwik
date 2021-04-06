package net.jqwik.time.internal.properties.arbitraries;

import java.math.*;
import java.time.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.valueRanges.*;

import static java.time.temporal.ChronoUnit.*;
import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultDurationArbitrary extends ArbitraryDecorator<Duration> implements DurationArbitrary {

	public static final Duration DEFAULT_MIN = Duration.ofSeconds(Long.MIN_VALUE, 0);
	public static final Duration DEFAULT_MAX = Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);

	private final DurationBetween durationBetween = new DurationBetween();
	private final OfPrecision ofPrecision = new OfPrecision();

	@Override
	protected Arbitrary<Duration> arbitrary() {

		Duration min = durationBetween.getMin() != null ? durationBetween.getMin() : DEFAULT_MIN;
		Duration max = durationBetween.getMax() != null ? durationBetween.getMax() : DEFAULT_MAX;

		Duration effectiveMin = calculateEffectiveMin(min, ofPrecision);
		Duration effectiveMax = calculateEffectiveMax(max, ofPrecision);

		if (effectiveMin.compareTo(effectiveMax) > 0) {
			throw new IllegalArgumentException("The maximum duration is to soon after the minimum duration.");
		}

		BigInteger bigIntegerMin = calculateValue(effectiveMin, ofPrecision);
		BigInteger bigIntegerMax = calculateValue(effectiveMax, ofPrecision);

		Arbitrary<BigInteger> bigIntegers = Arbitraries.bigIntegers()
													   .between(bigIntegerMin, bigIntegerMax)
													   .withDistribution(RandomDistribution.uniform())
													   .edgeCases(edgeCases -> edgeCases
																				   .includeOnly(bigIntegerMin, BigInteger.ZERO, bigIntegerMax));

		return bigIntegers.map(big -> calculateDuration(big, ofPrecision));

	}

	@SuppressWarnings("OverlyComplexMethod")
	static private Duration calculateEffectiveMin(Duration min, OfPrecision ofPrecision) {
		ChronoUnit precision = ofPrecision.get();
		try {
			Duration effective = min;
			if (precision.compareTo(NANOS) >= 1) {
				if (effective.getNano() % 1_000 != 0) {
					effective = effective.plusNanos(1_000 - (effective.getNano() % 1_000));
				}
				if (precision.compareTo(MICROS) >= 1) {
					if (effective.getNano() % 1_000_000 != 0) {
						effective = effective.plusNanos(1_000_000 - (effective.getNano() % 1_000_000));
					}
					if (precision.compareTo(MILLIS) >= 1) {
						if (effective.getNano() != 0) {
							effective = effective.plusNanos(1_000_000_000 - effective.getNano());
						}
						if (precision.compareTo(SECONDS) >= 1) {
							int seconds = (int) (effective.getSeconds() % 60);
							if (seconds < 0) {
								seconds += 60;
							}
							if (seconds != 0) {
								effective = effective.plusSeconds(60 - seconds);
							}
							if (precision.compareTo(MINUTES) >= 1) {
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
			throw new IllegalArgumentException("Min value must be increased because of precision " + precision + " but results in a " + e.getMessage());
		}
	}

	@SuppressWarnings("OverlyComplexMethod")
	static private Duration calculateEffectiveMax(Duration max, OfPrecision ofPrecision) {
		ChronoUnit precision = ofPrecision.get();
		try {
			Duration effective = max;
			if (precision.compareTo(NANOS) >= 1) {
				if (effective.getNano() % 1_000 != 0) {
					effective = effective.plusNanos(-(effective.getNano() % 1_000));
				}
				if (precision.compareTo(MICROS) >= 1) {
					if (effective.getNano() % 1_000_000 != 0) {
						effective = effective.plusNanos(-(effective.getNano() % 1_000_000));
					}
					if (precision.compareTo(MILLIS) >= 1) {
						if (effective.getNano() != 0) {
							effective = effective.plusNanos(-effective.getNano());
						}
						if (precision.compareTo(SECONDS) >= 1) {
							int seconds = (int) (effective.getSeconds() % 60);
							if (seconds < 0) {
								seconds += 60;
							}
							if (seconds != 0) {
								effective = effective.plusSeconds(-seconds);
							}
							if (precision.compareTo(MINUTES) >= 1) {
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
			throw new IllegalArgumentException("Max value must be decreased because of precision " + precision + " but results in a " + e.getMessage());
		}
	}

	private BigInteger calculateValue(Duration effective, OfPrecision ofPrecision) {

		ChronoUnit precision = ofPrecision.get();

		BigInteger helperMultiply = new BigInteger(1_000_000_000 + "");
		BigInteger helperDivide1000 = new BigInteger(1_000 + "");
		BigInteger helperDivide60 = new BigInteger("60");

		BigInteger bigInteger = new BigInteger(effective.getSeconds() + "");
		bigInteger = bigInteger.multiply(helperMultiply);
		bigInteger = bigInteger.add(new BigInteger(effective.getNano() + ""));

		if (precision.compareTo(NANOS) >= 1) {
			bigInteger = bigInteger.divide(helperDivide1000);
			if (precision.compareTo(MICROS) >= 1) {
				bigInteger = bigInteger.divide(helperDivide1000);
				if (precision.compareTo(MILLIS) >= 1) {
					bigInteger = bigInteger.divide(helperDivide1000);
					if (precision.compareTo(SECONDS) >= 1) {
						bigInteger = bigInteger.divide(helperDivide60);
						if (precision.compareTo(MINUTES) >= 1) {
							bigInteger = bigInteger.divide(helperDivide60);
						}
					}
				}
			}
		}

		return bigInteger;

	}

	static private Duration calculateDuration(BigInteger bigInteger, OfPrecision ofPrecision) {

		ChronoUnit precision = ofPrecision.get();

		BigInteger helperDivide = new BigInteger(1_000_000_000 + "");
		BigInteger helperMultiply1000 = new BigInteger(1_000 + "");
		BigInteger helperMultiply60 = new BigInteger("60");

		switch (precision) {
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

	private void setOfPrecisionImplicitly(DefaultDurationArbitrary clone, Duration duration) {
		if (clone.ofPrecision.isSet()) {
			return;
		}
		ChronoUnit ofPrecision = DefaultLocalTimeArbitrary.calculateOfPrecisionFromNanos(duration.getNano());
		if (clone.ofPrecision.get().compareTo(ofPrecision) > 0) {
			clone.ofPrecision.setProgrammatically(ofPrecision);
		}
	}

	private void setOfPrecisionImplicitly(DefaultDurationArbitrary clone) {
		setOfPrecisionImplicitly(clone, clone.durationBetween.getMin());
		setOfPrecisionImplicitly(clone, clone.durationBetween.getMax());
	}

	@Override
	public DurationArbitrary between(Duration min, Duration max) {
		DefaultDurationArbitrary clone = typedClone();
		clone.durationBetween.set(min, max);
		setOfPrecisionImplicitly(clone);
		return clone;
	}

	@Override
	public DurationArbitrary ofPrecision(ChronoUnit ofPrecision) {
		DefaultDurationArbitrary clone = typedClone();
		clone.ofPrecision.set(ofPrecision);
		return clone;
	}

}
