package examples.docs.defaultprovider;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.math.*;
import java.util.*;

public class MoneyArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isOfType(Money.class);
	}

	@Override
	public Set<Arbitrary<?>> provideArbitrariesFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		Arbitrary<BigDecimal> amount = Arbitraries.bigDecimals() //
				.between(BigDecimal.ZERO, new BigDecimal(1_000_000_000)) //
				.ofScale(2);
		Arbitrary<String> currency = Arbitraries.of("EUR", "USD", "CHF");
		return Collections.singleton(Combinators.combine(amount, currency).as(Money::new));
	}
}
