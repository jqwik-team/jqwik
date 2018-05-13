package net.jqwik.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

public class JqwikAnnotationSupport {

	/**
	 * Find all annotations in an element, even if they are repeatable or only present through meta-annotations
	 *
	 * @param element the element to check for present annotations
	 * @return a list of all found annotations
	 */
	public static List<Annotation> findAllAnnotations(AnnotatedElement element) {

		List<Annotation> annotations = new ArrayList<>();
		List<Annotation> presentAnnotations = Arrays.asList(element.getAnnotations());
		annotations.addAll(presentAnnotations);
		presentAnnotations.forEach(annotation -> appendMetaAnnotations(annotation, annotations));
		return annotations;
	}

	private static void appendMetaAnnotations(Annotation annotation, List<Annotation> collector) {
		Annotation[] metaAnnotationCandidates = annotation.annotationType().getDeclaredAnnotations();
		Arrays.stream(metaAnnotationCandidates) //
				.filter(candidate -> !isInJavaLangAnnotationPackage(candidate.annotationType())) //
				.filter(candidate -> !collector.contains(candidate)) //
				.forEach(metaAnnotation -> {
					collector.add(metaAnnotation);
					appendMetaAnnotations(metaAnnotation, collector);
				});
	}

	private static boolean isInJavaLangAnnotationPackage(Class<? extends Annotation> annotationType) {
		return (annotationType != null && annotationType.getName().startsWith("java.lang.annotation"));
	}

}
