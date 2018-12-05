package net.jqwik;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.lifecycle.*;
import net.jqwik.properties.*;
import net.jqwik.support.*;

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
		PropertyConfiguration propertyConfig = new PropertyConfiguration("Property", seed, null, null, tries, maxDiscardRatio, shrinking, GenerationMode.AUTO, AfterFailureMode.PREVIOUS_SEED);
		return new PropertyMethodDescriptor(uniqueId, method, containerClass, propertyConfig);
	}

	public static LifecycleSupplier nullLifecycleSupplier() {
		return new LifecycleSupplier() {
			@Override
			public AroundPropertyHook aroundPropertyHook(PropertyMethodDescriptor propertyMethodDescriptor) {
				return new AutoCloseableHook();
			}
		};
	}

	public static List<MethodParameter> getParameters(PropertyMethodDescriptor methodDescriptor) {
		return
			Arrays.stream(JqwikReflectionSupport
							  .getMethodParameters(methodDescriptor.getTargetMethod(), methodDescriptor.getContainerClass()))
				  .collect(Collectors.toList());

	}
}
