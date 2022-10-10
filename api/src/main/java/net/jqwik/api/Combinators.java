package net.jqwik.api;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;
import org.jetbrains.annotations.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.0")
public class Combinators {

	@API(status = INTERNAL)
	public static abstract class CombinatorsFacade {
		private static final CombinatorsFacade implementation;

		static {
			implementation = FacadeLoader.load(CombinatorsFacade.class);
		}

		public abstract <T1, T2> Combinator2<T1, T2> combine2(Arbitrary<T1> a1, Arbitrary<T2> a2);

		public abstract <T1, T2, T3> Combinator3<T1, T2, T3> combine3(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3);

		public abstract <T1, T2, T3, T4> Combinator4<T1, T2, T3, T4> combine4(
			Arbitrary<T1> a1,
			Arbitrary<T2> a2,
			Arbitrary<T3> a3,
			Arbitrary<T4> a4
		);

		public abstract <T1, T2, T3, T4, T5> Combinator5<T1, T2, T3, T4, T5> combine5(
			Arbitrary<T1> a1,
			Arbitrary<T2> a2,
			Arbitrary<T3> a3,
			Arbitrary<T4> a4,
			Arbitrary<T5> a5
		);

		public abstract <T1, T2, T3, T4, T5, T6> Combinator6<T1, T2, T3, T4, T5, T6> combine6(
			Arbitrary<T1> a1,
			Arbitrary<T2> a2,
			Arbitrary<T3> a3,
			Arbitrary<T4> a4,
			Arbitrary<T5> a5,
			Arbitrary<T6> a6
		);

		public abstract <T1, T2, T3, T4, T5, T6, T7> Combinator7<T1, T2, T3, T4, T5, T6, T7> combine7(
			Arbitrary<T1> a1,
			Arbitrary<T2> a2,
			Arbitrary<T3> a3,
			Arbitrary<T4> a4,
			Arbitrary<T5> a5,
			Arbitrary<T6> a6,
			Arbitrary<T7> a7
		);

		public abstract <T1, T2, T3, T4, T5, T6, T7, T8> Combinator8<T1, T2, T3, T4, T5, T6, T7, T8> combine8(
			Arbitrary<T1> a1,
			Arbitrary<T2> a2,
			Arbitrary<T3> a3,
			Arbitrary<T4> a4,
			Arbitrary<T5> a5,
			Arbitrary<T6> a6,
			Arbitrary<T7> a7,
			Arbitrary<T8> a8
		);

		public abstract <T> ListCombinator<T> combineList(List<Arbitrary<T>> listOfArbitraries);

		public abstract <R> Arbitrary<R> combine(Function<List<Object>, R> combinator, Arbitrary<?>... arbitraries);
	}

	private Combinators() {
	}

	/**
	 * Combine 2 arbitraries into one.
	 *
	 * @return Combinator2 instance which can be evaluated using {@linkplain Combinator2#as}
	 */
	public static <T1, T2> Combinator2<T1, T2> combine(Arbitrary<T1> a1, Arbitrary<T2> a2) {
		return CombinatorsFacade.implementation.combine2(a1, a2);
	}

	/**
	 * Combine 3 arbitraries into one.
	 *
	 * @return Combinator3 instance which can be evaluated using {@linkplain Combinator3#as}
	 */
	public static <T1, T2, T3> Combinator3<T1, T2, T3> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3) {
		return CombinatorsFacade.implementation.combine3(a1, a2, a3);
	}

	/**
	 * Combine 4 arbitraries into one.
	 *
	 * @return Combinator4 instance which can be evaluated using {@linkplain Combinator4#as}
	 */
	public static <T1, T2, T3, T4> Combinator4<T1, T2, T3, T4> combine(
		Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3,
		Arbitrary<T4> a4
	) {
		return CombinatorsFacade.implementation.combine4(a1, a2, a3, a4);
	}

	/**
	 * Combine 5 arbitraries into one.
	 *
	 * @return Combinator5 instance which can be evaluated using {@linkplain Combinator5#as}
	 */
	public static <T1, T2, T3, T4, T5> Combinator5<T1, T2, T3, T4, T5> combine(
		Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3,
		Arbitrary<T4> a4, Arbitrary<T5> a5
	) {
		return CombinatorsFacade.implementation.combine5(a1, a2, a3, a4, a5);
	}

	/**
	 * Combine 6 arbitraries into one.
	 *
	 * @return Combinator6 instance which can be evaluated using {@linkplain Combinator6#as}
	 */
	public static <T1, T2, T3, T4, T5, T6> Combinator6<T1, T2, T3, T4, T5, T6> combine(
		Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3,
		Arbitrary<T4> a4, Arbitrary<T5> a5, Arbitrary<T6> a6
	) {
		return CombinatorsFacade.implementation.combine6(a1, a2, a3, a4, a5, a6);
	}

	/**
	 * Combine 7 arbitraries into one.
	 *
	 * @return Combinator7 instance which can be evaluated using {@linkplain Combinator7#as}
	 */
	public static <T1, T2, T3, T4, T5, T6, T7> Combinator7<T1, T2, T3, T4, T5, T6, T7> combine(
		Arbitrary<T1> a1, Arbitrary<T2> a2,
		Arbitrary<T3> a3, Arbitrary<T4> a4, Arbitrary<T5> a5, Arbitrary<T6> a6, Arbitrary<T7> a7
	) {
		return CombinatorsFacade.implementation.combine7(a1, a2, a3, a4, a5, a6, a7);
	}

	/**
	 * Combine 8 arbitraries into one.
	 *
	 * @return Combinator8 instance which can be evaluated using {@linkplain Combinator8#as}
	 */
	public static <T1, T2, T3, T4, T5, T6, T7, T8> Combinator8<T1, T2, T3, T4, T5, T6, T7, T8> combine(
		Arbitrary<T1> a1, Arbitrary<T2> a2,
		Arbitrary<T3> a3, Arbitrary<T4> a4, Arbitrary<T5> a5, Arbitrary<T6> a6, Arbitrary<T7> a7, Arbitrary<T8> a8
	) {
		return CombinatorsFacade.implementation.combine8(a1, a2, a3, a4, a5, a6, a7, a8);
	}

	/**
	 * Combine a list of arbitraries into one.
	 *
	 * @return ListCombinator instance which can be evaluated using {@linkplain ListCombinator#as}
	 */
	public static <T> ListCombinator<T> combine(List<Arbitrary<T>> listOfArbitraries) {
		return CombinatorsFacade.implementation.combineList(listOfArbitraries);
	}

	@SuppressWarnings("unchecked")
	private static <T1, T2, T3, T4, T5, R> Function<List<Object>, R> combineFunction(F5<T1, T2, T3, T4, T5, R> combinator5) {
		return params -> combinator5
							 .apply(
								 (T1) params.get(0), (T2) params.get(1),
								 (T3) params.get(2), (T4) params.get(3),
								 (T5) params.get(4)
							 );
	}

	@SuppressWarnings("unchecked")
	private static <T1, T2, T3, T4, T5, T6, R> Function<List<Object>, R> combineFunction(F6<T1, T2, T3, T4, T5, T6, R> combinator6) {
		return params -> combinator6
							 .apply(
								 (T1) params.get(0), (T2) params.get(1),
								 (T3) params.get(2), (T4) params.get(3),
								 (T5) params.get(4), (T6) params.get(5)
							 );
	}

	@SuppressWarnings("unchecked")
	private static <T1, T2, T3, T4, T5, T6, T7, R> Function<List<Object>, R> combineFunction(F7<T1, T2, T3, T4, T5, T6, T7, R> combinator7) {
		return params -> combinator7
							 .apply(
								 (T1) params.get(0), (T2) params.get(1),
								 (T3) params.get(2), (T4) params.get(3),
								 (T5) params.get(4), (T6) params.get(5),
								 (T7) params.get(6)
							 );
	}

	@SuppressWarnings("unchecked")
	private static <T1, T2, T3, T4, T5, T6, T7, T8, R> Function<List<Object>, R> combineFunction(F8<T1, T2, T3, T4, T5, T6, T7, T8, R> combinator8) {
		return params -> combinator8
							 .apply(
								 (T1) params.get(0), (T2) params.get(1),
								 (T3) params.get(2), (T4) params.get(3),
								 (T5) params.get(4), (T6) params.get(5),
								 (T7) params.get(6), (T8) params.get(7)
							 );
	}

	/**
	 * Combinator for two values.
	 */
	public interface Combinator2<T1, T2> {

		/**
		 * Combine two values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		<R> Arbitrary<R> as(F2<T1, T2, @NotNull R> combinator);

		/**
		 * Filter two values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = EXPERIMENTAL, since = "1.7.1")
		Combinator2<T1, T2> filter(F2<T1, T2, Boolean> filter);

		/**
		 * Combine two values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R> return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R> Arbitrary<R> flatAs(F2<T1, T2, Arbitrary<@NotNull R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}
	}

	/**
	 * Combinator for three values.
	 */
	public interface Combinator3<T1, T2, T3> {

		/**
		 * Combine three values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		<R> Arbitrary<R> as(F3<T1, T2, T3, @NotNull R> combinator);

		/**
		 * Filter three values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = EXPERIMENTAL, since = "1.7.1")
		Combinator3<T1, T2, T3> filter(F3<T1, T2, T3, Boolean> filter);

		/**
		 * Combine three values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R> return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R> Arbitrary<R> flatAs(F3<T1, T2, T3, Arbitrary<@NotNull R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}

	}

	/**
	 * Combinator for four values.
	 */
	public interface Combinator4<T1, T2, T3, T4> {

		/**
		 * Combine four values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		<R> Arbitrary<R> as(F4<T1, T2, T3, T4, @NotNull R> combinator);

		/**
		 * Filter four values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = EXPERIMENTAL, since = "1.7.1")
		Combinator4<T1, T2, T3, T4> filter(F4<T1, T2, T3, T4, Boolean> filter);

		/**
		 * Combine four values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R> return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R> Arbitrary<R> flatAs(F4<T1, T2, T3, T4, Arbitrary<@NotNull R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}

	}

	/**
	 * Combinator for five values.
	 */
	public interface Combinator5<T1, T2, T3, T4, T5> {

		/**
		 * Combine five values.
		 *
		 * @param combinator function
		 * @param <R> return type
		 * @return arbitrary instance
		 */
		<R> Arbitrary<R> as(F5<T1, T2, T3, T4, T5, @NotNull R> combinator);

		/**
		 * Filter five values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = EXPERIMENTAL, since = "1.7.1")
		Combinator5<T1, T2, T3, T4, T5> filter(F5<T1, T2, T3, T4, T5, Boolean> filter);

		/**
		 * Combine five values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R> return type of arbitrary
		 * @return arbitrary instance
		 */
		default  <R> Arbitrary<R> flatAs(F5<T1, T2, T3, T4, T5, Arbitrary<@NotNull R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}
	}

	/**
	 * Combinator for six values.
	 */
	public interface Combinator6<T1, T2, T3, T4, T5, T6> {

		/**
		 * Combine six values.
		 *
		 * @param combinator function
		 * @param <R> return type
		 * @return arbitrary instance
		 */
		<R> Arbitrary<R> as(F6<T1, T2, T3, T4, T5, T6, @NotNull R> combinator);

		/**
		 * Filter six values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = EXPERIMENTAL, since = "1.7.1")
		Combinator6<T1, T2, T3, T4, T5, T6> filter(F6<T1, T2, T3, T4, T5, T6, Boolean> filter);

		/**
		 * Combine six values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R> return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R> Arbitrary<R> flatAs(F6<T1, T2, T3, T4, T5, T6, Arbitrary<@NotNull R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}

	}

	/**
	 * Combinator for seven values.
	 */
	public interface Combinator7<T1, T2, T3, T4, T5, T6, T7> {

		/**
		 * Combine seven values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		<R> Arbitrary<R> as(F7<T1, T2, T3, T4, T5, T6, T7, @NotNull R> combinator);

		/**
		 * Filter seven values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = EXPERIMENTAL, since = "1.7.1")
		Combinator7<T1, T2, T3, T4, T5, T6, T7> filter(F7<T1, T2, T3, T4, T5, T6, T7, Boolean> filter);

		/**
		 * Combine seven values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R> return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R> Arbitrary<R> flatAs(F7<T1, T2, T3, T4, T5, T6, T7, Arbitrary<@NotNull R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}

	}

	/**
	 * Combinator for eight values.
	 */
	public static class Combinator8<T1, T2, T3, T4, T5, T6, T7, T8> {
		private final Arbitrary<T1> a1;
		private final Arbitrary<T2> a2;
		private final Arbitrary<T3> a3;
		private final Arbitrary<T4> a4;
		private final Arbitrary<T5> a5;
		private final Arbitrary<T6> a6;
		private final Arbitrary<T7> a7;
		private final Arbitrary<T8> a8;

		public Combinator8(
			Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4, Arbitrary<T5> a5, Arbitrary<T6> a6,
			Arbitrary<T7> a7, Arbitrary<T8> a8
		) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
			this.a4 = a4;
			this.a5 = a5;
			this.a6 = a6;
			this.a7 = a7;
			this.a8 = a8;
		}

		/**
		 * Combine eight values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		public <R> Arbitrary<R> as(F8<T1, T2, T3, T4, T5, T6, T7, T8, @NotNull R> combinator) {
			return CombinatorsFacade.implementation.combine(combineFunction(combinator), a1, a2, a3, a4, a5, a6, a7, a8);
		}

		public <R> Arbitrary<R> flatAs(F8<T1, T2, T3, T4, T5, T6, T7, T8, Arbitrary<@NotNull R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}
	}

	/**
	 * Combinator for any number of values.
	 */
	public interface ListCombinator<T> {

		/**
		 * Combine any number of values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		<R> Arbitrary<R> as(Function<List<T>, @NotNull R> combinator);

		/**
		 * Filter list of values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = EXPERIMENTAL, since = "1.7.1")
		ListCombinator<T> filter(Predicate<List<T>> filter);

		/**
		 * Combine list of values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R> return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R> Arbitrary<R> flatAs(Function<List<T>, Arbitrary<@NotNull R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F2<T1, T2, R> {
		R apply(T1 t1, T2 t2);
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F3<T1, T2, T3, R> {
		R apply(T1 t1, T2 t2, T3 t3);
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F4<T1, T2, T3, T4, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4);
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F5<T1, T2, T3, T4, T5, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F6<T1, T2, T3, T4, T5, T6, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F7<T1, T2, T3, T4, T5, T6, T7, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F8<T1, T2, T3, T4, T5, T6, T7, T8, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);
	}

}
