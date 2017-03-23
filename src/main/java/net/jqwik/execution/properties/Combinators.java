package net.jqwik.execution.properties;

import javaslang.Function3;
import javaslang.test.Arbitrary;

public class Combinators {

	public static <T1, T2, T3> Combinator3<T1, T2, T3> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3) {
		return new Combinator3<T1, T2, T3>(a1, a2, a3);
	}

	public static class Combinator3<T1, T2, T3> {
		private final Arbitrary<T1> a1;
		private final Arbitrary<T2> a2;
		private final Arbitrary<T3> a3;

		Combinator3(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3) {
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
}
