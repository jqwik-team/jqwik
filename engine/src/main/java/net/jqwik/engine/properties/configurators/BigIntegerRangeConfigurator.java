package net.jqwik.engine.properties.configurators;

import java.math.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

import static net.jqwik.engine.properties.arbitraries.DefaultBigIntegerArbitrary.*;

public class BigIntegerRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(BigInteger.class);
	}

	public Arbitrary<BigInteger> configure(Arbitrary<BigInteger> arbitrary, BigRange range) {
		BigInteger min = evaluate(range.min(), val -> new BigDecimal(val).toBigIntegerExact(), DEFAULT_MIN);
		BigInteger max = evaluate(range.max(), val -> new BigDecimal(val).toBigIntegerExact(), DEFAULT_MAX);
		if (!range.minIncluded() || !range.maxIncluded()) {
			String message = "minIncluded and maxIncluded are only allowed for parameters of type java.math.BigDecimal";
			throw new JqwikException(message);
		}
		if (arbitrary instanceof BigIntegerArbitrary) {
			return ((BigIntegerArbitrary) arbitrary).greaterOrEqual(min).lessOrEqual(max);
		}
		return arbitrary.filter(i -> min.compareTo(i) <= 0 && max.compareTo(i) >= 0);
	}

	private static BigInteger evaluate(String valueString, Function<String, BigInteger> evaluator, BigInteger defaultValue) {
		return valueString.isEmpty() ? defaultValue : evaluator.apply(valueString);
	}
}
