package net.jqwik.execution.properties;

import java.util.*;

import javaslang.test.*;
import net.jqwik.api.properties.*;
import net.jqwik.api.properties.Property;

public class GeneratorTests {

	@Property
	boolean unspecifiedOptionalIsAlwaysPresent(@ForAll("optionalWithoutNull") Optional<Integer> anOptionalInt) {
		return anOptionalInt.isPresent();
	}

	@Generate
	Arbitrary<Optional<Integer>> optionalWithoutNull() {
		Arbitrary<Integer> anInt = Generator.integer(1, 10);
		return Generator.optionalOf(anInt);
	}
}
