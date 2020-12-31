package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.function.*;

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
	protected Shrinkable<BigInteger> createShrinkable(BigInteger additionalEdgeCase) {
		return new ShrinkableBigInteger(additionalEdgeCase, range, shrinkingTarget);
	}

}
