package net.jqwik.api;

import net.jqwik.engine.*;
import net.jqwik.engine.properties.*;

import static org.assertj.core.api.Assertions.*;

class CombinatorsBuilderTests {

	@Example
	void plainBuilder() {

		Arbitrary<Person> personArbitrary =
			Combinators
				.withBuilder(PersonBuilder::new)
				.build(PersonBuilder::build);

		Person value = TestHelper.generateFirst(personArbitrary);
		assertThat(value.age).isEqualTo(PersonBuilder.DEFAULT_AGE);
		assertThat(value.name).isEqualTo(PersonBuilder.DEFAULT_NAME);
	}

	@Example
	void useBuilderMethods() {

		Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);
		Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);

		Arbitrary<Person> personArbitrary =
			Combinators
				.withBuilder(PersonBuilder::new)
				.use(name).in((b, n) -> b.withName(n))
				.use(age).in((b, a) -> b.withAge(a))
				.build(PersonBuilder::build);

		Person value = TestHelper.generateFirst(personArbitrary);
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

	private static class Person {

		private final String name;
		private final int age;

		Person(String name, int age) {
			this.name = name;
			this.age = age;
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
