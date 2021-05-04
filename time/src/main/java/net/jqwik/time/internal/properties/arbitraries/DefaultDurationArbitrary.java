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
	private final OfPrecision ofPrecision = new OfPrecision();

	@Override
	protected Arbitrary<Duration> arbitrary() {

		Duration effectiveMin = effectiveMin(durationBetween, ofPrecision);
		Duration effectiveMax = effectiveMax(durationBetween, ofPrecision);

		BigInteger bigIntegerMin = valueFromDuration(effectiveMin, ofPrecision);
		BigInteger bigIntegerMax = valueFromDuration(effectiveMax, ofPrecision);

		Arbitrary<BigInteger> bigIntegers = Arbitraries.bigIntegers()
													   .between(bigIntegerMin, bigIntegerMax)
													   .withDistribution(RandomDistribution.uniform())
													   .edgeCases(edgeCases -> edgeCases
																				   .includeOnly(bigIntegerMin, BigInteger.ZERO, bigIntegerMax));

		return bigIntegers.map(ofPrecision::durationFromValue);

	}

	private Duration effectiveMin(DurationBetween durationBetween, OfPrecision ofPrecision) {
		Duration effective = durationBetween.getMin() != null ? durationBetween.getMin() : ofPrecision.minPossibleDuration();
		checkValueAndPrecision(effective, ofPrecision, true);
		return effective;
	}

	private void checkValueAndPrecision(Duration effective, OfPrecision ofPrecision, boolean minimum) {
		int minutes = (int) ((effective.getSeconds() % 3_600) / 60);
		int seconds = (int) (effective.getSeconds() % 60);
		int nanos = effective.getNano();
		if (!ofPrecision.valueWithPrecisionIsAllowed(minutes, seconds, nanos)) {
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

	private Duration effectiveMax(DurationBetween durationBetween, OfPrecision ofPrecision) {
		Duration effective = durationBetween.getMax() != null ? durationBetween.getMax() : ofPrecision.maxPossibleDuration();
		checkValueAndPrecision(effective, ofPrecision, false);
		return effective;
	}

	private BigInteger valueFromDuration(Duration effective, OfPrecision ofPrecision) {

		BigInteger helperMultiply = new BigInteger(1_000_000_000 + "");
		BigInteger helperDivide1000 = new BigInteger(1_000 + "");
		BigInteger helperDivide60 = new BigInteger("60");

		BigInteger bigInteger = new BigInteger(effective.getSeconds() + "");
		bigInteger = bigInteger.multiply(helperMultiply);
		bigInteger = bigInteger.add(new BigInteger(effective.getNano() + ""));

		if (ofPrecision.isGreatherThan(NANOS)) {
			bigInteger = bigInteger.divide(helperDivide1000);
			if (ofPrecision.isGreatherThan(MICROS)) {
				bigInteger = bigInteger.divide(helperDivide1000);
				if (ofPrecision.isGreatherThan(MILLIS)) {
					bigInteger = bigInteger.divide(helperDivide1000);
					if (ofPrecision.isGreatherThan(SECONDS)) {
						bigInteger = bigInteger.divide(helperDivide60);
						if (ofPrecision.isGreatherThan(MINUTES)) {
							bigInteger = bigInteger.divide(helperDivide60);
						}
					}
				}
			}
		}

		return bigInteger;

	}

	private void setOfPrecisionImplicitly(DefaultDurationArbitrary clone, Duration duration) {
		if (clone.ofPrecision.isSet()) {
			return;
		}
		ChronoUnit ofPrecision = DefaultLocalTimeArbitrary.ofPrecisionFromNanos(duration.getNano());
		if (clone.ofPrecision.isGreatherThan(ofPrecision)) {
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
