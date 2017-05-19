package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public class NCombinators {

	public static <T1, T2> Combinator2<T1, T2> combine(NArbitrary<T1> a1, NArbitrary<T2> a2) {
		return new Combinator2<T1, T2>(a1, a2);
	}

	public static <T1, T2, T3> Combinator3<T1, T2, T3> combine(NArbitrary<T1> a1, NArbitrary<T2> a2, NArbitrary<T3> a3) {
		return new Combinator3<T1, T2, T3>(a1, a2, a3);
	}

	public static <T1, T2, T3, T4> Combinator4<T1, T2, T3, T4> combine(NArbitrary<T1> a1, NArbitrary<T2> a2, NArbitrary<T3> a3, NArbitrary<T4> a4) {
		return new Combinator4<T1, T2, T3, T4>(a1, a2, a3, a4);
	}

	public static class Combinator2<T1, T2> {
		private final NArbitrary<T1> a1;
		private final NArbitrary<T2> a2;

		private Combinator2(NArbitrary<T1> a1, NArbitrary<T2> a2) {
			this.a1 = a1;
			this.a2 = a2;
		}

		@SuppressWarnings("unchecked")
		public <R> NArbitrary<R> as(F2<T1, T2, R> combinator) {
			return (tries) -> {
				NShrinkableGenerator<T1> g1 = a1.generator(tries);
				NShrinkableGenerator<T2> g2 = a2.generator(tries);
				return random -> {
					List<NShrinkable<Object>> shrinkables = new ArrayList<>();
					shrinkables.add((NShrinkable<Object>) g1.next(random));
					shrinkables.add((NShrinkable<Object>) g2.next(random));
					Function<List<Object>, R> combineFunction = params -> combinator.apply((T1) params.get(0), (T2) params.get(1));

					return new NCombinedShrinkable<R>(shrinkables, combineFunction);
				};
			};
		}
	}

	public static class Combinator3<T1, T2, T3> {
		private final NArbitrary<T1> a1;
		private final NArbitrary<T2> a2;
		private final NArbitrary<T3> a3;

		private Combinator3(NArbitrary<T1> a1, NArbitrary<T2> a2, NArbitrary<T3> a3) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
		}

		@SuppressWarnings("unchecked")
		public <R> NArbitrary<R> as(F3<T1, T2, T3, R> combinator) {
			return (tries) -> {
				NShrinkableGenerator<T1> g1 = a1.generator(tries);
				NShrinkableGenerator<T2> g2 = a2.generator(tries);
				NShrinkableGenerator<T3> g3 = a3.generator(tries);
				return random -> {
					List<NShrinkable<Object>> shrinkables = new ArrayList<>();
					shrinkables.add((NShrinkable<Object>) g1.next(random));
					shrinkables.add((NShrinkable<Object>) g2.next(random));
					shrinkables.add((NShrinkable<Object>) g3.next(random));
					Function<List<Object>, R> combineFunction = params -> combinator.apply((T1) params.get(0), (T2) params.get(1), (T3) params.get(2));

					return new NCombinedShrinkable<R>(shrinkables, combineFunction);
				};
			};
		}
	}

	public static class Combinator4<T1, T2, T3, T4> {
		private final NArbitrary<T1> a1;
		private final NArbitrary<T2> a2;
		private final NArbitrary<T3> a3;
		private final NArbitrary<T4> a4;

		private Combinator4(NArbitrary<T1> a1, NArbitrary<T2> a2, NArbitrary<T3> a3, NArbitrary<T4> a4) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
			this.a4 = a4;
		}

		@SuppressWarnings("unchecked")
		public <R> NArbitrary<R> as(F4<T1, T2, T3, T4, R> combinator) {
			return (tries) -> {
				NShrinkableGenerator<T1> g1 = a1.generator(tries);
				NShrinkableGenerator<T2> g2 = a2.generator(tries);
				NShrinkableGenerator<T3> g3 = a3.generator(tries);
				NShrinkableGenerator<T4> g4 = a4.generator(tries);
				return random -> {
					List<NShrinkable<Object>> shrinkables = new ArrayList<>();
					shrinkables.add((NShrinkable<Object>) g1.next(random));
					shrinkables.add((NShrinkable<Object>) g2.next(random));
					shrinkables.add((NShrinkable<Object>) g3.next(random));
					shrinkables.add((NShrinkable<Object>) g4.next(random));
					Function<List<Object>, R> combineFunction = params -> combinator.apply((T1) params.get(0), (T2) params.get(1), (T3) params.get(2), (T4) params.get(3));

					return new NCombinedShrinkable<R>(shrinkables, combineFunction);
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
