package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;
import org.assertj.core.api.*;

import java.util.*;

public class ArbitrariesTests {

	enum MyEnum {
		Yes, No, Maybe
	}

	private Random random = new Random();

	@Example
	void fromGenerator() {
		Arbitrary<String> stringArbitrary = Arbitraries.fromGenerator(random -> Integer.toString(random.nextInt(10)));

		RandomGenerator<String> generator = stringArbitrary.generator(1L, 1);

		assertIntStringLessThan10(generator.next(random));
		assertIntStringLessThan10(generator.next(random));
		assertIntStringLessThan10(generator.next(random));
		assertIntStringLessThan10(generator.next(random));
	}

	@Example
	void ofValues() {
		Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");

		RandomGenerator<String> generator = stringArbitrary.generator(1L, 1);

		Assertions.assertThat(generator.next(random)).isIn("1", "hallo", "test");
		Assertions.assertThat(generator.next(random)).isIn("1", "hallo", "test");
		Assertions.assertThat(generator.next(random)).isIn("1", "hallo", "test");
		Assertions.assertThat(generator.next(random)).isIn("1", "hallo", "test");
	}

	@Example
	void ofEnum() {
		Arbitrary<MyEnum> enumArbitrary = Arbitraries.of(MyEnum.class);

		RandomGenerator<MyEnum> generator = enumArbitrary.generator(1L, 1);

		Assertions.assertThat(generator.next(random)).isIn(MyEnum.class.getEnumConstants());
		Assertions.assertThat(generator.next(random)).isIn(MyEnum.class.getEnumConstants());
		Assertions.assertThat(generator.next(random)).isIn(MyEnum.class.getEnumConstants());
	}

	@Example
	void list() {
		Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
		Arbitrary<List<String>> listArbitrary = Arbitraries.list(stringArbitrary, 5);

		RandomGenerator<List<String>> generator = listArbitrary.generator(1L, 1);

		assertGeneratedList(generator.next(random));
		assertGeneratedList(generator.next(random));
		assertGeneratedList(generator.next(random));
		assertGeneratedList(generator.next(random));
	}

	private void assertGeneratedList(List<String> list) {
		Assertions.assertThat(list.size()).isBetween(0, 5);
		Assertions.assertThat(list).isSubsetOf("1", "hallo", "test");
	}

	private void assertIntStringLessThan10(String next) {
		Assertions.assertThat(next).isInstanceOf(String.class);
		Assertions.assertThat(Integer.parseInt(next)).isLessThan(10);
	}
}
