package net.jqwik.execution;

import net.jqwik.properties.*;

import java.lang.reflect.*;
import java.util.*;

public interface ArbitraryProvider {
	Optional<Arbitrary<Object>> forParameter(Parameter parameter);
}
