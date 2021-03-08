package net.jqwik.engine.properties.arbitraries;

import java.math.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

class IntegralEdgeCasesConfiguration extends GenericEdgeCasesConfiguration<BigInteger> {

	private final Range<BigInteger> range;
	private final BigInteger shrinkingTarget;

	public IntegralEdgeCasesConfiguration(Range<BigInteger> range, BigInteger shrinkingTarget) {
		this.range = range;
		this.shrinkingTarget = shrinkingTarget;
	}

	@Override
	protected void checkEdgeCaseIsValid(BigInteger edgeCase) {
		if (!range.includes(edgeCase)) {
			String message = String.format("Edge case <%s> is outside the arbitrary's allowed range %s", edgeCase, range);
			throw new IllegalArgumentException(message);
		}
	}

	@Override
	protected Shrinkable<BigInteger> createShrinkable(BigInteger additionalEdgeCase) {
		return new ShrinkableBigInteger(additionalEdgeCase, range, shrinkingTarget);
	}

}
