package net.jqwik.properties.shrinking;

import java.util.*;

public class Shrinkers {
	public static Shrinker<Integer> range(int min, int max) {
		return new IntegerShrinker(min, max);
	}

	public static <T> Shrinker<List<T>> list(Shrinker<T> elementShrinker) {
		return new ListShrinker(elementShrinker);
	}
}
