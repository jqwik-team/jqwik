package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import net.jqwik.api.Tuple.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.0")
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

	public static <T1, T2> Iterable<Tuple2<T1, T2>> of(Tuple2<T1, T2>... tuples) {
		return Arrays.asList(tuples);
	}

	public static <T1, T2, T3> Iterable<Tuple3<T1, T2, T3>> of(Tuple3<T1, T2, T3>... tuples) {
		return Arrays.asList(tuples);
	}

	public static <T1, T2, T3, T4> Iterable<Tuple4<T1, T2, T3, T4>> of(Tuple4<T1, T2, T3, T4>... tuples) {
		return Arrays.asList(tuples);
	}

	public static <T1, T2, T3, T4, T5> Iterable<Tuple5<T1, T2, T3, T4, T5>> of(Tuple5<T1, T2, T3, T4, T5>... tuples) {
		return Arrays.asList(tuples);
	}

	public static <T1, T2, T3, T4, T5, T6> Iterable<Tuple6<T1, T2, T3, T4, T5, T6>> of(Tuple6<T1, T2, T3, T4, T5, T6>... tuples) {
		return Arrays.asList(tuples);
	}

	public static <T1, T2, T3, T4, T5, T6, T7> Iterable<Tuple7<T1, T2, T3, T4, T5, T6, T7>> of(Tuple7<T1, T2, T3, T4, T5, T6, T7>... tuples) {
		return Arrays.asList(tuples);
	}

	public static <T1, T2, T3, T4, T5, T6, T7, T8> Iterable<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> of(Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>... tuples) {
		return Arrays.asList(tuples);
	}
}
