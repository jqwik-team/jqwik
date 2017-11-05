package examples.docs;

import org.assertj.core.api.Assertions;

import net.jqwik.api.*;

class MappingAndCombinatorExamples {

	@Property
	boolean fiveDigitsAreAlways5Long(@ForAll("fiveDigitStrings") String numericalString) {
		return numericalString.length() == 5;
	}

	@Provide
	Arbitrary<String> fiveDigitStrings() {
		return Arbitraries.integers(10000, 99999).map(aNumber -> String.valueOf(aNumber));
	}

	@Property(tries = 30, reporting = ReportingMode.GENERATED)
	void validPeopleHaveIDs(@ForAll Person aPerson) {
		Assertions.assertThat(aPerson.getID()).contains("-");
		Assertions.assertThat(aPerson.getID().length()).isBetween(5, 24);
	}

	@Provide
	Arbitrary<Person> validPeople() {
		Arbitrary<Character> initials = Arbitraries.chars('A', 'Z');
		Arbitrary<String> names = Arbitraries.strings('a', 'z', 2, 20);
		Arbitrary<Integer> ages = Arbitraries.integers(0, 130);
		return Combinators.combine(initials, names, ages).as((initial, name, age) -> new Person(initial + name, age));
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
}
