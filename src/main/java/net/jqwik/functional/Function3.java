package net.jqwik.functional;

/**
 * A function with three arguments.
 *
 * @param <T1> argument 1 of the function
 * @param <T2> argument 2 of the function
 * @param <T3> argument 3 of the function
 * @param <R> return type of the function
 */
@FunctionalInterface
public interface Function3<T1, T2, T3, R> {
    R apply(T1 t1, T2 t2, T3 t3);
}