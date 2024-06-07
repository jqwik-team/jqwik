package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import net.jqwik.api.Tuple.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.0")
public class Table {
	@SafeVarargs
	public static <E extends @Nullable Object> Iterable<Tuple1<E>> of(E... elements) {
		return Arrays.stream(elements)
				 .map(Tuple::of)
				 .collect(Collectors.toList());
	}

	@SafeVarargs
	public static <T extends @Nullable Object> Iterable<Tuple1<T>> of(Tuple1<T>... tuples) {
		return Arrays.asList(tuples);
	}

	@SafeVarargs
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object> Iterable<Tuple2<T1, T2>> of(Tuple2<T1, T2>... tuples) {
		return Arrays.asList(tuples);
	}

	@SafeVarargs
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object> Iterable<Tuple3<T1, T2, T3>> of(Tuple3<T1, T2, T3>... tuples) {
		return Arrays.asList(tuples);
	}

	@SafeVarargs
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object> Iterable<Tuple4<T1, T2, T3, T4>> of(Tuple4<T1, T2, T3, T4>... tuples) {
		return Arrays.asList(tuples);
	}

	@SafeVarargs
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object> Iterable<Tuple5<T1, T2, T3, T4, T5>> of(Tuple5<T1, T2, T3, T4, T5>... tuples) {
		return Arrays.asList(tuples);
	}

	@SafeVarargs
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object> Iterable<Tuple6<T1, T2, T3, T4, T5, T6>> of(Tuple6<T1, T2, T3, T4, T5, T6>... tuples) {
		return Arrays.asList(tuples);
	}

	@SafeVarargs
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object, T7 extends @Nullable Object> Iterable<Tuple7<T1, T2, T3, T4, T5, T6, T7>> of(Tuple7<T1, T2, T3, T4, T5, T6, T7>... tuples) {
		return Arrays.asList(tuples);
	}

	@SafeVarargs
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object, T7 extends @Nullable Object, T8 extends @Nullable Object> Iterable<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> of(Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>... tuples) {
		return Arrays.asList(tuples);
	}
}
