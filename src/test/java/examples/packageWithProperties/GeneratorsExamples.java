package examples.packageWithProperties;

import javaslang.test.*;
import net.jqwik.api.properties.*;
import net.jqwik.api.properties.Property;

public class GeneratorsExamples {

	@Property(tries = 5)
	boolean aString(@ForAll(size = 10) String aString, @ForAll(value = "digitsOnly", size = 10) String anotherString) {
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
		Arbitrary<Integer> age = Arbitrary.integer().filter(a -> a >= 0 && a <= 100);
		Arbitrary<String> first = Generator.string('a', 'z', 10).filter(f -> !f.isEmpty());
		Arbitrary<String> last = Generator.string('a', 'z', 15).filter(f -> !f.isEmpty());

		return Generator.combine(age, first, last).as((a, f, l) -> {
			String name = f + " " + l;
			return new Person(name, a);
		});
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
