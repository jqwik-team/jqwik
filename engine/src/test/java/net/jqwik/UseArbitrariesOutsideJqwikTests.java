package net.jqwik;

import org.junit.jupiter.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

class UseArbitrariesOutsideJqwikTests {

	@Test
	// This test must run outside jqwik!
	void forType() {
		TypeArbitrary<Person> people = Arbitraries.forType(Person.class);

		people.sampleStream().limit(100).forEach(value -> {
			assertThat(value).isNotNull();
			assertThat(value.firstName).isNotNull();
			assertThat(value.lastName).isNotNull();
		});
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
