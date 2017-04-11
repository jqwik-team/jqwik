package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

public class GenericProperty {
	public GenericProperty(String name, Function<List[], Boolean> assumeFunction, List<Arbitrary> arbitraries,
			Function<List[], Boolean> forAllFunction) {
	}

	public PropertyCheckResult check(int tries, long seed) {
		return null;
	}
}
