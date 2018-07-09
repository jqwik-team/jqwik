package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.support.*;

import java.util.*;

public interface ArbitraryResolver {
	Set<Arbitrary<?>> forParameter(MethodParameter parameter);
}
