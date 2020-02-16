package net.jqwik.engine.execution;

import java.lang.annotation.*;
import java.util.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

abstract class AbstractLifecycleContext implements LifecycleContext {

	private final Reporter reporter;

	public AbstractLifecycleContext(Reporter reporter) {
		this.reporter = reporter;
	}

	@Override
	public Reporter reporter() {
		return reporter;
	}

	@Override
	public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass) {
		return annotatedElement()
				   .flatMap(element -> AnnotationSupport.findAnnotation(element, annotationClass));
	}

}
