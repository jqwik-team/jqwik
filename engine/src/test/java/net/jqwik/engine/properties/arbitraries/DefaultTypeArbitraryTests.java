package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.*;

import static net.jqwik.engine.properties.ArbitraryTestHelper.*;

class DefaultTypeArbitraryTests {

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
	void useSingleStaticMethodWithoutParameter() throws NoSuchMethodException {

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
	@Disabled("currently failing") //TODO: fix
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
			aPerson -> aPerson.toString().length() <= 10
		);
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

		public Person(String name) {
			if (name.length() > 10) name = name.substring(0, 10);
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

}
