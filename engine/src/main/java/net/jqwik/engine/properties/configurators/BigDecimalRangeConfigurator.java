package net.jqwik.engine.properties.configurators;

import java.math.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

import static net.jqwik.engine.properties.arbitraries.DefaultBigDecimalArbitrary.*;

public class BigDecimalRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(BigDecimal.class);
	}

	public Arbitrary<BigDecimal> configure(Arbitrary<BigDecimal> arbitrary, BigRange range) {
		BigDecimal min = evaluate(range.min(), BigDecimal::new, DEFAULT_MIN);
		BigDecimal max = evaluate(range.max(), BigDecimal::new, DEFAULT_MAX);
		if (arbitrary instanceof BigDecimalArbitrary) {
			return ((BigDecimalArbitrary) arbitrary).between(min, range.minIncluded(), max, range.maxIncluded());
		}
		return arbitrary.filter(i -> {
			boolean minCondition = range.minIncluded() ? min.compareTo(i) <= 0 : min.compareTo(i) < 0;
			boolean maxCondition = range.maxIncluded() ? max.compareTo(i) >= 0 : max.compareTo(i) > 0;
			return minCondition && maxCondition;
		});

	}

	private static BigDecimal evaluate(String valueString, Function<String, BigDecimal> evaluator, BigDecimal defaultValue) {
		return valueString.isEmpty() ? defaultValue : evaluator.apply(valueString);
	}

}
