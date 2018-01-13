package examples.docs.defaultprovider;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.math.*;
import java.util.*;
import java.util.function.*;

public class MoneyArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isOfType(Money.class);
	}

	@Override
	public Arbitrary<?> provideFor(
		GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider
	) {
		Arbitrary<BigDecimal> amount = Arbitraries.bigDecimals(BigDecimal.ZERO, new BigDecimal(1_000_000_000), 2);
		Arbitrary<String> currency = Arbitraries.of("EUR", "USD", "CHF");
		return Combinators.combine(amount, currency).as((a, c) -> new Money(a, c));
	}
}
