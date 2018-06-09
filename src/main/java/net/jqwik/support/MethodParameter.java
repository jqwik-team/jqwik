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
		if (typeVariableWasResolved()) {
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
		return typeVariableWasResolved() ? resolvedType : parameter.getType();
	}

	public Type getParameterizedType() {
		if (typeVariableWasResolved()) {
			return resolvedType;
		}
		return parameter.getParameterizedType();
	}

	private boolean typeVariableWasResolved() {
		return resolvedType != null;
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
