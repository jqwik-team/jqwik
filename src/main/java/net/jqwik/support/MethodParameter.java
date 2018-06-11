package net.jqwik.support;

import org.junit.platform.commons.support.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class MethodParameter {

	private final Parameter parameter;
	private final Type resolvedType;

	public MethodParameter(Parameter parameter, Type resolvedType) {
		this.parameter = parameter;
		this.resolvedType = resolvedType;
	}

	public boolean isAnnotatedParameterized() {
		if (genericsCouldBeResolved()) {
			return false;
		}
		return (parameter.getAnnotatedType() instanceof AnnotatedParameterizedType);
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

	public Type getType() {
		return genericsCouldBeResolved() ? resolvedType : parameter.getParameterizedType();
	}

	private boolean genericsCouldBeResolved() {
		return resolvedType != parameter.getParameterizedType();
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
			return (AnnotatedParameterizedType) parameter.getAnnotatedType();
		else
			return null;
	}
}
