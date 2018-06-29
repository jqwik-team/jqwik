package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.support.*;

import java.util.*;

public interface ArbitraryResolver {
	Optional<Arbitrary<Object>> forParameter(MethodParameter parameter);
}
