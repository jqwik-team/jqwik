package net.jqwik.api;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

class CombinatorsBuilderTests {

	@Example
	void plainBuilder(@ForAll Random random) {
		Arbitrary<Person> personArbitrary =
			Combinators
				.withBuilder(PersonBuilder::new)
				.build(PersonBuilder::build);

		Person value = generateFirst(personArbitrary, random);
		assertThat(value.age).isEqualTo(PersonBuilder.DEFAULT_AGE);
		assertThat(value.name).isEqualTo(PersonBuilder.DEFAULT_NAME);
	}

	@Example
	void useBuilderMethods(@ForAll Random random) {
		Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);
		Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);

		Arbitrary<Person> personArbitrary =
			Combinators
				.withBuilder(PersonBuilder::new)
				.use(name).in((b, n) -> b.withName(n))
				.use(age).in((b, a) -> b.withAge(a))
				.build(PersonBuilder::build);

		Person value = generateFirst(personArbitrary, random);
		assertThat(value.age).isBetween(0, 15);
		assertThat(value.name).hasSize(10);
	}

	@Example
	void startWithArbitrary() {
		Arbitrary<Integer> digit = Arbitraries.of(1, 2, 3);
		Arbitrary<StringBuilder> stringBuilders = Arbitraries.of("a", "b", "c").map(StringBuilder::new);

		Arbitrary<String> personArbitrary =
			Combinators
				.withBuilder(stringBuilders)
				.use(digit).in((b, d) -> b.append(d))
				.build(b -> b.toString());

		ArbitraryTestHelper.assertAllGenerated(personArbitrary.generator(1), (String value) -> {
			assertThat(value).matches("(a|b|c)(1|2|3)");
		});
	}

	@Example
	void builderIsFreshlyCreatedForEachTry() {
		Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);

		Arbitrary<Person> personArbitrary =
			Combinators
				.withBuilder(PersonBuilder::new)
				.use(name).in((b, n) -> b.withName(n))
				.build(PersonBuilder::build);

		ArbitraryTestHelper.assertAllGenerated(
			personArbitrary.generator(1),
			person -> person.age == PersonBuilder.DEFAULT_AGE
		);
	}

	@Example
	void buildWithoutFunctionUsesIdentityAsDefault() {
		Arbitrary<Person> personArbitrary =
			Combinators
				.withBuilder(() -> new Person("john", 42))
				.build();

		ArbitraryTestHelper.assertAllGenerated(
			personArbitrary.generator(1),
			person -> person.age == 42 && person.name.equals("john")
		);
	}

	@Example
	void useInSetter(@ForAll Random random) {
		Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);
		Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);

		Arbitrary<Person> personArbitrary =
			Combinators
				.withBuilder(() -> new Person("", 0))
				.use(name).inSetter(Person::setName)
				.use(age).inSetter(Person::setAge)
				.build();

		Person value = generateFirst(personArbitrary, random);
		assertThat(value.age).isBetween(0, 15);
		assertThat(value.name).hasSize(10);
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
