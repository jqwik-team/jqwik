package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.Tuple.*;

public class Table {
	public static <E> Iterable<Tuple1<E>> of(E... elements) {
		List<Tuple1<E>> tuples =
			Arrays.stream(elements)
				  .map(Tuple::of)
				  .collect(Collectors.toList());
		return tuples;
	}

	public static <T> Iterable<Tuple1<T>> of(Tuple1<T>... tuples) {
		return Arrays.asList(tuples);
	}
}
