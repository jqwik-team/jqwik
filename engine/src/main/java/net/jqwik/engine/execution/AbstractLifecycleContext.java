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

abstract class AbstractLifecycleContext implements LifecycleContext {

	private Reporter reporter;
	private final TestDescriptor self;

	protected AbstractLifecycleContext(Reporter reporter, TestDescriptor self) {
		this.reporter = reporter;
		this.self = self;
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
		return self.getDisplayName();
	}

	@Override
	public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass) {
		return optionalElement()
			.flatMap(element -> AnnotationSupport.findAnnotation(element, annotationClass));
	}

	@Override
	public <T extends Annotation> List<T> findAnnotationsInContainer(Class<T> annotationClass) {
		return optionalElement()
			.map(element -> {
				List<T> annotations = new ArrayList<>();
				appendAnnotations(parentContainer(), annotationClass, annotations);
				return annotations;
			})
			.orElse(Collections.emptyList());
	}

	private Optional<ContainerClassDescriptor> parentContainer() {
		return parentContainer(self);
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
}
