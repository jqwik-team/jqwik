package net.jqwik.api;

import java.util.*;

import net.jqwik.api.edgeCases.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 50)
class BuildersTests {

	@Property
	void plainBuilder(@ForAll Random random) {
		Arbitrary<Person> personArbitrary =
				Builders
						.withBuilder(PersonBuilder::new)
						.build(PersonBuilder::build);

		Person value = generateFirst(personArbitrary, random);
		assertThat(value.age).isEqualTo(PersonBuilder.DEFAULT_AGE);
		assertThat(value.name).isEqualTo(PersonBuilder.DEFAULT_NAME);
	}

	@Property
	void plainBuilderWithArbitrary(@ForAll Random random) {
		Arbitrary<Person> personArbitrary =
				Builders
						.withBuilder(Arbitraries.create(PersonBuilder::new))
						.build(PersonBuilder::build);

		Person value = generateFirst(personArbitrary, random);
		assertThat(value.age).isEqualTo(PersonBuilder.DEFAULT_AGE);
		assertThat(value.name).isEqualTo(PersonBuilder.DEFAULT_NAME);
	}

	@Property
	void statefulBuilder(@ForAll Random random) {
		Arbitrary<String> digits = Arbitraries.of("0", "1", "2");

		Arbitrary<String> arbitrary =
				Builders
						.withBuilder(StringBuilder::new)
						.use(digits).in(StringBuilder::append)
						.use(digits).in(StringBuilder::append)
						.build(StringBuilder::toString);

		String value = generateFirst(arbitrary, random);
		assertThat(value).hasSize(2);
		assertThat(value).isIn("00", "01", "02", "10", "11", "12", "20", "21", "22");
	}

	@Property
	void useBuilderMethods(@ForAll Random random) {
		Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);
		Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);

		Arbitrary<Person> personArbitrary =
				Builders
						.withBuilder(PersonBuilder::new)
						.use(name).in((b, n) -> b.withName(n))
						.use(age).in((b, a) -> b.withAge(a))
						.build(PersonBuilder::build);

		Person value = generateFirst(personArbitrary, random);
		assertThat(value.age).isBetween(0, 15);
		assertThat(value.name).hasSize(10);
	}

	@Property
	void buildWithoutFunctionUsesIdentityAsDefault(@ForAll Random random) {
		Arbitrary<Person> personArbitrary =
			Builders
				.withBuilder(() -> new Person("john", 42))
				.build();

		assertAllGenerated(
			personArbitrary.generator(1, true),
			random,
			person -> person.age == 42 && person.name.equals("john")
		);
	}

	@Property
	void startWithArbitrary(@ForAll Random random) {
		Arbitrary<Integer> digit = Arbitraries.of(1, 2, 3);
		Arbitrary<String> stringBuilders = Arbitraries.of("a", "b", "c");
		Arbitrary<String> arbitrary =
			Builders
				.withBuilder(stringBuilders)
				.use(digit).in((s, d) -> s + d)
				.build();

		assertAllGenerated(
			arbitrary.generator(1, true),
				random,
				(String value) -> assertThat(value).matches("(a|b|c)(1|2|3)")
		);
	}

	// @Property
	// TODO: Fixing this probably requires major changes in edge case generation for flat mapped arbitraries
	void startWithArbitrary_failingDueToUnsolvedProblemWithFlatMappingEdgeCases(@ForAll Random random) {
		Arbitrary<Integer> digit = Arbitraries.of(1, 2, 3);
		Arbitrary<StringBuilder> stringBuilders = Arbitraries.of("a", "b", "c").map(StringBuilder::new);

		Arbitrary<String> personArbitrary =
				Builders
						.withBuilder(stringBuilders)
						.use(digit).in((b, d) -> b.append(d))
						.build(b -> b.toString());

		assertAllGenerated(
				personArbitrary.generator(1, true),
				random,
				(String value) -> assertThat(value).matches("(a|b|c)(1|2|3)")
		);
	}

	@Property
	void builderIsFreshlyCreatedForEachTry(@ForAll Random random) {
		Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);

		Arbitrary<Person> personArbitrary =
				Builders
						.withBuilder(PersonBuilder::new)
						.use(name).in((b, n) -> b.withName(n))
						.build(PersonBuilder::build);

		assertAllGenerated(
				personArbitrary.generator(1, true),
				random,
				person -> person.age == PersonBuilder.DEFAULT_AGE
		);
	}

	@Property
	void useInSetter(@ForAll Random random) {
		Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);
		Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);

		Arbitrary<Person> personArbitrary =
				Builders
						.withBuilder(() -> new Person("", 0))
						.use(name).inSetter(Person::setName)
						.use(age).inSetter(Person::setAge)
						.build();

		Person value = generateFirst(personArbitrary, random);
		assertThat(value.age).isBetween(0, 15);
		assertThat(value.name).hasSize(10);
	}

	// Test maybeUse

	// Test shrinking

	@Group
	@PropertyDefaults(tries = 1000)
	class EdgeCasesGeneration implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Person> simpleBuilder =
				Builders
					.withBuilder(Arbitraries.create(PersonBuilder::new))
					.build(PersonBuilder::build);

			Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);
			Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);
			Arbitrary<Person> builder =
				Builders
					.withBuilder(PersonBuilder::new)
					.use(name).in((b, n) -> b.withName(n))
					.use(age).in((b, a) -> b.withAge(a))
					.build(PersonBuilder::build);

			Arbitrary<Integer> digit = Arbitraries.of(1, 2, 3);
			Arbitrary<StringBuilder> stringBuilders = Arbitraries.of("a", "b", "c").map(StringBuilder::new);
			Arbitrary<String> fromArbitraryBuilder =
				Builders
					.withBuilder(stringBuilders)
					.use(digit).in((b, d) -> b.append(d))
					.build(b -> b.toString());

			return Arbitraries.of(simpleBuilder, builder, fromArbitraryBuilder);
		}

		@Example
		void edgeCases() {
			Arbitrary<String> name = Arbitraries.strings().withCharRange('a', 'z').ofLength(10);
			Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);
			Arbitrary<Person> arbitrary =
				Builders
					.withBuilder(PersonBuilder::new)
					.use(name).in((b, n) -> b.withName(n))
					.use(age).in((b, a) -> b.withAge(a))
					.build(PersonBuilder::build);

			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				new Person("aaaaaaaaaa", 0),
				new Person("aaaaaaaaaa", 1),
				new Person("aaaaaaaaaa", 2),
				new Person("aaaaaaaaaa", 14),
				new Person("aaaaaaaaaa", 15),
				new Person("zzzzzzzzzz", 0),
				new Person("zzzzzzzzzz", 1),
				new Person("zzzzzzzzzz", 2),
				new Person("zzzzzzzzzz", 14),
				new Person("zzzzzzzzzz", 15)
			);

			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).hasSize(10);
		}

		@Example
		void edgeCasesFromBuilderWithArbitrary() {
			Arbitrary<Integer> digit = Arbitraries.of(1, 2, 3);
			Arbitrary<String> stringBuilders = Arbitraries.of("a", "b", "c");
			Arbitrary<String> arbitrary =
				Builders
					.withBuilder(stringBuilders)
					.use(digit).in((s, d) -> s + d)
					.build();

			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				"a1", "a3", "c1", "c3"
			);
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				"a1", "a3", "c1", "c3"
			);
		}

		//@Example
		void edgeCasesFromStatefulBuilder_failingDueToUnsolvedProblemWithFlatMappingEdgeCases() {
			Arbitrary<String> digits = Arbitraries.of("0", "1", "2");

			Arbitrary<String> arbitrary =
				Builders
					.withBuilder(StringBuilder::new)
					.use(digits).in(StringBuilder::append)
					.use(digits).in(StringBuilder::append)
					.build(StringBuilder::toString);

			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				"00", "02", "20", "22"
			);
		}
	}

	private static class Person {

		private String name;
		private int age;

		Person(String name, int age) {
			this.name = name;
			this.age = age;
		}

		void setName(String newName) {
			this.name = newName;
		}

		void setAge(int newAge) {
			this.age = newAge;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Person person = (Person) o;

			if (age != person.age) return false;
			if (!name.equals(person.name)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = name.hashCode();
			result = 31 * result + age;
			return result;
		}

		@Override
		public String toString() {
			final StringBuffer sb = new StringBuffer("Person{");
			sb.append("name='").append(name).append('\'');
			sb.append(", age=").append(age);
			sb.append('}');
			return sb.toString();
		}
	}

	private static class PersonBuilder {

		static final int DEFAULT_AGE = 42;
		static final String DEFAULT_NAME = "A name";
		private String name = DEFAULT_NAME;
		private int age = DEFAULT_AGE;

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
