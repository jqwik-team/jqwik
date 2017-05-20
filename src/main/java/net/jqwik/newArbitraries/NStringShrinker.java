package net.jqwik.newArbitraries;

import java.util.*;

public class NStringShrinker implements NShrinker<String> {

	@Override
	public Set<String> shrink(String value) {
		if (value.isEmpty()) return Collections.emptySet();
		Set<String> strings = new HashSet<>();
		strings.add(value.substring(0, value.length() - 1));
		strings.add(value.substring(1, value.length()));
		return strings;
	}

	@Override
	public int distance(String value) {
		return value.length();
	}
}
