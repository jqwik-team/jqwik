package net.jqwik.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Stream;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.assertThat;

class JqwikAnnotationSupportTests {

	@Example
	void singlePresentAnnotationIsFound() throws NoSuchMethodException {
		Stream<Class> types = typesFromMethod("singleAnnotation");
		assertThat(types).containsExactly(ForAll.class);
	}

	@Example
	void twoAnnotationsAreFound() throws NoSuchMethodException {
		Stream<Class> types = typesFromMethod("twoAnnotations");
		assertThat(types).containsExactly(ForAll.class, StringLength.class);
	}

	@Example
	void repeatableAnnotationsAreFound() throws NoSuchMethodException {
		List<Annotation> annotations = annotationsFromMethod("repeatableAnnotations");
		Stream<Class> types = annotations.stream().map(a -> a.annotationType());

		assertThat(types).containsExactly(CharsList.class);
	}

	static class AClass {
		void singleAnnotation(@ForAll String param) {}
		void twoAnnotations(@ForAll @StringLength(max = 10) String param) {}
		void repeatableAnnotations(@Chars({'a'}) @Chars({'b'}) String param) {}
	}

	private List<Annotation> annotationsFromMethod(String methodName) throws NoSuchMethodException {
		Parameter param = parameterFromMethod(methodName);
		return JqwikAnnotationSupport.findAllAnnotations(param);
	}

	private Parameter parameterFromMethod(String methodName) throws NoSuchMethodException {
		return AClass.class.getDeclaredMethod(methodName, String.class).getParameters()[0];
	}

	private Stream<Class> typesFromMethod(String methodName) throws NoSuchMethodException {
		List<Annotation> annotations = annotationsFromMethod(methodName);
		return annotations.stream().map(a -> a.annotationType());
	}

}
