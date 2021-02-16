package net.jqwik;

import java.util.*;

import org.junit.jupiter.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

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
	void forStrings() {
		assertThat(Arbitraries.strings().sample()).isInstanceOf(String.class);
	}

	@Test
	void injectDuplicates() {
		Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
		Arbitrary<Integer> intsWithDuplicates = ints.injectDuplicates(0.5);

		List<Integer> listWithDuplicates = intsWithDuplicates.list().ofSize(100).sample();
		Set<Integer> noMoreDuplicates = new HashSet<>(listWithDuplicates);

		// Might very rarely fail
		assertThat(noMoreDuplicates).hasSizeLessThanOrEqualTo(65);
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

}
