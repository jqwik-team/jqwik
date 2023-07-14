package net.jqwik.api;

import java.util.*;

import org.jspecify.annotations.*;

import net.jqwik.api.edgeCases.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
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
	void appendingBuilder(@ForAll Random random) {
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
				.use(name).in(PersonBuilder::withName)
				.use(age).in(PersonBuilder::withAge)
				.build(PersonBuilder::build);

		Person value = generateFirst(personArbitrary, random);
		assertThat(value.name).hasSize(10);
		assertThat(value.age).isBetween(0, 15);
	}

	@Property
	void useNullableArbitraryInBuilderMethods(@ForAll Random random) {
		Arbitrary<String> name = Arbitraries.strings().injectNull(1.0);
		Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);

		Arbitrary<Person> personArbitrary =
			Builders
				.withBuilder(PersonBuilder::new)
				.use(name).in(PersonBuilder::withName)
				.use(age).in(PersonBuilder::withAge)
				.build(PersonBuilder::build);

		Person value = generateFirst(personArbitrary, random);
		assertThat(value.name).isNull();
		assertThat(value.age).isBetween(0, 15);
	}

	@Property
	void useBuilderMethodsWithProbability0and1(@ForAll Random random) {
		Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);
		Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);

		Arbitrary<Person> personArbitrary =
			Builders
				.withBuilder(PersonBuilder::new)
				.use(name).withProbability(0).in(PersonBuilder::withName)
				.use(age).withProbability(1).in(PersonBuilder::withAge)
				.build(PersonBuilder::build);

		Person value = generateFirst(personArbitrary, random);
		assertThat(value.name).isEqualTo(PersonBuilder.DEFAULT_NAME);
		assertThat(value.age).isBetween(0, 15);
	}

	@Example
	void useBuilderMethodsWithProbabilities(@ForAll Random random) {
		Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);
		Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);

		Arbitrary<Person> personArbitrary =
			Builders
				.withBuilder(PersonBuilder::new)
				.use(name).withProbability(0.5).in(PersonBuilder::withName)
				.use(age).withProbability(0.5).in(PersonBuilder::withAge)
				.build(PersonBuilder::build);

		TestingSupport.checkAtLeastOneGenerated(
			personArbitrary.generator(1000),
			random,
			(Person person) -> person.name.equals(PersonBuilder.DEFAULT_NAME)
		);
		TestingSupport.checkAtLeastOneGenerated(
			personArbitrary.generator(1000),
			random,
			(Person person) -> !person.name.equals(PersonBuilder.DEFAULT_NAME)
		);
		TestingSupport.checkAtLeastOneGenerated(
			personArbitrary.generator(1000),
			random,
			(Person person) -> person.age == 42
		);
		TestingSupport.checkAtLeastOneGenerated(
			personArbitrary.generator(1000),
			random,
			(Person person) -> person.age != 42
		);
	}

	@Property
	void buildWithoutFunctionUsesIdentityAsDefault(@ForAll Random random) {
		Arbitrary<Person> personArbitrary =
			Builders
				.withBuilder(() -> new Person("john", 42))
				.build();

		checkAllGenerated(
			personArbitrary.generator(1, true),
			random,
			person -> person.age == 42 && person.name.equals("john")
		);
	}

	@Property
	void builderIsFreshlyCreatedForEachTry(@ForAll Random random) {
		Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);

		Arbitrary<Person> personArbitrary =
				Builders
						.withBuilder(PersonBuilder::new)
						.use(name).in(PersonBuilder::withName)
						.build(PersonBuilder::build);

		checkAllGenerated(
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

	@Group
	@PropertyDefaults(tries = 100)
	class Shrinking {

		@Property
		void shrinkToSmallestValues(@ForAll Random random) {
			Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);
			Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);

			Arbitrary<Person> personArbitrary =
				Builders
					.withBuilder(PersonBuilder::new)
					.use(name).in(PersonBuilder::withName)
					.use(age).in(PersonBuilder::withAge)
					.build(PersonBuilder::build);

			Person person = falsifyThenShrink(personArbitrary, random);
			assertThat(person).isEqualTo(new Person("AAAAAAAAAA", 0));
		}

		@Property
		void shrinkMaybeUsesToNotUsed(@ForAll Random random) {
			Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);
			Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);

			Arbitrary<Person> personArbitrary =
				Builders
					.withBuilder(PersonBuilder::new)
					.use(name).in(PersonBuilder::withName)
					.use(age).withProbability(0.5).in(PersonBuilder::withAge)
					.build(PersonBuilder::build);

			Person person = falsifyThenShrink(personArbitrary, random);
			assertThat(person).isEqualTo(new Person("AAAAAAAAAA", 42));
		}

		@Property
		void shrinkAllUses(@ForAll Random random) {
			Arbitrary<Integer> digits = Arbitraries.integers().between(0, 9);

			Arbitrary<String> arbitrary =
				Builders
					.withBuilder(StringBuilder::new)
					.use(digits).in(StringBuilder::append)
					.use(digits).in(StringBuilder::append)
					.use(digits).in(StringBuilder::append)
					.build(StringBuilder::toString);

			Falsifier<String> falsifier = aString -> {
				if (aString.charAt(0) > '4' && aString.charAt(2) > '5') {
					return TryExecutionResult.falsified(null);
				}
				return TryExecutionResult.satisfied();
			};
			String string = falsifyThenShrink(arbitrary, random, falsifier);
			assertThat(string).isEqualTo("506");
		}

		@Property(tries = 5)
		void shrinkingBigBuilder(@ForAll Random random) {
			Builders.BuilderCombinator<Integer[]> combinator = bigBuilder(200);
			Arbitrary<Integer[]> arbitrary = combinator.build();

			Integer[] array = falsifyThenShrink(arbitrary, random);
			assertThat(array).containsOnly(1);
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void combineUsingValues() {
			Arbitrary<String> name = Arbitraries.of("John", "Lisa", "Kay");
			Arbitrary<Integer> age = Arbitraries.of(3, 5, 13);
			Arbitrary<Person> arbitrary =
				Builders
					.withBuilder(PersonBuilder::new)
					.use(name).in(PersonBuilder::withName)
					.use(age).in(PersonBuilder::withAge)
					.build(PersonBuilder::build);

			Optional<ExhaustiveGenerator<Person>> optionalGenerator = arbitrary.exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Person> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(9);

			assertThat(generator).containsExactly(
				new Person("John", 3), new Person("John", 5), new Person("John", 13),
				new Person("Lisa", 3), new Person("Lisa", 5), new Person("Lisa", 13),
				new Person("Kay", 3), new Person("Kay", 5), new Person("Kay", 13)
			);
		}

		@Example
		void combineUsingValuesWithProbability_failingDueToWrongGenerationInCombinators() {
			Arbitrary<String> name = Arbitraries.of("John", "Lisa", "Kay");
			Arbitrary<Integer> age = Arbitraries.of(3, 5, 13);
			Arbitrary<Person> arbitrary =
				Builders
					.withBuilder(PersonBuilder::new)
					.use(name).in(PersonBuilder::withName)
					.use(age).withProbability(0.5).in(PersonBuilder::withAge)
					.build(PersonBuilder::build);

			Optional<ExhaustiveGenerator<Person>> optionalGenerator = arbitrary.exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Person> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(12);

			assertThat(generator).containsExactlyInAnyOrder(
				new Person("John", 3), new Person("John", 5), new Person("John", 13), new Person("John", 42),
				new Person("Lisa", 3), new Person("Lisa", 5), new Person("Lisa", 13),new Person("Lisa", 42),
				new Person("Kay", 3), new Person("Kay", 5), new Person("Kay", 13), new Person("Kay", 42)
			);
		}

		@Example
		void withAppendingBuilder() {
			Arbitrary<String> string = Arbitraries.of("a", "b", "c");
			Arbitrary<Integer> digit = Arbitraries.of(1, 2, 3);
			Arbitrary<String> arbitrary =
				Builders
					.withBuilder(StringBuilder::new)
					.use(string).in(StringBuilder::append)
					.use(digit).in(StringBuilder::append)
					.build(StringBuilder::toString);

			Optional<ExhaustiveGenerator<String>> optionalGenerator = arbitrary.exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<String> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(9);

			assertThat(generator).containsExactly(
				"a1", "a2", "a3",
				"b1", "b2", "b3",
				"c1", "c2", "c3"
			);
		}

	}

	@Group
	class GenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Person> simpleBuilder =
				Builders
					.withBuilder(PersonBuilder::new)
					.build(PersonBuilder::build);

			Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);
			Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);
			Arbitrary<Person> builder =
				Builders
					.withBuilder(PersonBuilder::new)
					.use(name).in(PersonBuilder::withName)
					.use(age).in(PersonBuilder::withAge)
					.build(PersonBuilder::build);

			Arbitrary<Integer> digit = Arbitraries.of(1, 2, 3);
			Arbitrary<String> appendingBuilder =
				Builders
					.withBuilder(StringBuilder::new)
					.use(digit).in(StringBuilder::append)
					.use(digit).in(StringBuilder::append)
					.build(StringBuilder::toString);

			return Arbitraries.of(simpleBuilder, builder, appendingBuilder);
		}
	}

	@Group
	@PropertyDefaults(tries = 1000)
	class EdgeCasesGeneration implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Person> simpleBuilder =
				Builders
					.withBuilder(PersonBuilder::new)
					.build(PersonBuilder::build);

			Arbitrary<String> name = Arbitraries.strings().alpha().ofLength(10);
			Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);
			Arbitrary<Person> builder =
				Builders
					.withBuilder(PersonBuilder::new)
					.use(name).in(PersonBuilder::withName)
					.use(age).in(PersonBuilder::withAge)
					.build(PersonBuilder::build);

			Arbitrary<Integer> digit = Arbitraries.of(1, 2, 3);
			Arbitrary<String> appendingBuilder =
				Builders
					.withBuilder(StringBuilder::new)
					.use(digit).in(StringBuilder::append)
					.use(digit).in(StringBuilder::append)
					.build(StringBuilder::toString);

			return Arbitraries.of(simpleBuilder, builder, appendingBuilder);
		}

		@Example
		void edgeCases() {
			Arbitrary<String> name = Arbitraries.strings().withCharRange('a', 'z').ofLength(10);
			Arbitrary<Integer> age = Arbitraries.integers().between(0, 15);
			Arbitrary<Person> arbitrary =
				Builders
					.withBuilder(PersonBuilder::new)
					.use(name).in(PersonBuilder::withName)
					.use(age).in(PersonBuilder::withAge)
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
		void edgeCasesFromAppendingBuilder() {
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

		@Example
		void edgeCasesFromBigBuilder() {
			Builders.BuilderCombinator<Integer[]> builderCombinator = bigBuilder(100);
			Arbitrary<Integer[]> arbitrary = builderCombinator.build();

			assertThat(arbitrary.edgeCases(1).size()).isEqualTo(1);
			assertThat(arbitrary.edgeCases(100).size()).isEqualTo(100);
			assertThat(arbitrary.edgeCases(10000).size()).isEqualTo(10000);
		}
	}

	private Builders.BuilderCombinator<Integer[]> bigBuilder(int size) {
		Arbitrary<Integer> digits = Arbitraries.integers().between(1, 1000);
		Builders.BuilderCombinator<Integer[]> combinator = Builders.withBuilder(() -> new Integer[size]);
		for (int i = 0; i < size; i++) {
			int index = i;
			combinator = combinator.use(digits).inSetter((a, v) -> a[index] = v);
		}
		return combinator;
	}

	private static class Person {

		@Nullable private String name;
		private int age;

		Person(@Nullable String name, int age) {
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
			return Objects.equals(name, person.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, age);
		}

		@Override
		public String toString() {
			String sb = "Person{" + "name='" + name + '\'' +
							", age=" + age +
							'}';
			return sb;
		}
	}

	private static class PersonBuilder {

		static final int DEFAULT_AGE = 42;
		static final String DEFAULT_NAME = "A name";
		@Nullable private String name = DEFAULT_NAME;
		private int age = DEFAULT_AGE;

		public PersonBuilder withName(@Nullable String name) {
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
