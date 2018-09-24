package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.properties.shrinking.*;

public class Combinators {

	private Combinators() {
	}

	public static <T1, T2> Combinator2<T1, T2> combine(Arbitrary<T1> a1, Arbitrary<T2> a2) {
		return new Combinator2<>(a1, a2);
	}

	public static <T1, T2, T3> Combinator3<T1, T2, T3> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3) {
		return new Combinator3<>(a1, a2, a3);
	}

	public static <T1, T2, T3, T4> Combinator4<T1, T2, T3, T4> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3,
			Arbitrary<T4> a4) {
		return new Combinator4<>(a1, a2, a3, a4);
	}

	public static <T1, T2, T3, T4, T5> Combinator5<T1, T2, T3, T4, T5> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3,
			Arbitrary<T4> a4, Arbitrary<T5> a5) {
		return new Combinator5<>(a1, a2, a3, a4, a5);
	}

	public static <T1, T2, T3, T4, T5, T6> Combinator6<T1, T2, T3, T4, T5, T6> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3,
			Arbitrary<T4> a4, Arbitrary<T5> a5, Arbitrary<T6> a6) {
		return new Combinator6<>(a1, a2, a3, a4, a5, a6);
	}

	public static <T1, T2, T3, T4, T5, T6, T7> Combinator7<T1, T2, T3, T4, T5, T6, T7> combine(Arbitrary<T1> a1, Arbitrary<T2> a2,
			Arbitrary<T3> a3, Arbitrary<T4> a4, Arbitrary<T5> a5, Arbitrary<T6> a6, Arbitrary<T7> a7) {
		return new Combinator7<>(a1, a2, a3, a4, a5, a6, a7);
	}

	public static <T1, T2, T3, T4, T5, T6, T7, T8> Combinator8<T1, T2, T3, T4, T5, T6, T7, T8> combine(Arbitrary<T1> a1, Arbitrary<T2> a2,
			Arbitrary<T3> a3, Arbitrary<T4> a4, Arbitrary<T5> a5, Arbitrary<T6> a6, Arbitrary<T7> a7, Arbitrary<T8> a8) {
		return new Combinator8<>(a1, a2, a3, a4, a5, a6, a7, a8);
	}

	public static <T> ListCombinator<T> combine(List<Arbitrary<T>> listOfArbitraries) {
		return new ListCombinator<>(listOfArbitraries);
	}


	public static class Combinator2<T1, T2> {
		private final Arbitrary<T1> a1;
		private final Arbitrary<T2> a2;

		private Combinator2(Arbitrary<T1> a1, Arbitrary<T2> a2) {
			this.a1 = a1;
			this.a2 = a2;
		}

		// This is a shorter implementation of as, which however would have worse shrinking
		// behaviour because it builds on flatMap:
		//		public <R> Arbitrary<R> as(F2<T1, T2, R> combinator) {
		//			return a1.flatMap(v1 -> a2.map(v2 -> combinator.apply(v1, v2)));
		//		}
		public <R> Arbitrary<R> as(F2<T1, T2, R> combinator) {
			return (genSize) -> {
				RandomGenerator<T1> g1 = a1.generator(genSize);
				RandomGenerator<T2> g2 = a2.generator(genSize);
				return new RandomGenerator<R>() {
					@SuppressWarnings("unchecked")
					@Override
					public Shrinkable<R> next(Random random) {
						List<Shrinkable<Object>> shrinkables = new ArrayList<>();
						shrinkables.add((Shrinkable<Object>) g1.next(random));
						shrinkables.add((Shrinkable<Object>) g2.next(random));
						Function<List<Object>, R> combineFunction = params -> combinator.apply((T1) params.get(0), (T2) params.get(1));

						return new CombinedShrinkable<>(shrinkables, combineFunction);
					}

					@Override
					public void reset() {
						g1.reset();
						g2.reset();
					}
				};
			};
		}

		public <R> Arbitrary<R> flatAs(F2<T1, T2, Arbitrary<R>> flatCombinator) {
			return a1.flatMap(v1 -> a2.flatMap(v2 -> flatCombinator.apply(v1, v2)));
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
			return (genSize) -> {
				RandomGenerator<T1> g1 = a1.generator(genSize);
				RandomGenerator<T2> g2 = a2.generator(genSize);
				RandomGenerator<T3> g3 = a3.generator(genSize);
				return new RandomGenerator<R>() {
					@Override
					public Shrinkable<R> next(Random random) {
						List<Shrinkable<Object>> shrinkables = new ArrayList<>();
						shrinkables.add((Shrinkable<Object>) g1.next(random));
						shrinkables.add((Shrinkable<Object>) g2.next(random));
						shrinkables.add((Shrinkable<Object>) g3.next(random));
						Function<List<Object>, R> combineFunction = params -> combinator.apply((T1) params.get(0), (T2) params.get(1),
																							   (T3) params.get(2)
						);

						return new CombinedShrinkable<>(shrinkables, combineFunction);
					}

					@Override
					public void reset() {
						g1.reset();
						g2.reset();
						g3.reset();
					}
				};
			};
		}

		public <R> Arbitrary<R> flatAs(F3<T1, T2, T3, Arbitrary<R>> flatCombinator) {
			return a1.flatMap(
				v1 -> a2.flatMap(
					v2 -> a3.flatMap(
						v3 -> flatCombinator.apply(v1, v2, v3))));
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
			return (genSize) -> {
				RandomGenerator<T1> g1 = a1.generator(genSize);
				RandomGenerator<T2> g2 = a2.generator(genSize);
				RandomGenerator<T3> g3 = a3.generator(genSize);
				RandomGenerator<T4> g4 = a4.generator(genSize);
				return new RandomGenerator<R>() {
					@Override
					public Shrinkable<R> next(Random random) {
						List<Shrinkable<Object>> shrinkables = new ArrayList<>();
						shrinkables.add((Shrinkable<Object>) g1.next(random));
						shrinkables.add((Shrinkable<Object>) g2.next(random));
						shrinkables.add((Shrinkable<Object>) g3.next(random));
						shrinkables.add((Shrinkable<Object>) g4.next(random));
						Function<List<Object>, R> combineFunction = params -> combinator.apply((T1) params.get(0), (T2) params.get(1),
																							   (T3) params.get(2), (T4) params.get(3)
						);

						return new CombinedShrinkable<>(shrinkables, combineFunction);
					}

					@Override
					public void reset() {
						g1.reset();
						g2.reset();
						g3.reset();
						g4.reset();
					}
				};
			};
		}

		public <R> Arbitrary<R> flatAs(F4<T1, T2, T3, T4, Arbitrary<R>> flatCombinator) {
			return a1.flatMap(
				v1 -> a2.flatMap(
					v2 -> a3.flatMap(
						v3 -> a4.flatMap(
							v4 -> flatCombinator.apply(v1, v2, v3, v4)))));
		}


	}

	public static class Combinator5<T1, T2, T3, T4, T5> {
		private final Arbitrary<T1> a1;
		private final Arbitrary<T2> a2;
		private final Arbitrary<T3> a3;
		private final Arbitrary<T4> a4;
		private final Arbitrary<T5> a5;

		private Combinator5(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4, Arbitrary<T5> a5) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
			this.a4 = a4;
			this.a5 = a5;
		}

		@SuppressWarnings("unchecked")
		public <R> Arbitrary<R> as(F5<T1, T2, T3, T4, T5, R> combinator) {
			return (genSize) -> {
				RandomGenerator<T1> g1 = a1.generator(genSize);
				RandomGenerator<T2> g2 = a2.generator(genSize);
				RandomGenerator<T3> g3 = a3.generator(genSize);
				RandomGenerator<T4> g4 = a4.generator(genSize);
				RandomGenerator<T5> g5 = a5.generator(genSize);
				return new RandomGenerator<R>() {
					@Override
					public Shrinkable<R> next(Random random) {
						List<Shrinkable<Object>> shrinkables = new ArrayList<>();
						shrinkables.add((Shrinkable<Object>) g1.next(random));
						shrinkables.add((Shrinkable<Object>) g2.next(random));
						shrinkables.add((Shrinkable<Object>) g3.next(random));
						shrinkables.add((Shrinkable<Object>) g4.next(random));
						shrinkables.add((Shrinkable<Object>) g5.next(random));
						Function<List<Object>, R> combineFunction = params -> combinator.apply((T1) params.get(0), (T2) params.get(1),
																							   (T3) params.get(2), (T4) params
																															.get(3), (T5) params
																																			  .get(4)
						);

						return new CombinedShrinkable<>(shrinkables, combineFunction);
					}

					@Override
					public void reset() {
						g1.reset();
						g2.reset();
						g3.reset();
						g4.reset();
						g5.reset();
					}
				};
			};
		}
	}

	public static class Combinator6<T1, T2, T3, T4, T5, T6> {
		private final Arbitrary<T1> a1;
		private final Arbitrary<T2> a2;
		private final Arbitrary<T3> a3;
		private final Arbitrary<T4> a4;
		private final Arbitrary<T5> a5;
		private final Arbitrary<T6> a6;

		private Combinator6(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4, Arbitrary<T5> a5, Arbitrary<T6> a6) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
			this.a4 = a4;
			this.a5 = a5;
			this.a6 = a6;
		}

		@SuppressWarnings("unchecked")
		public <R> Arbitrary<R> as(F6<T1, T2, T3, T4, T5, T6, R> combinator) {
			return (genSize) -> {
				RandomGenerator<T1> g1 = a1.generator(genSize);
				RandomGenerator<T2> g2 = a2.generator(genSize);
				RandomGenerator<T3> g3 = a3.generator(genSize);
				RandomGenerator<T4> g4 = a4.generator(genSize);
				RandomGenerator<T5> g5 = a5.generator(genSize);
				RandomGenerator<T6> g6 = a6.generator(genSize);
				return new RandomGenerator<R>() {
					@Override
					public Shrinkable<R> next(Random random) {
						List<Shrinkable<Object>> shrinkables = new ArrayList<>();
						shrinkables.add((Shrinkable<Object>) g1.next(random));
						shrinkables.add((Shrinkable<Object>) g2.next(random));
						shrinkables.add((Shrinkable<Object>) g3.next(random));
						shrinkables.add((Shrinkable<Object>) g4.next(random));
						shrinkables.add((Shrinkable<Object>) g5.next(random));
						shrinkables.add((Shrinkable<Object>) g6.next(random));
						Function<List<Object>, R> combineFunction = params -> combinator.apply((T1) params.get(0), (T2) params.get(1),
																							   (T3) params.get(2), (T4) params
																															.get(3), (T5) params
																																			  .get(4), (T6) params
																																								.get(5)
						);

						return new CombinedShrinkable<>(shrinkables, combineFunction);
					}

					@Override
					public void reset() {
						g1.reset();
						g2.reset();
						g3.reset();
						g4.reset();
						g5.reset();
						g6.reset();
					}
				};
			};
		}
	}

	public static class Combinator7<T1, T2, T3, T4, T5, T6, T7> {
		private final Arbitrary<T1> a1;
		private final Arbitrary<T2> a2;
		private final Arbitrary<T3> a3;
		private final Arbitrary<T4> a4;
		private final Arbitrary<T5> a5;
		private final Arbitrary<T6> a6;
		private final Arbitrary<T7> a7;

		private Combinator7(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4, Arbitrary<T5> a5, Arbitrary<T6> a6,
				Arbitrary<T7> a7) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
			this.a4 = a4;
			this.a5 = a5;
			this.a6 = a6;
			this.a7 = a7;
		}

		@SuppressWarnings("unchecked")
		public <R> Arbitrary<R> as(F7<T1, T2, T3, T4, T5, T6, T7, R> combinator) {
			return (genSize) -> {
				RandomGenerator<T1> g1 = a1.generator(genSize);
				RandomGenerator<T2> g2 = a2.generator(genSize);
				RandomGenerator<T3> g3 = a3.generator(genSize);
				RandomGenerator<T4> g4 = a4.generator(genSize);
				RandomGenerator<T5> g5 = a5.generator(genSize);
				RandomGenerator<T6> g6 = a6.generator(genSize);
				RandomGenerator<T7> g7 = a7.generator(genSize);
				return new RandomGenerator<R>() {
					@Override
					public Shrinkable<R> next(Random random) {
						List<Shrinkable<Object>> shrinkables = new ArrayList<>();
						shrinkables.add((Shrinkable<Object>) g1.next(random));
						shrinkables.add((Shrinkable<Object>) g2.next(random));
						shrinkables.add((Shrinkable<Object>) g3.next(random));
						shrinkables.add((Shrinkable<Object>) g4.next(random));
						shrinkables.add((Shrinkable<Object>) g5.next(random));
						shrinkables.add((Shrinkable<Object>) g6.next(random));
						shrinkables.add((Shrinkable<Object>) g7.next(random));
						Function<List<Object>, R> combineFunction = params -> combinator.apply(
							(T1) params.get(0), (T2) params.get(1),
							(T3) params.get(2), (T4) params.get(3),
							(T5) params.get(4), (T6) params.get(5),
							(T7) params.get(6)
						);
						return new CombinedShrinkable<>(shrinkables, combineFunction);
					}

					@Override
					public void reset() {
						g1.reset();
						g2.reset();
						g3.reset();
						g4.reset();
						g5.reset();
						g6.reset();
						g7.reset();
					}
				};
			};
		}
	}

	public static class Combinator8<T1, T2, T3, T4, T5, T6, T7, T8> {
		private final Arbitrary<T1> a1;
		private final Arbitrary<T2> a2;
		private final Arbitrary<T3> a3;
		private final Arbitrary<T4> a4;
		private final Arbitrary<T5> a5;
		private final Arbitrary<T6> a6;
		private final Arbitrary<T7> a7;
		private final Arbitrary<T8> a8;

		private Combinator8(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4, Arbitrary<T5> a5, Arbitrary<T6> a6,
				Arbitrary<T7> a7, Arbitrary<T8> a8) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
			this.a4 = a4;
			this.a5 = a5;
			this.a6 = a6;
			this.a7 = a7;
			this.a8 = a8;
		}

		@SuppressWarnings("unchecked")
		public <R> Arbitrary<R> as(F8<T1, T2, T3, T4, T5, T6, T7, T8, R> combinator) {
			return (genSize) -> {
				RandomGenerator<T1> g1 = a1.generator(genSize);
				RandomGenerator<T2> g2 = a2.generator(genSize);
				RandomGenerator<T3> g3 = a3.generator(genSize);
				RandomGenerator<T4> g4 = a4.generator(genSize);
				RandomGenerator<T5> g5 = a5.generator(genSize);
				RandomGenerator<T6> g6 = a6.generator(genSize);
				RandomGenerator<T7> g7 = a7.generator(genSize);
				RandomGenerator<T8> g8 = a8.generator(genSize);
				return new RandomGenerator<R>() {
					@Override
					public Shrinkable<R> next(Random random) {
						List<Shrinkable<Object>> shrinkables = new ArrayList<>();
						shrinkables.add((Shrinkable<Object>) g1.next(random));
						shrinkables.add((Shrinkable<Object>) g2.next(random));
						shrinkables.add((Shrinkable<Object>) g3.next(random));
						shrinkables.add((Shrinkable<Object>) g4.next(random));
						shrinkables.add((Shrinkable<Object>) g5.next(random));
						shrinkables.add((Shrinkable<Object>) g6.next(random));
						shrinkables.add((Shrinkable<Object>) g7.next(random));
						shrinkables.add((Shrinkable<Object>) g8.next(random));
						Function<List<Object>, R> combineFunction = params -> combinator.apply(
							(T1) params.get(0), (T2) params.get(1),
							(T3) params.get(2), (T4) params.get(3),
							(T5) params.get(4), (T6) params.get(5),
							(T7) params.get(6), (T8) params.get(7)
						);

						return new CombinedShrinkable<>(shrinkables, combineFunction);
					}

					@Override
					public void reset() {
						g1.reset();
						g2.reset();
						g3.reset();
						g4.reset();
						g5.reset();
						g6.reset();
						g7.reset();
						g8.reset();
					}
				};
			};
		}
	}

	public static class ListCombinator<T> {
		private final List<Arbitrary<T>> listOfArbitraries;

		private ListCombinator(List<Arbitrary<T>> listOfArbitraries) {
			this.listOfArbitraries = listOfArbitraries;
		}

		@SuppressWarnings("unchecked")
		public <R> Arbitrary<R> as(Function<List<T>, R> combinator) {
			return (genSize) -> {
				List<RandomGenerator<T>> listOfGenerators = listOfArbitraries
					.stream()
					.map(a -> a.generator(genSize))
					.collect(Collectors.toList());

				return new RandomGenerator<R>() {
					@Override
					public Shrinkable<R> next(Random random) {
						List<Shrinkable<Object>> shrinkables =
							listOfGenerators
								.stream()
								.map(g -> g.next(random))
								.map(s -> (Shrinkable<Object>) s)
								.collect(Collectors.toList());

						Function<List<Object>, R> combineFunction = params -> combinator.apply((List<T>) params);

						return new CombinedShrinkable<>(shrinkables, combineFunction);
					}

					@Override
					public void reset() {
						listOfGenerators.forEach(RandomGenerator::reset);
					}
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

	@FunctionalInterface
	public interface F5<T1, T2, T3, T4, T5, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
	}

	@FunctionalInterface
	public interface F6<T1, T2, T3, T4, T5, T6, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);
	}

	@FunctionalInterface
	public interface F7<T1, T2, T3, T4, T5, T6, T7, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);
	}

	@FunctionalInterface
	public interface F8<T1, T2, T3, T4, T5, T6, T7, T8, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);
	}

}
