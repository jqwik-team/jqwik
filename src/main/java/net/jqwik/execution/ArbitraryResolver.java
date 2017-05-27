package net.jqwik.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.properties.*;

public interface ArbitraryResolver {
	Optional<Arbitrary<Object>> forParameter(Parameter parameter);
}
