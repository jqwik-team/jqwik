package net.jqwik;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.properties.*;
import net.jqwik.support.*;
import org.junit.platform.engine.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class TestHelper {
	public static List<MethodParameter> getParametersFor(Class<?> aClass, String methodName) {
		return getParameters(getMethod(aClass, methodName), aClass);
	}

	private static List<MethodParameter> getParameters(Method method, Class<?> containerClass) {
		return Arrays.stream(JqwikReflectionSupport.getMethodParameters(method, containerClass)).collect(Collectors.toList());
	}

	public static Method getMethod(Class<?> aClass, String methodName) {
		return Arrays.stream(aClass.getDeclaredMethods()).filter(m -> m.getName().equals(methodName)).findFirst().get();
	}

	public static <T> T generateFirst(Arbitrary<T> arbitrary) {
		RandomGenerator<T> generator = arbitrary.generator(1);
		return generateNext(generator);
	}

	public static <T> T generateNext(RandomGenerator<T> generator) {
		return generator.next(SourceOfRandomness.current()).value();
	}

	public static <T> T generateUntil(RandomGenerator<T> generator, Predicate<T> untilCondition) {
		T actual = generateNext(generator);
		while (untilCondition.negate().test(actual)) {
			actual = generateNext(generator);
		}
		return actual;
	}

	public static PropertyMethodDescriptor createPropertyMethodDescriptor(
		Class<?> containerClass, String methodName, String seed, int tries, int maxDiscardRatio, ShrinkingMode shrinking
	) {
		UniqueId uniqueId = UniqueId.root("test", "i dont care");
		Method method = getMethod(containerClass, methodName);
		PropertyConfiguration propertyConfig = new PropertyConfiguration("Property", seed, tries, maxDiscardRatio, shrinking, new Reporting[0]);
		return new PropertyMethodDescriptor(uniqueId, method, containerClass, propertyConfig);
	}
}
