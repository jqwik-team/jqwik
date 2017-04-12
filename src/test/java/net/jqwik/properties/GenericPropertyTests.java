package net.jqwik.properties;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.jqwik.api.*;

class GenericPropertyTests {

	@Example
	void satisfiedWithOneParameter() {
		AtomicInteger countTries = new AtomicInteger(0);
		Function<List<?>, Boolean> forAllFunction = args -> {
			assertThat(args).hasSize(1);
			assertThat(args.get(0)).isInstanceOf(Integer.class);
			countTries.incrementAndGet();
			return true;
		};

		List<Arbitrary> arbitraries =  new ArrayList<>();

		AtomicInteger countNext = new AtomicInteger(0);
		Generator<Integer> countingGenerator = () -> countNext.incrementAndGet();
		Arbitrary<Integer> arbitrary = Arbitraries.fromGenerator(countingGenerator);

		arbitraries.add(arbitrary);

		GenericProperty property = new GenericProperty("my property", arbitraries, forAllFunction);

		long seed = 42L;
		int tries = 2;
		PropertyCheckResult result = property.check(tries, seed);

		assertThat(countTries.get()).isEqualTo(2);
		assertThat(countNext.get()).isEqualTo(2);

		assertThat(result.propertyName()).isEqualTo("my property");
		assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.SATISFIED);
		assertThat(result.tries()).isEqualTo(2);
		assertThat(result.randomSeed()).isEqualTo(42L);
		assertThat(result.sample()).isNotPresent();
		assertThat(result.throwable()).isNotPresent();
	}

}
