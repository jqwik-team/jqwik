package net.jqwik.docs.arbitraryconfigurator;

import java.math.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;

import org.jspecify.annotations.*;

public class OddConfigurator extends ArbitraryConfiguratorBase {

	public Arbitrary<Integer> configureInteger(Arbitrary<Integer> arbitrary, Odd odd) {
		return arbitrary.filter(number -> Math.abs(number % 2) == 1);
	}

	public Arbitrary<BigInteger> configureBigInteger(Arbitrary<BigInteger> arbitrary, Odd odd) {
		return arbitrary.filter(number -> {
			return number.remainder(BigInteger.valueOf(2)).abs().equals(BigInteger.ONE);
		});
	}
}

class PlainOddConfigurator implements ArbitraryConfigurator {

	@Override
	public <T extends @Nullable Object> Arbitrary<T> configure(Arbitrary<T> arbitrary, TypeUsage targetType) {
		if (!targetType.isOfType(Integer.class) && !targetType.isOfType(int.class)) {
			return arbitrary;
		}
		return targetType.findAnnotation(Odd.class)
						 .map(odd -> arbitrary.filter(number -> Math.abs(((Integer) number) % 2) == 1))
						 .orElse(arbitrary);
	}
}

