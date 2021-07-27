package net.jqwik.api;

import java.util.*;

import net.jqwik.engine.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 10)
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

	@Disabled
	@Example
	void startWithArbitrary() {
		Arbitrary<Integer> digit = Arbitraries.of(1, 2, 3);
		Arbitrary<StringBuilder> stringBuilders = Arbitraries.of("a", "b", "c").map(StringBuilder::new);

		Arbitrary<String> personArbitrary =
				Builders
						.withBuilder(stringBuilders)
						.use(digit).in((b, d) -> b.append(d))
						.build(b -> b.toString());

		assertAllGenerated(
				personArbitrary.generator(1, true),
				SourceOfRandomness.current(),
				(String value) -> assertThat(value).matches("(a|b|c)(1|2|3)")
		);

	}

	@Disabled
	@Example
	void builderIsFreshlyCreatedForEachTry() {
		Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);

		Arbitrary<Person> personArbitrary =
				Builders
						.withBuilder(PersonBuilder::new)
						.use(name).in((b, n) -> b.withName(n))
						.build(PersonBuilder::build);

		assertAllGenerated(
				personArbitrary.generator(1, true),
				SourceOfRandomness.current(),
				person -> person.age == PersonBuilder.DEFAULT_AGE
		);
	}

	@Disabled
	@Example
	void buildWithoutFunctionUsesIdentityAsDefault() {
		Arbitrary<Person> personArbitrary =
				Builders
						.withBuilder(() -> new Person("john", 42))
						.build();

		assertAllGenerated(
				personArbitrary.generator(1, true),
				SourceOfRandomness.current(),
				person -> person.age == 42 && person.name.equals("john")
		);
	}

	@Disabled
	@Example
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
