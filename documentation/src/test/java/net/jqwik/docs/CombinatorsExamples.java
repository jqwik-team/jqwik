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
	class Combine_and_filter {

		@Property
		void pairsCannotBeTwins(@ForAll("digitPairsWithoutTwins") String pair) {
			Assertions.assertThat(pair).hasSize(2);
			Assertions.assertThat(pair.charAt(0)).isNotEqualTo(pair.charAt(1));
		}

		@Provide
		Arbitrary<String> digitPairsWithoutTwins() {
			Arbitrary<Integer> digits = Arbitraries.integers().between(0, 9);
			return Combinators.combine(digits, digits)
							  .filter((first, second) -> first != second)
							  .as((first, second) -> first + "" + second);
		}

	}

	@Group
	class Combine_asFlat {
		@Property
		@Report(Reporting.GENERATED)
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

}
