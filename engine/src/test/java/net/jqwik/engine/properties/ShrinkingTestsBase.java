package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;

import org.mockito.*;

import net.jqwik.api.*;

public class ShrinkingTestsBase {

	protected final Consumer<List<Object>> falsifiedReporter = ignore -> {};

	protected final Reporter reporter = Mockito.mock(Reporter.class);

	@SuppressWarnings("unchecked")
	protected <T> List<Shrinkable<Object>> toListOfShrinkables(Shrinkable<T>... shrinkables) {
		ArrayList<Shrinkable<Object>> parameterList = new ArrayList<>();
		for (Shrinkable<T> shrinkable : shrinkables) {
			parameterList.add((Shrinkable<Object>) shrinkable);
		}
		return parameterList;
	}

	@SuppressWarnings("unchecked")
	protected <T> TestingFalsifier<List<Object>> falsifier(Predicate<T> tFalsifier) {
		return params -> {
			T seq = (T) params.get(0);
			return tFalsifier.test(seq);
		};
	}

	@SuppressWarnings("unchecked")
	protected <T1, T2> TestingFalsifier<List<Object>> falsifier(BiPredicate<T1, T2> t1t2Falsifier) {
		return params -> {
			T1 t1 = (T1) params.get(0);
			T2 t2 = (T2) params.get(1);
			return t1t2Falsifier.test(t1, t2);
		};
	}

	protected AssertionError failAndCatch(String message) {
		try {
			throw new AssertionError(message);
		} catch (AssertionError error) {
			return error;
		}
	}

}
