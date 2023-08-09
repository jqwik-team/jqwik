package net.jqwik.docs.arbitraryconfigurator;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;

import java.math.*;

public class OddConfigurator extends ArbitraryConfiguratorBase {

	public Arbitrary<Integer> configureInteger(Arbitrary<Integer> arbitrary, Odd odd) {
		return arbitrary.filter(number  -> Math.abs(number % 2) == 1);
	}

	public Arbitrary<BigInteger> configureBigInteger(Arbitrary<BigInteger> arbitrary, Odd odd) {
		return arbitrary.filter(number  -> {
            return number.mod(BigInteger.valueOf(2)).compareTo(BigInteger.ZERO) != 0;
        });
	}
}
