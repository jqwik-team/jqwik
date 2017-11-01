package net.jqwik.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.assertj.core.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;

class JqwikAnnotationSupportTests {

	@Example
	void singlePresentAnnotationIsFound() throws NoSuchMethodException {
		Annotation[] annotations = annotationsFromMethod("singleAnnotation");
		assertThat(annotations).hasSize(1);
		assertThat(annotations[0].annotationType()).isAssignableFrom(ForAll.class);
	}

	@Example
	void twoAnnotationsAreFound() throws NoSuchMethodException {
		Annotation[] annotations = annotationsFromMethod("twoAnnotations");
		assertThat(annotations).hasSize(2);
		assertThat(annotations[0].annotationType()).isAssignableFrom(ForAll.class);
		assertThat(annotations[1].annotationType()).isAssignableFrom(StringLength.class);
	}

	@Example
	void repeatableAnnotationsAreFound() throws NoSuchMethodException {
		Annotation[] annotations = annotationsFromMethod("repeatableAnnotations");
		assertThat(annotations).hasSize(3);
		//assertThat(annotations[0].annotationType()).isAssignableFrom(ForAll.class);
		//assertThat(annotations[0].annotationType()).isAssignableFrom(StringLength.class);
	}

	private Annotation[] annotationsFromMethod(String singleAnnotation) throws NoSuchMethodException {
		Parameter param = parameterFromMethod(singleAnnotation);
		return JqwikAnnotationSupport.getAllAnnotations(param);
	}

	private Parameter parameterFromMethod(String methodName) throws NoSuchMethodException {
		return AClass.class.getDeclaredMethod(methodName, String.class).getParameters()[0];
	}

	static class AClass {
		void singleAnnotation(@ForAll String param) {}
		void twoAnnotations(@ForAll @StringLength(max = 10) String param) {}
		void repeatableAnnotations(@Chars({'a'}) @Chars({'b'}) String param) {}
	}

}
