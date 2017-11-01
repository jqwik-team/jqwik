package net.jqwik.support;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

public class JqwikAnnotationSupport {

	/**
	 * Find all annotations, even if they are repeatable or only present through meta-annotations
	 *
	 * @param element
	 */
	public static List<Annotation> findAllAnnotations(AnnotatedElement element) {

		List<Annotation> annotations = new ArrayList<>();
		List<Annotation> presentAnnotations = Arrays.asList(element.getAnnotations());
		annotations.addAll(presentAnnotations);
//		presentAnnotations.stream() //
//				.map(annotation -> annotation.annotationType().getAnnotation(Repeatable.class)) //
//				.filter(repeatable -> repeatable != null) //
//				.forEach(repeatable -> {
//					Class<? extends Annotation> repeating = repeatable.value();
//					annotations.addAll(Arrays.asList(element.getAnnotationsByType(repeating)));
//				});
		return annotations;
	}
}
