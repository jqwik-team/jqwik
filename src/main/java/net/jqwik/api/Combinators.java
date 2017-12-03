package net.jqwik.api;

import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.function.*;

public class Combinators {

	private Combinators() {
	}

	public static <T1, T2> ACombinator2<T1, T2> combine(Arbitrary<T1> a1, Arbitrary<T2> a2) {
		return new ACombinator2<T1, T2>(a1, a2);
	}

	public static <T1, T2> GCombinator2<T1, T2> combine(RandomGenerator<T1> g1, RandomGenerator<T2> g2) {
		return new GCombinator2<T1, T2>(g1, g2);
	}

	public static <T1, T2, T3> Combinator3<T1, T2, T3> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3) {
		return new Combinator3<T1, T2, T3>(a1, a2, a3);
	}

	public static <T1, T2, T3, T4> Combinator4<T1, T2, T3, T4> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4) {
		return new Combinator4<T1, T2, T3, T4>(a1, a2, a3, a4);
	}

	public static class ACombinator2<T1, T2> {
		private final Arbitrary<T1> a1;
		private final Arbitrary<T2> a2;

		private ACombinator2(Arbitrary<T1> a1, Arbitrary<T2> a2) {
			this.a1 = a1;
			this.a2 = a2;
		}

		public <R> Arbitrary<R> as(F2<T1, T2, R> combinator) {
			return (tries) -> combine(a1.generator(tries), a2.generator(tries)).as(combinator);
		}
	}

	public static class GCombinator2<T1, T2> {
		private final RandomGenerator<T1> g1;
		private final RandomGenerator<T2> g2;

		private GCombinator2(RandomGenerator<T1> g1, RandomGenerator<T2> g2) {
			this.g1 = g1;
			this.g2 = g2;
		}

		@SuppressWarnings("unchecked")
		public <R> RandomGenerator<R> as(F2<T1, T2, R> combinator) {
			return random -> {
				List<Shrinkable<Object>> shrinkables = new ArrayList<>();
				shrinkables.add((Shrinkable<Object>) g1.next(random));
				shrinkables.add((Shrinkable<Object>) g2.next(random));
				Function<List<Object>, R> combineFunction = params -> combinator.apply((T1) params.get(0), (T2) params.get(1));
				return new CombinedShrinkable<R>(shrinkables, combineFunction);
			};
		}
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

		@SuppressWarnings("unchecked")
		public <R> Arbitrary<R> as(F3<T1, T2, T3, R> combinator) {
			return (tries) -> {
				RandomGenerator<T1> g1 = a1.generator(tries);
				RandomGenerator<T2> g2 = a2.generator(tries);
				RandomGenerator<T3> g3 = a3.generator(tries);
				return random -> {
					List<Shrinkable<Object>> shrinkables = new ArrayList<>();
					shrinkables.add((Shrinkable<Object>) g1.next(random));
					shrinkables.add((Shrinkable<Object>) g2.next(random));
					shrinkables.add((Shrinkable<Object>) g3.next(random));
					Function<List<Object>, R> combineFunction = params -> combinator.apply((T1) params.get(0), (T2) params.get(1), (T3) params.get(2));

					return new CombinedShrinkable<R>(shrinkables, combineFunction);
				};
			};
		}
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

		@SuppressWarnings("unchecked")
		public <R> Arbitrary<R> as(F4<T1, T2, T3, T4, R> combinator) {
			return (tries) -> {
				RandomGenerator<T1> g1 = a1.generator(tries);
				RandomGenerator<T2> g2 = a2.generator(tries);
				RandomGenerator<T3> g3 = a3.generator(tries);
				RandomGenerator<T4> g4 = a4.generator(tries);
				return random -> {
					List<Shrinkable<Object>> shrinkables = new ArrayList<>();
					shrinkables.add((Shrinkable<Object>) g1.next(random));
					shrinkables.add((Shrinkable<Object>) g2.next(random));
					shrinkables.add((Shrinkable<Object>) g3.next(random));
					shrinkables.add((Shrinkable<Object>) g4.next(random));
					Function<List<Object>, R> combineFunction = params -> combinator.apply((T1) params.get(0), (T2) params.get(1), (T3) params.get(2), (T4) params.get(3));

					return new CombinedShrinkable<R>(shrinkables, combineFunction);
				};
			};
		}
	}

	@FunctionalInterface
	public interface F2<T1, T2, R> {
		R apply(T1 t1, T2 t2);
	}

	@FunctionalInterface
	public interface F3<T1, T2, T3, R> {
		R apply(T1 t1, T2 t2, T3 t3);
	}

	@FunctionalInterface
	public interface F4<T1, T2, T3, T4, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4);
	}

}
