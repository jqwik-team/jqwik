package net.jqwik.execution.properties;

import javaslang.test.Arbitrary;

import java.lang.reflect.Parameter;
import java.util.Optional;

public interface ArbitraryProvider {
	Optional<Arbitrary<Object>> forParameter(Parameter parameter);
}
