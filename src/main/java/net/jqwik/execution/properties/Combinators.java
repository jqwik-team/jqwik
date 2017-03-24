package net.jqwik.execution.properties;

import javaslang.*;
import javaslang.test.*;

public class Combinators {

	public static <T1, T2> Combinator2<T1, T2> combine(Arbitrary<T1> a1, Arbitrary<T2> a2) {
		return new Combinator2<T1, T2>(a1, a2);
	}

	public static class Combinator2<T1, T2> {
		private final Arbitrary<T1> a1;
		private final Arbitrary<T2> a2;

		private Combinator2(Arbitrary<T1> a1, Arbitrary<T2> a2) {
			this.a1 = a1;
			this.a2 = a2;
		}

		public <R> Arbitrary<R> as(Function2<T1,T2, R> combinator) {
			return size -> random -> {
				T1 t1 = a1.apply(size).apply(random);
				T2 t2 = a2.apply(size).apply(random);
				return combinator.apply(t1, t2);
			};
		}
	}

	public static <T1, T2, T3> Combinator3<T1, T2, T3> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3) {
		return new Combinator3<T1, T2, T3>(a1, a2, a3);
	}

	public static class Combinator3<T1, T2, T3> {
		private final Arbitrary<T1> a1;
		private final Arbitrary<T2> a2;
		private final Arbitrary<T3> a3;

		private Combinator3(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
		}

		public <R> Arbitrary<R> as(Function3<T1,T2, T3, R> combinator) {
			return size -> random -> {
				T1 t1 = a1.apply(size).apply(random);
				T2 t2 = a2.apply(size).apply(random);
				T3 t3 = a3.apply(size).apply(random);
				return combinator.apply(t1, t2, t3);
			};
		}
	}

	public static <T1, T2, T3, T4> Combinator4<T1, T2, T3, T4> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4) {
		return new Combinator4<T1, T2, T3, T4>(a1, a2, a3, a4);
	}

	public static class Combinator4<T1, T2, T3, T4> {
		private final Arbitrary<T1> a1;
		private final Arbitrary<T2> a2;
		private final Arbitrary<T3> a3;
		private final Arbitrary<T4> a4;

		private Combinator4(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
			this.a4 = a4;
		}

		public <R> Arbitrary<R> as(Function4<T1,T2, T3, T4, R> combinator) {
			return size -> random -> {
				T1 t1 = a1.apply(size).apply(random);
				T2 t2 = a2.apply(size).apply(random);
				T3 t3 = a3.apply(size).apply(random);
				T4 t4 = a4.apply(size).apply(random);
				return combinator.apply(t1, t2, t3, t4);
			};
		}
	}
}
