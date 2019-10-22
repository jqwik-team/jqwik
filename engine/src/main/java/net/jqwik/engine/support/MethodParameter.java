package net.jqwik.engine.support;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.commons.support.*;

public class MethodParameter {

	public Parameter getRawParameter() {
		return rawParameter;
	}

	private final Parameter rawParameter;
	private final TypeResolution resolution;

	public MethodParameter(Parameter rawParameter, TypeResolution resolution) {
		this.rawParameter = rawParameter;
		this.resolution = resolution;
	}

	public boolean isParameterized() {
		return (resolution.annotatedType() instanceof AnnotatedParameterizedType);
	}

	public boolean isAnnotated(Class<? extends Annotation> annotationType) {
		return AnnotationSupport.isAnnotated(rawParameter, annotationType);
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return findAnnotation(annotationClass).orElse(null);
	}

	public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass) {
		return AnnotationSupport.findAnnotation(rawParameter, annotationClass);
	}

	public Type getType() {
		return resolution.type();
	}

	@Override
	public String toString() {
		return rawParameter.toString();
	}

	public List<Annotation> findAllAnnotations() {
		return JqwikAnnotationSupport.findAllAnnotations(rawParameter);
	}

	public AnnotatedType getAnnotatedType() {
		return resolution.annotatedType();
	}

}
