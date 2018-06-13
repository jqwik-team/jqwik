package examples.docs.defaultprovider;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

public class MoneyArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isOfType(Money.class);
	}

	@Override
	public Arbitrary<?> provideFor(TypeUsage targetType, Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider) {
		Arbitrary<BigDecimal> amount = Arbitraries.bigDecimals() //
				.between(BigDecimal.ZERO, new BigDecimal(1_000_000_000)) //
				.ofScale(2);
		Arbitrary<String> currency = Arbitraries.of("EUR", "USD", "CHF");
		return Combinators.combine(amount, currency).as((a, c) -> new Money(a, c));
	}
}
