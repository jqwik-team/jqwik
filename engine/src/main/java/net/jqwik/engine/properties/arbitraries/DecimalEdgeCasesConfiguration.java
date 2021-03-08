package net.jqwik.engine.properties.arbitraries;

import java.math.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

import static net.jqwik.engine.properties.arbitraries.randomized.RandomDecimalGenerators.*;

class DecimalEdgeCasesConfiguration extends GenericEdgeCasesConfiguration<BigDecimal> {

	private final Range<BigDecimal> range;
	private final int scale;
	private final BigDecimal shrinkingTarget;

	public DecimalEdgeCasesConfiguration(Range<BigDecimal> range, int scale, BigDecimal shrinkingTarget) {
		this.range = range;
		this.scale = scale;
		this.shrinkingTarget = shrinkingTarget;
	}

	@Override
	protected void checkEdgeCaseIsValid(BigDecimal edgeCase) {
		if (!range.includes(edgeCase)) {
			String message = String.format("Edge case <%s> is outside the arbitrary's allowed range %s", edgeCase, range);
			throw new IllegalArgumentException(message);
		}
	}

	@Override
	protected Shrinkable<BigDecimal> createShrinkable(BigDecimal additionalEdgeCase) {
		Range<BigInteger> bigIntegerRange = unscaledBigIntegerRange(range, scale);
		BigInteger bigIntegerValue = unscaledBigInteger(additionalEdgeCase, scale);
		BigInteger integralShrinkingTarget = unscaledBigInteger(shrinkingTarget, scale);
		return new ShrinkableBigInteger(bigIntegerValue, bigIntegerRange, integralShrinkingTarget)
					   .map(bigInteger -> scaledBigDecimal(bigInteger, scale));
	}

}
