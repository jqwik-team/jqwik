package net.jqwik.docs;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;

@Group
class BuildersExamples {

	@Group
	class RealBuilder {
		@Property
		// @Report(Reporting.GENERATED)
		void validPeopleHaveIDs(@ForAll("validPeopleWithBuilder") Person aPerson) {
			Statistics.collect(aPerson.age == 42);
			Assertions.assertThat(aPerson.getID()).contains("-");
			Assertions.assertThat(aPerson.getID().length()).isBetween(5, 24);
		}

		@Provide
		Arbitrary<Person> validPeopleWithBuilder() {
			Arbitrary<String> names =
				Arbitraries.strings().withCharRange('a', 'z').ofMinLength(3).ofMaxLength(21);
			Arbitrary<Integer> ages = Arbitraries.integers().between(0, 130);

			return Builders.withBuilder(() -> new PersonBuilder())
							  .use(names).in((builder, name) -> builder.withName(name))
							  .use(ages).withProbability(0.5).in((builder, age)-> builder.withAge(age))
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

			return Builders.withBuilder(() -> new Person(null, -1))
						   .use(names).inSetter(Person::setName)
						   .use(ages).withProbability(0.5).inSetter(Person::setAge)
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
