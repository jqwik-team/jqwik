package net.jqwik.execution.properties;

import java.lang.reflect.*;
import java.util.*;

import javaslang.test.*;

public interface ArbitraryProvider {
	Optional<Arbitrary<Object>> forParameter(Parameter parameter);
}
