package net.jqwik.engine.execution;

import java.lang.annotation.*;
import java.util.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.lifecycle.*;

abstract class AbstractLifecycleContext implements LifecycleContext {

	private final Reporter reporter;
	protected final ResolveParameterHook resolveParameterHook;

	protected AbstractLifecycleContext(Reporter reporter, ResolveParameterHook resolveParameterHook) {
		this.reporter = reporter;
		this.resolveParameterHook = resolveParameterHook;
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
