package examples.packageWithProperties;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

public class GeneratorsExamples {

	@Property(tries = 50, reporting = ReportingMode.GENERATED)
	boolean aString(@ForAll String aString, @ForAll(value = "digitsOnly") String anotherString) {
		return true;
	}

	@Provide
	Arbitrary<String> stringArbitrary() {
		return Arbitraries.string('a', 'z');
	}

	@Provide
	Arbitrary<String> digitsOnly() {
		return Arbitraries.string('0', '9');
	}

	@Property(tries = 20)
	boolean aPersonIsNeverYoungerThan1(@ForAll Person aPerson) {
		System.out.println(aPerson);
		return aPerson.getAge() > 0;
	}

	@Provide
	Arbitrary<Person> aValidPerson() {
		Arbitrary<Integer> age = Arbitraries.integer(0, 100);
		Arbitrary<String> first = Arbitraries.string('a', 'z', 10).filter(f -> !f.isEmpty());
		Arbitrary<String> last = Arbitraries.string('a', 'z', 15).filter(f -> !f.isEmpty());

		return Combinators.combine(age, first, last).as((a, f, l) -> {
			String name = f + " " + l;
			return new Person(name, a);
		});
	}

	@Property(tries = 100)
	boolean plusMinusSameNumberIsZero(@ForAll int aNumber) {
		System.out.println(aNumber);
		return aNumber - aNumber == 0;
	}

	@Property(tries = 100)
	boolean numbersBetween1and100(@ForAll("between1and100") long aNumber) {
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
	boolean aBigDecimal(@ForAll @DoubleRange(max = 100.0) @Scale(value = 4) BigDecimal aBigDecimal) {
		System.out.println(aBigDecimal);
		return true;
	}

	@Provide
	Arbitrary<Long> between1and100() {
		return Arbitraries.longInteger(1L, 100L);
	}

	@Property(tries = 10)
	boolean aListOfInts(@ForAll List<Integer> anIntList) {
		System.out.println(anIntList.toString());
		return true;
	}

	@Provide
	Arbitrary<List<Integer>> aList() {
		return Arbitraries.listOf(Arbitraries.integer(0, 10));
	}

	@Property(tries = 10)
	void anArrayOfInteger(@ForAll Integer[] array) {
		System.out.println(Arrays.asList(array));
	}

	@Property(tries = 10)
	void anArrayOfPrimitiveInts(@ForAll @Size(max = 10) @IntRange(min = 1, max = 5) int[] array) {
		List<Integer> asList = IntStream.of(array).mapToObj(Integer::valueOf).collect(Collectors.toList());
		System.out.println(asList);
	}

	@Property(tries = 10)
	boolean aPeopleList(@ForAll List<Person> people) {
		System.out.println(people);
		return people != null;
	}

	@Property(tries = 100)
	boolean aPeopleSet(@ForAll Set<Person> people) {
		System.out.println(people);
		return people != null;
	}

	@Property(tries = 10)
	boolean aPeopleStream(@ForAll Stream<Person> people) {
		return people.peek(p -> System.out.println(p)).allMatch(person -> person.getAge() >= 0);
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
