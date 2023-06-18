package net.jqwik.engine.execution;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static net.jqwik.engine.execution.LifecycleContextSupport.*;

public class DefaultTryLifecycleContext implements TryLifecycleContext {
	private final PropertyLifecycleContext propertyContext;

	public DefaultTryLifecycleContext(PropertyLifecycleContext propertyContext) {
		this.propertyContext = propertyContext;
	}

	@Override
	public Method targetMethod() {
		return propertyContext.targetMethod();
	}

	@Override
	public Class<?> containerClass() {
		return propertyContext.containerClass();
	}

	@Override
	public Object testInstance() {
		return propertyContext.testInstance();
	}

	@Override
	public List<Object> testInstances() {
		return propertyContext.testInstances();
	}

	@Override
	public String label() {
		return propertyContext.label();
	}

	@Override
	public Optional<AnnotatedElement> optionalElement() {
		return propertyContext.optionalElement();
	}

	@Override
	public Optional<Class<?>> optionalContainerClass() {
		return Optional.of(propertyContext.containerClass());
	}

	@Override
	public Reporter reporter() {
		return propertyContext.reporter();
	}

	@Override
	public void wrapReporter(Function<Reporter, Reporter> wrapper) {
		propertyContext.wrapReporter(wrapper);
	}

	@Override
	public <T> T newInstance(Class<T> clazz) {
		return propertyContext.newInstance(clazz);
	}

	@Override
	public Optional<ResolveParameterHook.ParameterSupplier> resolveParameter(Executable executable, int index) {
		return propertyContext.resolveParameter(executable, index);
	}

	@Override
	public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass) {
		return propertyContext.findAnnotation(annotationClass);
	}

	@Override
	public <T extends Annotation> List<T> findAnnotationsInContainer(Class<T> annotationClass) {
		return propertyContext.findAnnotationsInContainer(annotationClass);
	}

	@Override
	public <T extends Annotation> List<T> findRepeatableAnnotations(Class<T> annotationClass) {
		return propertyContext.findRepeatableAnnotations(annotationClass);
	}

	@Override
	public String toString() {
		return String.format("TryLifecycleContext:%s", propertyContext);
	}
}
