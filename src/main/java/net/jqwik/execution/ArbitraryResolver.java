package net.jqwik.execution;

import net.jqwik.newArbitraries.*;
import net.jqwik.properties.*;

import java.lang.reflect.*;
import java.util.*;

public interface ArbitraryResolver {
	Optional<NArbitrary<Object>> forParameter(Parameter parameter);
}
