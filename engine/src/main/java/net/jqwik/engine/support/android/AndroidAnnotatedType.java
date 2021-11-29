package net.jqwik.engine.support.android;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.jetbrains.annotations.*;

public class AndroidAnnotatedType implements AnnotatedType, AnnotatedParameterizedType {

	private final Object delegee;

	public AndroidAnnotatedType(Object delegee) {
		this.delegee = delegee;
	}

	@Override
	public Type getType() {
		return (Type) delegee;
	}

	@Override
	public <T extends Annotation> T getAnnotation(@NotNull Class<T> annotationClass) {
		return annotatedElement().map(a -> a.getAnnotation(annotationClass)).orElse(null);
	}

	private Optional<AnnotatedElement> annotatedElement() {
		if (delegee instanceof AnnotatedElement) {
			return Optional.of((AnnotatedElement) delegee);
		}
		return Optional.empty();
	}

	private Optional<ParameterizedType> parameterizedType() {
		if (delegee instanceof ParameterizedType) {
			return Optional.of((ParameterizedType) delegee);
		}
		return Optional.empty();
	}

	@Override
	public Annotation[] getAnnotations() {
		return annotatedElement().map(AnnotatedElement::getAnnotations).orElse(new Annotation[0]);
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return annotatedElement().map(AnnotatedElement::getDeclaredAnnotations).orElse(new Annotation[0]);
	}

	@Override
	public boolean isAnnotationPresent(@NotNull Class<? extends Annotation> annotationClass) {
		return annotatedElement().map(a -> a.isAnnotationPresent(annotationClass)).orElse(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
		return annotatedElement().map(a -> a.getAnnotationsByType(annotationClass)).orElse((T[]) new Annotation[0]);
	}

	@Override
	public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
		return annotatedElement().map(a -> a.getDeclaredAnnotation(annotationClass)).orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
		return annotatedElement().map(a -> a.getDeclaredAnnotationsByType(annotationClass)).orElse((T[]) new Annotation[0]);
	}

	@Override
	public AnnotatedType[] getAnnotatedActualTypeArguments() {
		return parameterizedType()
			.map(parameterizedType -> Arrays.stream((parameterizedType).getActualTypeArguments())
											.map(AndroidAnnotatedType::new)
											.toArray(AnnotatedType[]::new))
			.orElse(new AnnotatedType[0]);
	}

	// Part of interface for Java >= 9
	public AnnotatedType getAnnotatedOwnerType() {
		return null;
	}
}
