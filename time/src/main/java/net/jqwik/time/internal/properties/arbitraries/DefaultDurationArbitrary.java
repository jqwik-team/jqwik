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
	public static final Duration DEFAULT_MIN_PRECISION_HOURS = Duration.ofSeconds((Long.MIN_VALUE / (60 * 60)) * (60 * 60), 0);
	public static final Duration DEFAULT_MAX = Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);
	public static final Duration DEFAULT_MAX_PRECISION_HOURS = Duration.ofSeconds((Long.MAX_VALUE / (60 * 60)) * (60 * 60), 0);

	private final DurationBetween durationBetween = new DurationBetween();
	private OfPrecision ofPrecision = new OfPrecision();

	@Override
	protected Arbitrary<Duration> arbitrary() {

		Duration effectiveMin = calculateEffectiveMin(durationBetween, ofPrecision);
		Duration effectiveMax = calculateEffectiveMax(durationBetween, ofPrecision);

		BigInteger bigIntegerMin = calculateValue(effectiveMin, ofPrecision);
		BigInteger bigIntegerMax = calculateValue(effectiveMax, ofPrecision);

		Arbitrary<BigInteger> bigIntegers = Arbitraries.bigIntegers()
													   .between(bigIntegerMin, bigIntegerMax)
													   .withDistribution(RandomDistribution.uniform())
													   .edgeCases(edgeCases -> edgeCases
																				   .includeOnly(bigIntegerMin, BigInteger.ZERO, bigIntegerMax));

		return bigIntegers.map(big -> calculateDuration(big, ofPrecision));

	}

	private Duration calculateEffectiveMin(DurationBetween durationBetween, OfPrecision ofPrecision) {
		Duration effective = durationBetween.getMin() != null ? durationBetween.getMin() : calculateMinPossibleValue(ofPrecision);
		checkValueAndPrecision(effective, ofPrecision, true);
		return effective;
	}

	private Duration calculateMinPossibleValue(OfPrecision ofPrecision) {
		switch (ofPrecision.get()) {
			case HOURS:
				return Duration.ofSeconds((Long.MIN_VALUE / (60 * 60)) * (60 * 60), 0);
			case MINUTES:
				return Duration.ofSeconds((Long.MIN_VALUE / 60) * 60, 0);
			default:
				return Duration.ofSeconds(Long.MIN_VALUE, 0);
		}
	}

	private void checkValueAndPrecision(Duration effective, OfPrecision ofPrecision, boolean minimum) {
		boolean throwException = false;
		switch (ofPrecision.get()) {
			case HOURS:
				throwException = effective.getSeconds() % 3600 != 0;
			case MINUTES:
				throwException = throwException || effective.getSeconds() % 60 != 0;
			case SECONDS:
				throwException = throwException || effective.getNano() != 0;
				break;
			case MILLIS:
				throwException = (effective.getNano() % 1_000_000) != 0;
				break;
			case MICROS:
				throwException = (effective.getNano() % 1_000) != 0;
		}
		if (throwException) {
			throwDurationAndPrecisionException(effective.toString(), minimum, ofPrecision.get());
		}
	}

	private static void throwDurationAndPrecisionException(String val, boolean minimum, ChronoUnit precision) {
		String minMax = minimum ? "minimum" : "maximum";
		throw new IllegalArgumentException(
			String
				.format("Can't use %s as %s duration with precision %s.%nYou may want to round the duration to %s or change the precision.", val, minMax, precision, precision)
		);
	}

	private Duration calculateEffectiveMax(DurationBetween durationBetween, OfPrecision ofPrecision) {
		Duration effective = durationBetween.getMax() != null ? durationBetween.getMax() : calculateMaxPossibleValue(ofPrecision);
		checkValueAndPrecision(effective, ofPrecision, false);
		return effective;
	}

	private Duration calculateMaxPossibleValue(OfPrecision ofPrecision) {
		switch (ofPrecision.get()) {
			case HOURS:
				return Duration.ofSeconds((Long.MAX_VALUE / (60 * 60)) * (60 * 60), 0);
			case MINUTES:
				return Duration.ofSeconds((Long.MAX_VALUE / 60) * 60, 0);
			case MILLIS:
				return Duration.ofSeconds(Long.MAX_VALUE, 999_000_000);
			case MICROS:
				return Duration.ofSeconds(Long.MAX_VALUE, 999_999_000);
			case NANOS:
				return Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);
			default:
				return Duration.ofSeconds(Long.MAX_VALUE, 0);
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
			clone.ofPrecision = this.ofPrecision.setProgrammatically(ofPrecision);
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
		clone.ofPrecision = this.ofPrecision.set(ofPrecision);
		return clone;
	}

}
