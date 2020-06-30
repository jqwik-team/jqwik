package net.jqwik.engine;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.execution.reporting.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

public class TestHelper {

	public static PropertyLifecycleContext propertyLifecycleContextFor(
		Class<?> containerClass,
		String methodName,
		Class<?>... parameterTypes
	) {
		PropertyMethodDescriptor methodDescriptor =
			(PropertyMethodDescriptor) TestDescriptorBuilder.forMethod(containerClass, methodName, parameterTypes).build();
		Object instance = JqwikReflectionSupport.newInstanceWithDefaultConstructor(containerClass);
		return new DefaultPropertyLifecycleContext(
			methodDescriptor,
			instance,
			new DefaultReporter((key, value) -> {}, methodDescriptor),
			ResolveParameterHook.DO_NOT_RESOLVE
		);
	}

	public static List<MethodParameter> getParametersFor(Class<?> aClass, String methodName) {
		return getParameters(getMethod(aClass, methodName), aClass);
	}

	private static List<MethodParameter> getParameters(Method method, Class<?> containerClass) {
		return getMethodParameters(method, containerClass);
	}

	public static Method getMethod(Class<?> aClass, String methodName) {
		return Arrays.stream(aClass.getDeclaredMethods()).filter(m -> m.getName().equals(methodName)).findFirst().get();
	}

	public static PropertyMethodDescriptor createPropertyMethodDescriptor(
		Class<?> containerClass, String methodName, String seed, int tries, int maxDiscardRatio, ShrinkingMode shrinking
	) {
		UniqueId uniqueId = UniqueId.root("test", "i dont care");
		Method method = getMethod(containerClass, methodName);
		PropertyConfiguration propertyConfig = new PropertyConfiguration(
			"Property",
			seed,
			null,
			null,
			tries,
			maxDiscardRatio,
			shrinking,
			GenerationMode.AUTO,
			AfterFailureMode.PREVIOUS_SEED,
			EdgeCasesMode.MIXIN
		);
		return new PropertyMethodDescriptor(uniqueId, method, containerClass, propertyConfig);
	}

	public static LifecycleHooksSupplier emptyLifecycleSupplier() {
		return lifecycleSupplier(Collections.emptyList());
	}

	public static LifecycleHooksSupplier lifecycleSupplier(List<AroundPropertyHook> aroundPropertyHooks) {
		return new LifecycleHooksSupplier() {
			@Override
			public AroundPropertyHook aroundPropertyHook(PropertyMethodDescriptor propertyMethodDescriptor) {
				return HookSupport.combineAroundPropertyHooks(aroundPropertyHooks);
			}

			@Override
			public AroundTryHook aroundTryHook(PropertyMethodDescriptor methodDescriptor) {
				return AroundTryHook.BASE;
			}

			@Override
			public BeforeContainerHook beforeContainerHook(TestDescriptor descriptor) {
				return BeforeContainerHook.DO_NOTHING;
			}

			@Override
			public AfterContainerHook afterContainerHook(TestDescriptor descriptor) {
				return AfterContainerHook.DO_NOTHING;
			}

			@Override
			public ResolveParameterHook resolveParameterHook(TestDescriptor descriptor) {
				return ResolveParameterHook.DO_NOT_RESOLVE;
			}

			@Override
			public SkipExecutionHook skipExecutionHook(TestDescriptor testDescriptor) {
				return descriptor -> SkipExecutionHook.SkipResult.doNotSkip();
			}
		};
	}

	public static List<MethodParameter> getParameters(PropertyMethodDescriptor methodDescriptor) {
		return getParameters(methodDescriptor.getTargetMethod(), methodDescriptor.getContainerClass());
	}

	public static Reporter reporter() {
		return new Reporter() {
			@Override
			public void publishValue(final String key, final String value) {
			}

			@Override
			public void publishReport(final String key, final Object object) {
			}

			@Override
			public void publishReports(final String key, final Map<String, Object> objects) {
			}
		};
	}

	public static Supplier<TryLifecycleContext> tryLifecycleContextSupplier() {
		return () -> new TryLifecycleContext() {
			@Override
			public Method targetMethod() {
				return null;
			}

			@Override
			public Class<?> containerClass() {
				return null;
			}

			@Override
			public Object testInstance() {
				return null;
			}

			@Override
			public String label() {
				return null;
			}

			@Override
			public Optional<AnnotatedElement> optionalElement() {
				return Optional.empty();
			}

			@Override
			public Optional<Class<?>> optionalContainerClass() {
				return Optional.empty();
			}

			@Override
			public Reporter reporter() {
				return TestHelper.reporter();
			}

			@Override
			public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass) {
				return Optional.empty();
			}

			@Override
			public <T> T newInstance(Class<T> clazz) {
				return null;
			}

			@Override
			public Optional<ResolveParameterHook.ParameterSupplier> resolveParameter(Executable executable, int index) {
				return Optional.empty();
			}
		};
	}
}
