package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public interface ArbitraryResolver {
	Set<Arbitrary<?>> forParameter(MethodParameter parameter);
}
