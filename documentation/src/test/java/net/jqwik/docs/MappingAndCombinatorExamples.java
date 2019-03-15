package net.jqwik.docs;

import org.assertj.core.api.Assertions;

import net.jqwik.api.*;

class MappingAndCombinatorExamples {

	@Property
	boolean fiveDigitsAreAlways5Long(@ForAll("fiveDigitStrings") String numericalString) {
		return numericalString.length() == 5;
	}

	@Provide
	Arbitrary<String> fiveDigitStrings() {
		return Arbitraries.integers().between(10000, 99999).map(aNumber -> String.valueOf(aNumber));
	}

	@Property
	void validPeopleHaveIDs(@ForAll("validPeople") Person aPerson) {
		Assertions.assertThat(aPerson.getID()).contains("-");
		Assertions.assertThat(aPerson.getID().length()).isBetween(5, 24);
	}

	@Provide
	Arbitrary<Person> validPeople() {
		Arbitrary<String> names = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(3).ofMaxLength(21);
		Arbitrary<Integer> ages = Arbitraries.integers().between(0, 130);
		return Combinators.combine(names, ages).as((name, age) -> new Person(name, age));
	}

	@Property
	void validPeopleHaveIDs2(@ForAll("validPeopleWithBuilder") Person aPerson) {
		Assertions.assertThat(aPerson.getID()).contains("-");
		Assertions.assertThat(aPerson.getID().length()).isBetween(5, 24);
	}

	@Provide
	Arbitrary<Person> validPeopleWithBuilder() {
		Arbitrary<String> names =
			Arbitraries.strings().withCharRange('a', 'z').ofMinLength(3).ofMaxLength(21);
		Arbitrary<Integer> ages = Arbitraries.integers().between(0, 130);

		return Combinators.withBuilder(() -> new PersonBuilder())
						  .use(names).in((builder, name) -> builder.withName(name))
						  .use(ages).in((builder, age)-> builder.withAge(age))
						  .build( builder -> builder.build());
	}

	static class Person {

		private final String name;
		private final int age;

		public Person(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public String getID() {
			return name + "-" + age;
		}

		@Override
		public String toString() {
			return String.format("%s:%s", name, age);
		}
	}

	static class PersonBuilder {

		private String name = "A name";
		private int age = 42;

		public PersonBuilder withName(String name) {
			this.name = name;
			return this;
		}

		public PersonBuilder withAge(int age) {
			this.age = age;
			return this;
		}

		public Person build() {
			return new Person(name, age);
		}
	}

}
