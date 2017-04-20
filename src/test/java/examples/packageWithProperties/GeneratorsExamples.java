package examples.packageWithProperties;

import net.jqwik.api.*;
import net.jqwik.properties.*;

import java.util.*;
import java.util.stream.*;

public class GeneratorsExamples {

	@Property(tries = 5)
	boolean aString(@ForAll String aString, @ForAll(value = "digitsOnly") String anotherString) {
		System.out.println(String.format("#%s# #%s#", aString, anotherString));
		return true;
	}

	@Generate
	Arbitrary<String> stringArbitrary() {
		return Generator.string('a', 'z');
	}

	@Generate
	Arbitrary<String> digitsOnly() {
		return Generator.string('0', '9');
	}

	@Property(tries = 20)
	boolean aPersonIsNeverYoungerThan0(@ForAll Person aPerson) {
		System.out.println(aPerson);
		return aPerson.getAge() > 0;
	}

	@Generate
	Arbitrary<Person> aValidPerson() {
		Arbitrary<Integer> age = Generator.integer(0, 100);
		Arbitrary<String> first = Generator.string('a', 'z', 10).filter(f -> !f.isEmpty());
		Arbitrary<String> last = Generator.string('a', 'z', 15).filter(f -> !f.isEmpty());

		return Generator.combine(age, first, last).as((a, f, l) -> {
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

	@Generate
	Arbitrary<Long> between1and100() {
		return Generator.integer(1L, 100L);
	}

	@Property(tries = 10)
	boolean aListOfInts(@ForAll List<Integer> anIntList) {
		System.out.println(anIntList.toString());
		return true;
	}

	@Generate
	Arbitrary<List<Integer>> aList() {
		return Generator.listOf(Generator.integer(0, 10));
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
