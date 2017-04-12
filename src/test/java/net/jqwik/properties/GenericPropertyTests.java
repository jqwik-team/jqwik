package net.jqwik.properties;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

class GenericPropertyTests {

	@Example
	void satisfiedWithOneParameter() {
		Function<List<?>, Boolean> exactlyOneIntegerArgument = args -> args.size() == 1 && args.get(0) instanceof Integer;
		ForAllSpy forAllFunction = new ForAllSpy(trie -> true, exactlyOneIntegerArgument);

		CountingArbitrary arbitrary = new CountingArbitrary();
		List<Arbitrary> arbitraries = arbitraries(arbitrary);

		GenericProperty property = new GenericProperty("my property", arbitraries, forAllFunction);
		PropertyCheckResult result = property.check(2, 42L);

		assertThat(forAllFunction.countCalls()).isEqualTo(2);
		assertThat(arbitrary.count()).isEqualTo(2);

		assertThat(result.propertyName()).isEqualTo("my property");
		assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.SATISFIED);
		assertThat(result.tries()).isEqualTo(2);
		assertThat(result.randomSeed()).isEqualTo(42L);
		assertThat(result.sample()).isNotPresent();
		assertThat(result.throwable()).isNotPresent();
	}

	private List<Arbitrary> arbitraries(Arbitrary... arbitraries) {
		return Arrays.asList(arbitraries);
	}

}
