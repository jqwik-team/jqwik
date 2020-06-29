package examples.packageWithProperties;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

public class GeneratorsExamples {

	@Property(tries = 50) @Report(Reporting.GENERATED)
	boolean aString(@ForAll @StringLength(min = 2, max = 33) String aString, @ForAll(value = "digitsOnly") String anotherString) {
		return true;
	}

	@Provide
	Arbitrary<String> stringArbitrary() {
		return Arbitraries.strings().withCharRange('a', 'z');
	}

	@Provide
	Arbitrary<String> digitsOnly() {
		return Arbitraries.strings().withCharRange('0', '9');
	}

	@Property(tries = 20)
	boolean aPersonIsNeverYoungerThan1(@ForAll Person aPerson) {
		System.out.println(aPerson);
		return aPerson.getAge() > 0;
	}

	@Provide
	Arbitrary<Person> aValidPerson() {
		Arbitrary<Integer> age = Arbitraries.integers().between(0, 100);

		Arbitrary<String> firstName = Arbitraries.strings() //
												 .withCharRange('a', 'z') //
												 .ofMinLength(0).ofMaxLength(10) //
												 .filter(f -> !f.isEmpty());

		Arbitrary<String> lastName = Arbitraries.strings() //
												.withCharRange('a', 'z') //
												.ofMinLength(1).ofMaxLength(15);

		return Combinators.combine(age, firstName, lastName).as((a, f, l) -> {
			String name = f + " " + l;
			return new Person(name, a);
		});
	}

	@Property(tries = 100)
	boolean plusMinusSameNumberIsZero(@ForAll int aNumber) {
		System.out.println(aNumber);
		return aNumber - aNumber == 0;
	}

	@Property(tries = 100, generation = GenerationMode.RANDOMIZED)
	boolean numbersBetween0and100(@ForAll("between0and100") long aNumber) {
		System.out.println(aNumber);
		return aNumber - aNumber == 0;
	}

	@Property(tries = 10)
	boolean aDouble(@ForAll @Scale(value = 4) double aDouble) {
		System.out.println(aDouble);
		return true;
	}

	@Property(tries = 10)
	boolean aFloat(@ForAll @Scale(value = 4) float aFloat) {
		System.out.println(aFloat);
		return true;
	}

	@Property(tries = 10)
	boolean aBigDecimal(@ForAll @BigRange(max = "100.0") @Scale(value = 4) BigDecimal aBigDecimal) {
		System.out.println(aBigDecimal);
		return true;
	}

	@Property(tries = 10) @Report(Reporting.GENERATED)
	boolean aChar(@ForAll @AlphaChars char aChar, @ForAll char unconstrained) {
		return true;
	}

	@Provide
	Arbitrary<Long> between0and100() {
		return Arbitraries.longs().between(0L, 100L);
	}

	@Property(tries = 10) @Report(Reporting.GENERATED)
	boolean aListOfInts(@ForAll List<Integer> anIntList) {
		return true;
	}

	@Provide
	Arbitrary<List<Integer>> aList() {
		return Arbitraries.integers().between(0, 10).list();
	}

	@Property(tries = 10)
	void anArrayOfInteger(@ForAll Integer[] array) {
		System.out.println(Arrays.asList(array));
	}

	@Property(tries = 10) @Report(Reporting.GENERATED)
	void anArrayOfPrimitiveInts(@ForAll @Size(max = 10) @IntRange(min = 1, max = 5) int[] array) {
	}

	@Property
	boolean aConstantString(@ForAll("justHello") String aString) {
		return aString.equals("hello");
	}

	@Provide
	Arbitrary<String> justHello() {
		return Arbitraries.just("hello");
	}

	@Property(tries = 10) @Report(Reporting.GENERATED)
	boolean aPeopleList(@ForAll @Size(min = 2, max = 5) List<Person> people) {
		return people != null;
	}

	@Property(tries = 100) @Report(Reporting.GENERATED)
	boolean aPeopleSet(@ForAll Set<Person> people) {
		return people != null;
	}

	@Property(tries = 10)
	boolean aPeopleStream(@ForAll Stream<Person> people) {
		return people.allMatch(person -> person.getAge() >= 0);
	}

	static class Person {
		private final String name;
		private final int age;

		Person(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public int getAge() {
			return age;
		}

		@Override
		public String toString() {
			return String.format("Person(%s, %s)", name, age);
		}
	}
}
