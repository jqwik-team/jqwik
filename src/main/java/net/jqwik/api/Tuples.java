package net.jqwik.api;

public class Tuples {

	/**
	 * @deprecated Use {@link Tuple#of(Object, Object)} instead.
	 */
	@Deprecated
	public static <T1, T2> Tuple2<T1, T2> tuple(T1 v1, T2 v2) {
		return new Tuple2<T1, T2>(v1, v2);
	}

	/**
	 * @deprecated Use {@link Tuple#of(Object, Object, Object)} instead.
	 */
	@Deprecated
	public static <T1, T2, T3> Tuple3<T1, T2, T3> tuple(T1 v1, T2 v2, T3 v3) {
		return new Tuple3<T1, T2, T3>(v1, v2, v3);
	}

	/**
	 * @deprecated Use {@link Tuple#of(Object, Object, Object, Object)} instead.
	 */
	@Deprecated
	public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> tuple(T1 v1, T2 v2, T3 v3, T4 v4) {
		return new Tuple4<T1, T2, T3, T4>(v1, v2, v3, v4);
	}

	/**
	 * @deprecated Use {@link Tuple.Tuple2} instead.
	 */
	@Deprecated
	public static class Tuple2<T1, T2> extends Tuple.Tuple2<T1, T2> {
		private Tuple2(T1 v1, T2 v2) {
			super(v1, v2);
		}
	}

	/**
	 * @deprecated Use {@link Tuple.Tuple3} instead.
	 */
	@Deprecated
	public static class Tuple3<T1, T2, T3> extends Tuple.Tuple3<T1, T2, T3> {
		private Tuple3(T1 v1, T2 v2, T3 v3) {
			super(v1, v2, v3);
		}
	}

	/**
	 * @deprecated Use {@link Tuple.Tuple4} instead.
	 */
	@Deprecated
	public static class Tuple4<T1, T2, T3, T4> extends Tuple.Tuple4<T1, T2, T3, T4> {
		private Tuple4(T1 v1, T2 v2, T3 v3, T4 v4) {
			super(v1, v2, v3, v4);
		}
	}
}



