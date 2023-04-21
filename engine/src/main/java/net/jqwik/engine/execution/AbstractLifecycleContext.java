package net.jqwik.engine.execution;

import java.lang.annotation.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.execution.LifecycleContextSupport.*;

abstract class AbstractLifecycleContext implements LifecycleContext {

	private Reporter reporter;
	private final TestDescriptor descriptor;

	protected AbstractLifecycleContext(Reporter reporter, TestDescriptor descriptor) {
		this.reporter = reporter;
		this.descriptor = descriptor;
	}

	@Override
	public Reporter reporter() {
		return reporter;
	}

	@Override
	public void wrapReporter(Function<Reporter, Reporter> wrapper) {
		this.reporter = wrapper.apply(this.reporter);
	}

	@Override
	public String label() {
		return descriptor.getDisplayName();
	}

	@Override
	public <A extends Annotation> Optional<A> findAnnotation(Class<A> annotationClass) {
		return optionalElement()
			.flatMap(element -> AnnotationSupport.findAnnotation(element, annotationClass));
	}

	@Override
	public <A extends Annotation> List<A> findAnnotationsInContainer(Class<A> annotationClass) {
		return optionalElement()
			.map(element -> {
				List<A> annotations = new ArrayList<>();
				appendAnnotations(parentContainer(), annotationClass, annotations);
				return annotations;
			})
			.orElse(Collections.emptyList());
	}

	@Override
	public <T extends Annotation> List<T> findRepeatableAnnotations(Class<T> annotationClass) {
		return AnnotationSupport.findRepeatableAnnotations(optionalElement(), annotationClass);
	}

	private Optional<ContainerClassDescriptor> parentContainer() {
		return parentContainer(descriptor);
	}

	private Optional<ContainerClassDescriptor> parentContainer(TestDescriptor descriptor) {
		return descriptor.getParent()
						 .filter(parent -> parent instanceof ContainerClassDescriptor)
						 .map(parent -> (ContainerClassDescriptor) parent);
	}

	private <T extends Annotation> void appendAnnotations(
		Optional<ContainerClassDescriptor> optionalContainer,
		Class<T> annotationClass,
		List<T> annotations
	) {
		optionalContainer.ifPresent(container -> {
			annotations.addAll(JqwikAnnotationSupport.findContainerAnnotations(container.getContainerClass(), annotationClass));
			appendAnnotations(parentContainer(container), annotationClass, annotations);
		});
	}

	protected String toString(Class<? extends LifecycleContext> contextType) {
		String uniqueIdDescription = formatUniqueId(descriptor.getUniqueId());
		return String.format("%s(%s)", contextType.getSimpleName(), uniqueIdDescription);
	}

}
