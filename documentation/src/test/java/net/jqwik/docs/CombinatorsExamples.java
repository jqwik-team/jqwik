package net.jqwik.docs;

import org.assertj.core.api.*;

import net.jqwik.api.*;

@Group
class CombinatorsExamples {

	@Group
	class Combine_as {
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
	}

	@Group
	class Combine_asFlat {
		@Property @Report(Reporting.GENERATED)
		boolean fullNameHasTwoParts(@ForAll("fullName") String aName) {
			return aName.split(" ").length == 2;
		}

		@Provide
		Arbitrary<String> fullName() {
			Arbitrary<Integer> firstNameLength = Arbitraries.integers().between(2, 10);
			Arbitrary<Integer> lastNameLength = Arbitraries.integers().between(2, 10);
			return Combinators.combine(firstNameLength, lastNameLength).flatAs((fLength, lLength) -> {
				Arbitrary<String> firstName = Arbitraries.strings().alpha().ofLength(fLength);
				Arbitrary<String> lastName = Arbitraries.strings().alpha().ofLength(fLength);
				return Combinators.combine(firstName, lastName).as((f, l) -> f + " " + l);
			});
		}
	}

	@Group
	class RealBuilder {
		@Property
		void validPeopleHaveIDs(@ForAll("validPeopleWithBuilder") Person aPerson) {
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
	}

	@Group
	class POJO_as_builder {
		@Property
		void validPeopleHaveIDs(@ForAll("validPeopleWithPersonAsBuilder") Person aPerson) {
			Assertions.assertThat(aPerson.getID()).contains("-");
			Assertions.assertThat(aPerson.getID().length()).isBetween(5, 24);
		}

		@Provide
		Arbitrary<Person> validPeopleWithPersonAsBuilder() {
			Arbitrary<String> names =
				Arbitraries.strings().withCharRange('a', 'z').ofMinLength(3).ofMaxLength(21);
			Arbitrary<Integer> ages = Arbitraries.integers().between(0, 130);

			return Combinators.withBuilder(() -> new Person(null, -1))
							  .use(names).inSetter(Person::setName)
							  .use(ages).inSetter(Person::setAge)
							  .build();
		}
	}

	static class Person {

		private String name;
		private int age;

		public Person(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public void setName(String newName) {
			this.name = newName;
		}

		public void setAge(int age) {
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
