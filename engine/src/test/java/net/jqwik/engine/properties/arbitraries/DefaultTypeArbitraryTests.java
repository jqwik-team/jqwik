package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;

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
				.use(getClass().getDeclaredMethod("stringFromNoParams"));

		assertAllGenerated(
			typeArbitrary.generator(1000),
			aString -> {return aString.equals("a string");}
		);
	}

	@Example
	void twoCreatorsAreUsedRandomly() throws NoSuchMethodException {

		TypeArbitrary<String> typeArbitrary =
			new DefaultTypeArbitrary<>(String.class)
				.use(getClass().getDeclaredMethod("stringFromNoParams"))
				.use(String.class.getConstructor());

		RandomGenerator<String> generator = typeArbitrary.generator(1000);

		assertAllGenerated(
			generator,
			aString -> aString.equals("") || aString.equals("a string")
		);

		assertAtLeastOneGeneratedOf(generator, "", "a string");
	}

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
					  .use(getClass().getDeclaredMethod("nonStaticMethod"))
		).isInstanceOf(JqwikException.class);
	}

	@Example
	void creatorWithWrongReturnTypeIsNotSupported() {
		Assertions.assertThatThrownBy(
			() -> new DefaultTypeArbitrary<>(TypeUsage.of(List.class, TypeUsage.of(int.class)))
					  .use(getClass().getDeclaredMethod("listOfStringsFromNoParams"))
		).isInstanceOf(JqwikException.class);
	}

	private static String stringFromNoParams() {
		return "a string";
	}

	private static List<String> listOfStringsFromNoParams() {
		return Arrays.asList("a", "b");
	}

	private String nonStaticMethod() {
		return "a string";
	}

}
