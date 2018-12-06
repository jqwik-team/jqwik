package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;

public interface DataResolver {
	Optional<Iterable<? extends Tuple>> forMethod(Method method);
}
