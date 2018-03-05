package net.jqwik.support;

import org.junit.platform.commons.support.*;

import java.lang.annotation.*;
import java.lang.reflect.*;

public class MethodParameter {

	@Deprecated
	public Parameter getNativeParameter() {
		return parameter;
	}

	private final Parameter parameter;

	public MethodParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public boolean isAnnotated(Class<? extends Annotation> annotationType) {
		return AnnotationSupport.isAnnotated(parameter, annotationType);
	}

	public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
		return parameter.getDeclaredAnnotation(annotationClass);
	}

	public Class<?> getType() {
		return parameter.getType();
	}
}
