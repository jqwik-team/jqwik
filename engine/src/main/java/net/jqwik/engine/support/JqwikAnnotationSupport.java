package net.jqwik.engine.support;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;
import org.junit.platform.commons.support.*;

import net.jqwik.engine.discovery.predicates.*;

/**
 * Provide stuff that org.junit.commons.support.AnnotationSupport does not.
 */
public class JqwikAnnotationSupport {

	/**
	 * Find all annotations in an element, even if they are repeatable or only present through meta-annotations
	 *
	 * @param element the element to check for present annotations
	 * @return a list of all found annotations
	 */
	public static List<Annotation> findAllAnnotations(AnnotatedElement element) {
		List<Annotation> annotations = new ArrayList<>();
		List<Annotation> presentAnnotations = Arrays.asList(getDeclaredAnnotations(element));
		annotations.addAll(presentAnnotations);
		presentAnnotations.forEach(annotation -> appendMetaAnnotations(annotation, annotations));
		return annotations;
	}

	private static Annotation[] getDeclaredAnnotations(AnnotatedElement element) {
		if (element instanceof AnnotatedArrayType) {
			return ((AnnotatedArrayType) element).getAnnotatedGenericComponentType().getAnnotations();
		}
		return element.getAnnotations();
	}

	private static void appendMetaAnnotations(Annotation annotation, List<Annotation> collector) {
		Stream<Annotation> metaAnnotationStream = streamMetaAnnotations(annotation);
		metaAnnotationStream
			.filter(candidate -> !collector.contains(candidate))
			.forEach(metaAnnotation -> {
				collector.add(metaAnnotation);
				appendMetaAnnotations(metaAnnotation, collector);
			});
	}

	public static List<Annotation> allMetaAnnotations(Annotation annotation) {
		List<Annotation> all = new ArrayList<>();
		Stream<Annotation> metaAnnotationStream = streamMetaAnnotations(annotation);
		metaAnnotationStream
			.filter(candidate -> !all.contains(candidate))
			.forEach(metaAnnotation -> {
				all.add(metaAnnotation);
				appendMetaAnnotations(metaAnnotation, all);
			});
		return all;
	}

	private static Stream<Annotation> streamMetaAnnotations(Annotation annotation) {
		Annotation[] metaAnnotationCandidates = annotation.annotationType().getDeclaredAnnotations();
		return Arrays.stream(metaAnnotationCandidates)
					 .filter(candidate -> !isInJavaLangAnnotationPackage(candidate.annotationType()))
					 .filter(candidate -> !isApiAnnotation(candidate.annotationType()));

	}

	private static boolean isApiAnnotation(Class<? extends Annotation> annotationType) {
		return annotationType == API.class;
	}

	private static boolean isInJavaLangAnnotationPackage(Class<? extends Annotation> annotationType) {
		return (annotationType != null && annotationType.getName().startsWith("java.lang.annotation"));
	}

	public static <A extends Annotation> List<A> findRepeatableAnnotationOnElementOrContainer(
		AnnotatedElement element,
		Class<A> annotationType
	) {
		List<A> annotations = new ArrayList<>(AnnotationSupport.findRepeatableAnnotations(element, annotationType));
		if (element instanceof Member) {
			AnnotatedElement container = ((Member) element).getDeclaringClass();
			annotations.addAll(findRepeatableAnnotationOnElementOrContainer(container, annotationType));
		}
		if (isGroup(element)) {
			AnnotatedElement container = getGroupContainer((Class<?>) element);
			annotations.addAll(findRepeatableAnnotationOnElementOrContainer(container, annotationType));
		}
		return annotations;
	}

	private static AnnotatedElement getGroupContainer(Class<?> group) {
		return group.getDeclaringClass();
	}

	private static boolean isGroup(AnnotatedElement element) {
		if (element instanceof Class) {
			return new IsContainerAGroup().test((Class<?>) element);
		}
		return false;
	}

	/**
	 * Find all annotation instances of a given type on a container class, even if they are repeatable or annotated.
	 *
	 * Sort those annotations from closer (directly on class) to more remote (on extended classes and interfaces)
	 */
	public static <A extends Annotation> List<A> findContainerAnnotations(
		Class<?> container,
		Class<A> annotationType
	) {
		Optional<A> optional = AnnotationSupport.findAnnotation(container, annotationType);
		return optional.map(annotation -> {
			List<A> annotations = new ArrayList<>();
			annotations.add(annotation);
			appendInheritedAnnotations(container, annotationType,annotation, annotations);
			return annotations;
		}).orElse(Collections.emptyList());
	}

	private static <A extends Annotation> void appendInheritedAnnotations(
		Class<?> container,
		Class<A> annotationType,
		A annotation,
		List<A> collector
	) {
		if (isInherited(annotationType)) {
			List<A> inheritedAnnotations = inheritedAnnotations(container, annotationType);
			for (A inheritedAnnotation : inheritedAnnotations) {
				if (inheritedAnnotation != annotation) {
					collector.add(inheritedAnnotation);
				}
			}
		}
	}
	private static <A extends Annotation> List<A> inheritedAnnotations(Class<?> container, Class<A> annotationType) {
		List<A> inheritedAnnotations = new ArrayList<>();
		Class<?> superclass = container.getSuperclass();
		if (superclass != null) {
			inheritedAnnotations.addAll(findContainerAnnotations(superclass, annotationType));
		}
		for (Class<?> anInterface : container.getInterfaces()) {
			inheritedAnnotations.addAll(findContainerAnnotations(anInterface, annotationType));
		}
		return inheritedAnnotations;
	}

	private static <A extends Annotation> boolean isInherited(Class<A> annotationType) {
		return AnnotationSupport.isAnnotated(annotationType, Inherited.class);
	}
}
