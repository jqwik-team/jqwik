package net.jqwik.execution;

import java.lang.reflect.Parameter;
import java.util.Optional;

import net.jqwik.api.Arbitrary;

public interface ArbitraryResolver {
	Optional<Arbitrary<Object>> forParameter(Parameter parameter);
}
