package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;

import org.mockito.*;

import net.jqwik.api.*;

public class ShrinkingTestsBase {

	public static final Consumer<List<Object>> falsifiedReporter = ignore -> {};

	public static final Reporter reporter = Mockito.mock(Reporter.class);

	@SuppressWarnings("unchecked")
	public static <T> List<Shrinkable<Object>> toListOfShrinkables(Shrinkable<T>... shrinkables) {
		ArrayList<Shrinkable<Object>> parameterList = new ArrayList<>();
		for (Shrinkable<T> shrinkable : shrinkables) {
			parameterList.add((Shrinkable<Object>) shrinkable);
		}
		return parameterList;
	}

	@SuppressWarnings("unchecked")
	public static <T> Falsifier<List<Object>> parameterFalsifier(Falsifier<T> tFalsifier) {
		return params -> {
			T t = (T) params.get(0);
			return tFalsifier.execute(t);
		};
	}

	@SuppressWarnings("unchecked")
	public static <T> TestingFalsifier<List<Object>> falsifier(Predicate<T> tFalsifier) {
		return params -> {
			T seq = (T) params.get(0);
			return tFalsifier.test(seq);
		};
	}

	@SuppressWarnings("unchecked")
	public static <T1, T2> TestingFalsifier<List<Object>> falsifier(BiPredicate<T1, T2> t1t2Falsifier) {
		return params -> {
			T1 t1 = (T1) params.get(0);
			T2 t2 = (T2) params.get(1);
			return t1t2Falsifier.test(t1, t2);
		};
	}

	public static AssertionError failAndCatch(String message) {
		try {
			throw new AssertionError(message);
		} catch (AssertionError error) {
			return error;
		}
	}

}
