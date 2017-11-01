package net.jqwik.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class JqwikAnnotationSupport {

	/**
	 * Find all annotations, even if they are repeatable or only present through meta-annotations
	 *
	 * @param element
	 */
	public static Annotation[] getAllAnnotations(AnnotatedElement element) {

		return element.getDeclaredAnnotations();
	}
}
