package net.jqwik.properties;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import java.util.*;
import java.util.function.*;

class GenericPropertyTests {

	@Example
	void satisfiedWithOneParameter() {
		Function<List[], Boolean> assumeFunction = args -> true;
		Function<List[], Boolean> forAllFunction = args -> true;
		List<Arbitrary> arbitraries =  new ArrayList<>();
		arbitraries.add(new ObjectArbitrary());
		GenericProperty property = new GenericProperty("my property", assumeFunction, arbitraries, forAllFunction);

		long seed = 42L;
		int tries = 2;
		PropertyCheckResult result = property.check(tries, seed);

		Assertions.assertThat(result.propertyName()).isEqualTo("my property");
		Assertions.assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.SATISFIED);
		Assertions.assertThat(result.tries()).isEqualTo(2);
		Assertions.assertThat(result.randomSeed()).isEqualTo(42L);
		Assertions.assertThat(result.sample()).isNotPresent();
		Assertions.assertThat(result.throwable()).isNotPresent();
	}

}
