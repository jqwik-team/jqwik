package net.jqwik.engine.execution;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

class ParametersGeneratorForTests implements ParametersGenerator{

	int index = 0;

	@Override
	public boolean hasNext() {
		return index < 25;
	}

	@Override
	public List<Shrinkable<Object>> next(TryLifecycleContext context) {
		return Arrays.asList(shrinkableInt(++index));
	}

	private Shrinkable<Object> shrinkableInt(int anInt) {
		Range<BigInteger> range = Range.of(BigInteger.ZERO, BigInteger.valueOf(1000));
		BigInteger value = BigInteger.valueOf(anInt);
		return new ShrinkableBigInteger(value, range, BigInteger.ZERO)
			.map(BigInteger::intValueExact)
			.asGeneric();
	}

	@Override
	public int edgeCasesTotal() {
		return 0;
	}

	@Override
	public int edgeCasesTried() {
		return 0;
	}

	@Override
	public GenerationInfo generationInfo(String randomSeed) {
		return new GenerationInfo(randomSeed, index);
	}

	@Override
	public void reset() {
		index = 0;
	}

}
