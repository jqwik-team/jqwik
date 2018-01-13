package net.jqwik;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.RandomGenerator;

public class TestHelper {
	public static List<Parameter> getParametersFor(Class<?> aClass, String methodName) {
		return getParameters(getMethod(aClass, methodName));
	}

	private static List<Parameter> getParameters(Method method) {
		return Arrays.stream(method.getParameters()).collect(Collectors.toList());
	}

	public static Method getMethod(Class<?> aClass, String methodName) {
		return Arrays.stream(aClass.getDeclaredMethods()).filter(m -> m.getName().equals(methodName)).findFirst().get();
	}

	public static <T> T generateFirst(Arbitrary<T> arbitrary) {
		RandomGenerator<T> generator = arbitrary.generator(1);
		return generateNext(generator);
	}

	public static <T> T generateNext(RandomGenerator<T> generator) {
		return generator.next(new Random()).value();
	}

	public static <T> T generateUntil(RandomGenerator<T> generator, Predicate<T> untilCondition) {
		T actual = generateNext(generator);
		while (untilCondition.negate().test(actual)) {
			actual = generateNext(generator);
		}
		return actual;
	}



}
