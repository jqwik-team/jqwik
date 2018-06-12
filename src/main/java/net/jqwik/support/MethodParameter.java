package net.jqwik.support;

import org.junit.platform.commons.support.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class MethodParameter {

	private final Parameter parameter;
	private final GenericsResolution resolution;

	public MethodParameter(Parameter parameter, GenericsResolution resolution) {
		this.parameter = parameter;
		this.resolution = resolution;
	}

	public boolean isAnnotatedParameterized() {
		if (genericsResolutionChangedType()) {
			// TODO: What if a resolved type has annotations?
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
		return genericsResolutionChangedType() ? resolution.type() : parameter.getParameterizedType();
	}

	private boolean genericsResolutionChangedType() {
		return resolution.typeHasChanged();
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
