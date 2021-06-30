package net.jqwik;

import java.util.*;

import org.junit.jupiter.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.providers.*;

import static org.assertj.core.api.Assertions.*;

// These tests must run outside jqwik!
class UseArbitrariesOutsideJqwikTests {

	@Test
	void forType() {
		TypeArbitrary<Person> people = Arbitraries.forType(Person.class);

		people.sampleStream().limit(100).forEach(value -> {
			assertThat(value).isNotNull();
			assertThat(value.firstName).isNotNull();
			assertThat(value.lastName).isNotNull();
		});
	}

	@Test
	void defaultFor() {
		Arbitrary<String> strings = Arbitraries.defaultFor(String.class);

		strings.sampleStream().limit(100).forEach(value -> {
			assertThat(value).isNotNull();
			assertThat(value).isInstanceOf(String.class);
		});
	}

	@Test
	void defaultForWithGloballyRegisteredProvider() {
		RegisteredArbitraryProviders.register(new PersonProvider());

		Arbitrary<Person> people = Arbitraries.defaultFor(Person.class);

		people.sampleStream().limit(100).forEach(value -> {
			assertThat(value).isNotNull();
			assertThat(value.firstName).isNotNull();
			assertThat(value.lastName).isNotNull();
		});
	}

	@Test
	void forStrings() {
		assertThat(Arbitraries.strings().sample()).isInstanceOf(String.class);
	}

	@Test
	void lazyOf() {
		Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);

		Arbitrary<Integer> sum = Arbitraries.lazyOf(
				() -> Arbitraries.just(0),
				() -> ints
		);

		assertThat(sum.sample()).isBetween(-1000, 1000);
	}

	@Test
	void injectDuplicates() {
		Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
		Arbitrary<Integer> intsWithDuplicates = ints.injectDuplicates(0.5);

		Random random = new Random();
		// Try 5 times because it can fail rarely
		for (int i = 0; i < 20; i++) {

			List<Integer> listWithDuplicates = intsWithDuplicates.list().ofSize(100).sample();
			Set<Integer> noMoreDuplicates = new HashSet<>(listWithDuplicates);

			if (i < 19) {
				// Can fail because collections with no duplicates have probability of approx. 5%
				if (noMoreDuplicates.size() < 80) {
					return;
				}
			} else {
				assertThat(noMoreDuplicates).hasSizeLessThanOrEqualTo(65);
			}
		}
	}

	@Test
	void samplingOfSameArbitraryShouldUseSameGenerator() {
		Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
		Arbitrary<Integer> intsWithDuplicates = ints.injectDuplicates(1.0);

		int i1 = intsWithDuplicates.sample();
		int i2 = intsWithDuplicates.sample();
		int i3 = intsWithDuplicates.sample();
		int i4 = intsWithDuplicates.sample();

		assertThat(Arrays.asList(i1, i2, i3, i4)).allMatch(i -> i == i1);
	}

	private static class Person {
		private final String firstName;
		private final String lastName;

		public Person(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public static Person create(String firstName) {
			return new Person(firstName, "Stranger");
		}
	}

	private static class PersonProvider implements ArbitraryProvider {
		@Override
		public boolean canProvideFor(TypeUsage targetType) {
			return targetType.isOfType(Person.class);
		}

		@Override
		public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
			return Collections.singleton(Arbitraries.just(new Person("first", "last")));
		}
	}
}
