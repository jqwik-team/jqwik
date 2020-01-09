package net.jqwik.engine;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.support.*;

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

	public static PropertyMethodDescriptor createPropertyMethodDescriptor(
		Class<?> containerClass, String methodName, String seed, int tries, int maxDiscardRatio, ShrinkingMode shrinking
	) {
		UniqueId uniqueId = UniqueId.root("test", "i dont care");
		Method method = getMethod(containerClass, methodName);
		PropertyConfiguration propertyConfig = new PropertyConfiguration("Property", seed, null, null, tries, maxDiscardRatio, shrinking, GenerationMode.AUTO, AfterFailureMode.PREVIOUS_SEED);
		return new PropertyMethodDescriptor(uniqueId, method, containerClass, propertyConfig);
	}

	public static LifecycleHooksSupplier nullLifecycleSupplier() {
		return new LifecycleHooksSupplier() {
			@Override
			public AroundPropertyHook aroundPropertyHook(PropertyMethodDescriptor propertyMethodDescriptor) {
				return new AutoCloseableHook();
			}

			@Override
			public SkipExecutionHook skipExecutionHook(TestDescriptor testDescriptor) {
				return descriptor -> SkipExecutionHook.SkipResult.doNotSkip();
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
