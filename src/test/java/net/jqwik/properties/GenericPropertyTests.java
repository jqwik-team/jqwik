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

		GenericProperty property = new GenericProperty("satisfied property", arbitraries, forAllFunction);
		PropertyCheckResult result = property.check(2, 42L);

		assertThat(forAllFunction.countCalls()).isEqualTo(2);
		assertThat(arbitrary.count()).isEqualTo(2);

		assertThat(result.propertyName()).isEqualTo("satisfied property");
		assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.SATISFIED);
		assertThat(result.countTries()).isEqualTo(2);
		assertThat(result.countChecks()).isEqualTo(2);
		assertThat(result.randomSeed()).isEqualTo(42L);
		assertThat(result.throwable()).isNotPresent();
		assertThat(result.sample()).isNotPresent();
	}

	@Example
	void falsifiedWithOneParameter() {
		int failingTry = 5;

		Function<List<?>, Boolean> exactlyOneIntegerArgument = args -> args.size() == 1 && args.get(0) instanceof Integer;
		ForAllSpy forAllFunction = new ForAllSpy(trie -> trie < failingTry, exactlyOneIntegerArgument);

		CountingArbitrary arbitrary = new CountingArbitrary();
		List<Arbitrary> arbitraries = arbitraries(arbitrary);

		GenericProperty property = new GenericProperty("falsified property", arbitraries, forAllFunction);
		PropertyCheckResult result = property.check(10, 41L);

		assertThat(forAllFunction.countCalls()).isEqualTo(failingTry);
		assertThat(arbitrary.count()).isEqualTo(failingTry);

		assertThat(result.propertyName()).isEqualTo("falsified property");
		assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.FALSIFIED);
		assertThat(result.countTries()).isEqualTo(failingTry);
		assertThat(result.countChecks()).isEqualTo(failingTry);
		assertThat(result.randomSeed()).isEqualTo(41L);
		assertThat(result.throwable()).isNotPresent();

		assertThat(result.sample()).isPresent();
		assertThat(result.sample().get()).containsExactly(failingTry);
	}

	private List<Arbitrary> arbitraries(Arbitrary... arbitraries) {
		return Arrays.asList(arbitraries);
	}

}
