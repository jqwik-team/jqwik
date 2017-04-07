package net.jqwik.execution.properties;

import java.util.*;

import javaslang.test.*;
import net.jqwik.api.*;
import net.jqwik.api.properties.*;
import net.jqwik.api.properties.Property;
import org.assertj.core.api.*;

import static net.jqwik.execution.properties.TestHelper.generate;

public class GeneratorTests {

	@Property
	boolean optionalArbitraryCreatesSomeNullValues(@ForAll("optionalWithoutNull") Optional<Integer> anOptionalInt) {
		return anOptionalInt != null;
	}

	@Generate
	Arbitrary<Optional<Integer>> optionalWithoutNull() {
		Arbitrary<Integer> anInt = Generator.integer(1, 10);
		return Generator.optionalOf(anInt);
	}

	@Example
	void withNullInjectsNullValues() {
		Arbitrary<Integer> anInt = Generator.integer(1, 10);
		Arbitrary<Integer> anIntWithNull = Generator.withNull(anInt, 0.5);

		for (int i = 0; i < 1000; i++) {
			Integer value = generate(anIntWithNull);
			if (value == null)
				return;
		}

		Assertions.fail("Null should have been generated");
	}
}
