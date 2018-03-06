package net.jqwik.support;

import org.junit.platform.commons.support.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class MethodParameter {

	private final Parameter parameter;
	private final AnnotatedType annotatedType;

	public MethodParameter(Parameter parameter, AnnotatedType annotatedType) {
		this.parameter = parameter;
		this.annotatedType = annotatedType;
	}

	public boolean isAnnotatedParameterized() {
		return (annotatedType instanceof AnnotatedParameterizedType);
	}

	public boolean isAnnotated(Class<? extends Annotation> annotationType) {
		return AnnotationSupport.isAnnotated(parameter, annotationType);
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return findAnnotation(annotationClass).orElse(null);
	}

	public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass) {
		return AnnotationSupport.findAnnotation(parameter, annotationClass);
	}

	public Class<?> getType() {
		return parameter.getType();
	}

	public Type getParameterizedType() {
		return parameter.getParameterizedType();
	}

	@Override
	public String toString() {
		return parameter.toString();
	}

	public List<Annotation> findAllAnnotations() {
		return JqwikAnnotationSupport.findAllAnnotations(parameter);
	}

	public AnnotatedParameterizedType getAnnotatedType() {
		if (isAnnotatedParameterized())
			return (AnnotatedParameterizedType) annotatedType;
		else
			return null;
	}
}
