package net.jqwik.api;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

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

		public abstract <T extends @Nullable Object> ListCombinator<T> combineList(List<? extends Arbitrary<T>> listOfArbitraries);
	}

	private Combinators() {
	}

	/**
	 * Combine 2 arbitraries into one.
	 *
	 * @return Combinator2 instance which can be evaluated using {@linkplain Combinator2#as}
	 */
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object> Combinator2<T1, T2> combine(Arbitrary<T1> a1, Arbitrary<T2> a2) {
		return CombinatorsFacade.implementation.combine2(a1, a2);
	}

	/**
	 * Combine 3 arbitraries into one.
	 *
	 * @return Combinator3 instance which can be evaluated using {@linkplain Combinator3#as}
	 */
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object> Combinator3<T1, T2, T3> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3) {
		return CombinatorsFacade.implementation.combine3(a1, a2, a3);
	}

	/**
	 * Combine 4 arbitraries into one.
	 *
	 * @return Combinator4 instance which can be evaluated using {@linkplain Combinator4#as}
	 */
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object> Combinator4<T1, T2, T3, T4> combine(
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
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object> Combinator5<T1, T2, T3, T4, T5> combine(
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
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object> Combinator6<T1, T2, T3, T4, T5, T6> combine(
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
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object, T7 extends @Nullable Object> Combinator7<T1, T2, T3, T4, T5, T6, T7> combine(
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
	public static <T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object, T7 extends @Nullable Object, T8 extends @Nullable Object> Combinator8<T1, T2, T3, T4, T5, T6, T7, T8> combine(
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
	public static <T extends @Nullable Object> ListCombinator<T> combine(List<? extends Arbitrary<T>> listOfArbitraries) {
		return CombinatorsFacade.implementation.combineList(listOfArbitraries);
	}

	/**
	 * Combinator for two values.
	 */
	public interface Combinator2<T1 extends @Nullable Object, T2 extends @Nullable Object> {

		/**
		 * Combine two values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		<R extends @Nullable Object> Arbitrary<R> as(F2<? super T1, ? super T2, ? extends R> combinator);

		/**
		 * Filter two values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = MAINTAINED, since = "1.8.0")
		Combinator2<T1, T2> filter(F2<? super T1, ? super T2, Boolean> filter);

		/**
		 * Combine two values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R>            return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R extends @Nullable Object> Arbitrary<R> flatAs(F2<? super T1, ? super T2, ? extends Arbitrary<R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}
	}

	/**
	 * Combinator for three values.
	 */
	public interface Combinator3<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object> {

		/**
		 * Combine three values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		<R extends @Nullable Object> Arbitrary<R> as(F3<? super T1, ? super T2, ? super T3, ? extends R> combinator);

		/**
		 * Filter three values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = MAINTAINED, since = "1.8.0")
		Combinator3<T1, T2, T3> filter(F3<? super T1, ? super T2, ? super T3, Boolean> filter);

		/**
		 * Combine three values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R>            return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R extends @Nullable Object> Arbitrary<R> flatAs(F3<? super T1, ? super T2, ? super T3, ? extends Arbitrary<R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}

	}

	/**
	 * Combinator for four values.
	 */
	public interface Combinator4<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object> {

		/**
		 * Combine four values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		<R extends @Nullable Object> Arbitrary<R> as(F4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> combinator);

		/**
		 * Filter four values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = MAINTAINED, since = "1.8.0")
		Combinator4<T1, T2, T3, T4> filter(F4<? super T1, ? super T2, ? super T3, ? super T4, Boolean> filter);

		/**
		 * Combine four values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R>            return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R extends @Nullable Object> Arbitrary<R> flatAs(F4<? super T1, ? super T2, ? super T3, ? super T4, ? extends Arbitrary<R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}

	}

	/**
	 * Combinator for five values.
	 */
	public interface Combinator5<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object> {

		/**
		 * Combine five values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		<R extends @Nullable Object> Arbitrary<R> as(F5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends R> combinator);

		/**
		 * Filter five values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = MAINTAINED, since = "1.8.0")
		Combinator5<T1, T2, T3, T4, T5> filter(F5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, Boolean> filter);

		/**
		 * Combine five values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R>            return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R extends @Nullable Object> Arbitrary<R> flatAs(F5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends Arbitrary<R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}
	}

	/**
	 * Combinator for six values.
	 */
	public interface Combinator6<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object> {

		/**
		 * Combine six values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		<R extends @Nullable Object> Arbitrary<R> as(F6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R> combinator);

		/**
		 * Filter six values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = MAINTAINED, since = "1.8.0")
		Combinator6<T1, T2, T3, T4, T5, T6> filter(F6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, Boolean> filter);

		/**
		 * Combine six values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R>            return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R extends @Nullable Object> Arbitrary<R> flatAs(F6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends Arbitrary<R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}

	}

	/**
	 * Combinator for seven values.
	 */
	public interface Combinator7<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object, T7 extends @Nullable Object> {

		/**
		 * Combine seven values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		<R extends @Nullable Object> Arbitrary<R> as(F7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends R> combinator);

		/**
		 * Filter seven values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = MAINTAINED, since = "1.8.0")
		Combinator7<T1, T2, T3, T4, T5, T6, T7> filter(F7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, Boolean> filter);

		/**
		 * Combine seven values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R>            return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R extends @Nullable Object> Arbitrary<R> flatAs(F7<T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends Arbitrary<R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}

	}

	/**
	 * Combinator for eight values.
	 */
	public interface Combinator8<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object, T7 extends @Nullable Object, T8 extends @Nullable Object> {

		/**
		 * Combine eight values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		<R extends @Nullable Object> Arbitrary<R> as(F8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends R> combinator);

		/**
		 * Filter eight values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = MAINTAINED, since = "1.8.0")
		Combinator8<T1, T2, T3, T4, T5, T6, T7, T8> filter(F8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, Boolean> filter);

		/**
		 * Combine eight values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R>            return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R extends @Nullable Object> Arbitrary<R> flatAs(F8<T1, T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends Arbitrary<R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}
	}

	/**
	 * Combinator for any number of values.
	 */
	public interface ListCombinator<T extends @Nullable Object> {

		/**
		 * Combine any number of values.
		 *
		 * @param combinator function
		 * @param <R>        return type
		 * @return arbitrary instance
		 */
		<R extends @Nullable Object> Arbitrary<R> as(Function<? super List<T>, ? extends R> combinator);

		/**
		 * Filter list of values to only let them pass if the predicate is true.
		 *
		 * @param filter function
		 * @return combinator instance
		 */
		@API(status = MAINTAINED, since = "1.8.0")
		ListCombinator<T> filter(Predicate<? super List<? extends T>> filter);

		/**
		 * Combine list of values to create a new arbitrary.
		 *
		 * @param flatCombinator function
		 * @param <R>            return type of arbitrary
		 * @return arbitrary instance
		 */
		default <R extends @Nullable Object> Arbitrary<R> flatAs(Function<? super List<? extends T>, ? extends Arbitrary<R>> flatCombinator) {
			return as(flatCombinator).flatMap(Function.identity());
		}
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F2<T1 extends @Nullable Object, T2 extends @Nullable Object, R extends @Nullable Object> {
		R apply(T1 t1, T2 t2);
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F3<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, R extends @Nullable Object> {
		R apply(T1 t1, T2 t2, T3 t3);
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F4<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, R extends @Nullable Object> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4);
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F5<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, R extends @Nullable Object> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F6<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object, R extends @Nullable Object> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F7<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object, T7 extends @Nullable Object, R extends @Nullable Object> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);
	}

	@FunctionalInterface
	@API(status = INTERNAL)
	public interface F8<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object, T7 extends @Nullable Object, T8 extends @Nullable Object, R extends @Nullable Object> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);
	}

}
