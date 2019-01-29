package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.*;

import static net.jqwik.engine.properties.ArbitraryTestHelper.*;

@Group
@Label("DefaultTypeArbitrary")
class DefaultTypeArbitraryTests {

	@Group
	class DirectUses {

		@Example
		void useConstructorWithoutParameter() throws NoSuchMethodException {

			TypeArbitrary<String> typeArbitrary =
				new DefaultTypeArbitrary<>(String.class)
					.use(String.class.getConstructor());

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aString -> {return aString.equals("");}
			);
		}

		@Example
		void useSingleFactoryWithoutParameter() throws NoSuchMethodException {

			TypeArbitrary<String> typeArbitrary =
				new DefaultTypeArbitrary<>(String.class)
					.use(Samples.class.getDeclaredMethod("stringFromNoParams"));

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aString -> {return aString.equals("a string");}
			);
		}

		@Example
		void twoCreatorsAreUsedRandomly() throws NoSuchMethodException {

			TypeArbitrary<String> typeArbitrary =
				new DefaultTypeArbitrary<>(String.class)
					.use(Samples.class.getDeclaredMethod("stringFromNoParams"))
					.use(String.class.getConstructor());

			RandomGenerator<String> generator = typeArbitrary.generator(1000);

			assertAllGenerated(
				generator,
				aString -> aString.equals("") || aString.equals("a string")
			);

			assertAtLeastOneGeneratedOf(generator, "", "a string");
		}

		@Example
		void exceptionsDuringCreationAreIgnored() throws NoSuchMethodException {
			TypeArbitrary<String> typeArbitrary =
				new DefaultTypeArbitrary<>(String.class)
					.use(Samples.class.getDeclaredMethod("stringWithRandomException"));

			RandomGenerator<String> generator = typeArbitrary.generator(1000);

			assertAllGenerated(
				generator,
				aString -> {
					return aString.equals("a string");
				}
			);
		}

		@Example
		void useConstructorWithOneParameter() throws NoSuchMethodException {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class)
					.use(Person.class.getConstructor(String.class));

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.toString().length() <= 1000
			);
		}

		@Example
		void useConstructorWithTwoParameters() throws NoSuchMethodException {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class)
					.use(Person.class.getConstructor(String.class, int.class));

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.toString().length() <= 1000
			);
		}

		@Example
		void useFactoryMethodWithTwoParameters() throws NoSuchMethodException {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class)
					.use(Person.class.getDeclaredMethod("create", int.class, String.class));

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.toString().length() <= 1000
			);
		}
	}

	@Group
	class ConfigurationErrors {
		@Example
		void typeArbitraryWithoutUseFailsOnGeneration() throws NoSuchMethodException {
			TypeArbitrary<String> typeArbitrary = new DefaultTypeArbitrary<>(String.class);

			Assertions.assertThatThrownBy(
				() -> typeArbitrary.generator(1000)
			).isInstanceOf(JqwikException.class);
		}

		@Example
		void nonStaticMethodsAreNotSupported() {
			Assertions.assertThatThrownBy(
				() -> new DefaultTypeArbitrary<>(String.class)
						  .use(Samples.class.getDeclaredMethod("nonStaticMethod"))
			).isInstanceOf(JqwikException.class);
		}

		@Example
		void creatorWithWrongReturnTypeIsNotSupported() {
			Assertions.assertThatThrownBy(
				() -> new DefaultTypeArbitrary<>(TypeUsage.of(List.class, TypeUsage.of(int.class)))
						  .use(Samples.class.getDeclaredMethod("listOfStringsFromNoParams"))
			).isInstanceOf(JqwikException.class);
		}

		@Example
		void creatorWithParameterThatHasNoDefaultArbitraryWillThrowOnCreation() throws NoSuchMethodException {
			TypeArbitrary<Customer> typeArbitrary =
				new DefaultTypeArbitrary<>(Customer.class)
					.use(Customer.class.getConstructor(Person.class));

			RandomGenerator<Customer> generator = typeArbitrary.generator(1000);
			Assertions.assertThatThrownBy(
				() -> generator.next(SourceOfRandomness.current())
			).isInstanceOf(JqwikException.class);
		}

	}

	private static class Samples {

		private static String stringFromNoParams() {
			return "a string";
		}

		private static String stringWithRandomException() {
			if (SourceOfRandomness.current().nextDouble() > 0.5) {
				throw new AssertionError();
			}
			return "a string";
		}

		private static List<String> listOfStringsFromNoParams() {
			return Arrays.asList("a", "b");
		}

		private String nonStaticMethod() {
			return "a string";
		}
	}

	private static class Person {
		private final String name;

		public static Person create(int age, String name) {
			return new Person(name, age);
		}

		public Person(String name, int age) {
			this(name);
		}

		public Person(String name) {
			if (name.length() > 1000) throw new IllegalArgumentException();
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static class Customer {
		public Customer(Person person) { }
	}

}
