package net.jqwik.properties.arbitraries;

import javaslang.*;
import net.jqwik.properties.*;

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

		public <R> Arbitrary<R> as(Function2<T1, T2, R> combinator) {
			return (seed, tries) -> {
				RandomGenerator<T1> g1 = a1.generator(seed, tries);
				RandomGenerator<T2> g2 = a2.generator(seed, tries);
				return random -> {
					T1 t1 = g1.next(random);
					T2 t2 = g2.next(random);
					return combinator.apply(t1, t2);
				};
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

		public <R> Arbitrary<R> as(Function3<T1, T2, T3, R> combinator) {
			return (seed, tries) -> {
				RandomGenerator<T1> g1 = a1.generator(seed, tries);
				RandomGenerator<T2> g2 = a2.generator(seed, tries);
				RandomGenerator<T3> g3 = a3.generator(seed, tries);
				return random -> {
					T1 t1 = g1.next(random);
					T2 t2 = g2.next(random);
					T3 t3 = g3.next(random);
					return combinator.apply(t1, t2, t3);
				};
			};
		}
	}

	public static <T1, T2, T3, T4> Combinator4<T1, T2, T3, T4> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3,
			Arbitrary<T4> a4) {
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

		public <R> Arbitrary<R> as(Function4<T1, T2, T3, T4, R> combinator) {
			return (seed, tries) -> {
				RandomGenerator<T1> g1 = a1.generator(seed, tries);
				RandomGenerator<T2> g2 = a2.generator(seed, tries);
				RandomGenerator<T3> g3 = a3.generator(seed, tries);
				RandomGenerator<T4> g4 = a4.generator(seed, tries);
				return random -> {
					T1 t1 = g1.next(random);
					T2 t2 = g2.next(random);
					T3 t3 = g3.next(random);
					T4 t4 = g4.next(random);
					return combinator.apply(t1, t2, t3, t4);
				};
			};
		}
	}
}
