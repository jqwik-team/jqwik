package net.jqwik.engine;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.discovery.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.execution.reporting.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

public class TestHelper {

	public static final int DEFAULT_TRIES = 1000;
	public static final int BOUNDED_SHRINKING_SECONDS = 10;
	public static final int DEFAULT_MAX_DISCARD_RATIO = 5;
	public static final AfterFailureMode DEFAULT_AFTER_FAILURE = AfterFailureMode.PREVIOUS_SEED;
	public static final GenerationMode DEFAULT_GENERATION = GenerationMode.AUTO;
	public static final EdgeCasesMode DEFAULT_EDGE_CASES = EdgeCasesMode.MIXIN;
	public static final ShrinkingMode DEFAULT_SHRINKING = ShrinkingMode.BOUNDED;
	public static final FixedSeedMode DEFAULT_WHEN_FIXED_SEED = FixedSeedMode.ALLOW;

	public static PropertyAttributesDefaults propertyAttributesDefaults() {
		return PropertyAttributesDefaults.with(
			DEFAULT_TRIES,
			DEFAULT_MAX_DISCARD_RATIO,
			DEFAULT_AFTER_FAILURE,
			DEFAULT_GENERATION,
			DEFAULT_EDGE_CASES,
			DEFAULT_SHRINKING,
			BOUNDED_SHRINKING_SECONDS,
			DEFAULT_WHEN_FIXED_SEED
		);
	}

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
		PropertyAttributes propertyAttributes = new DefaultPropertyAttributes(
			tries,
			maxDiscardRatio,
			shrinking,
			null,
			null,
			null,
			null,
			seed,
			null
		);

		PropertyConfiguration propertyConfig = new PropertyConfiguration(
			propertyAttributes,
			propertyAttributesDefaults(),
			GenerationInfo.NULL,
			seed,
			tries,
			GenerationMode.AUTO
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

			@Override
			public InvokePropertyMethodHook invokePropertyMethodHook(TestDescriptor testDescriptor) {
				return InvokePropertyMethodHook.DEFAULT;
			}

			@Override
			public ProvidePropertyInstanceHook providePropertyInstanceHook(TestDescriptor testDescriptor) {
				return ProvidePropertyInstanceHook.DEFAULT;
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
			public void wrapReporter(Function<Reporter, Reporter> wrapper) {
			}

			@Override
			public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass) {
				return Optional.empty();
			}

			@Override
			public <T extends Annotation> List<T> findAnnotationsInContainer(Class<T> annotationClass) {
				return Collections.emptyList();
			}

			@Override
			public <T extends Annotation> List<T> findRepeatableAnnotations(Class<T> annotationClass) {
				return Collections.emptyList();
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
