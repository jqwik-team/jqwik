package net.jqwik.support;

import java.lang.annotation.*;
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
		Stream<Class> types = typesFromMethod("repeatableAnnotations");
		assertThat(types).containsExactly(CharsList.class);
	}

	@Example
	void mixedAnnotationsAreFound() throws NoSuchMethodException {
		Stream<Class> types = typesFromMethod("mixedAnnotations");
		assertThat(types).containsExactly(ForAll.class, CharsList.class);
	}

	@Example
	void simpleMetaAnnotation() throws NoSuchMethodException {
		Stream<Class> types = typesFromMethod("simpleMetaAnnotations");
		assertThat(types).containsExactly(ForAny.class, ForAll.class);
	}

	@Example
	void repeatedMetaAnnotations() throws NoSuchMethodException {
		Stream<Class> types = typesFromMethod("repeatedMetaAnnotations");
		assertThat(types).containsExactly(AlphaNumeric.class, CharsList.class, StringLength.class);
	}

	@Example
	void nestedMetaAnnotations() throws NoSuchMethodException {
		Stream<Class> types = typesFromMethod("nestedMetaAnnotations");
		assertThat(types).containsExactly(Nested.class, AlphaNumeric.class, CharsList.class, StringLength.class);
	}

	@Example
	void handleCircularMetaAnnotations() throws NoSuchMethodException {
		Stream<Class> types = typesFromMethod("circularMetaAnnotations");
		assertThat(types).containsExactly(One.class, Two.class);
	}

	static class AClass {
		void singleAnnotation(@ForAll String param) {}
		void twoAnnotations(@ForAll @StringLength(max = 10) String param) {}
		void repeatableAnnotations(@Chars({'a'}) @Chars({'b'}) String param) {}
		void mixedAnnotations(@ForAll @Chars({'a'}) @Chars({'b'}) String param) {}
		void simpleMetaAnnotations(@ForAny String param) {}
		void repeatedMetaAnnotations(@AlphaNumeric String param) {}
		void nestedMetaAnnotations(@Nested String param) {}
		void circularMetaAnnotations(@One String param) {}
	}

	@Target({ ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@ForAll
	@interface ForAny { }

	@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@Chars({'a'})
	@Chars({'1'})
	@StringLength(max = 100)
	@interface AlphaNumeric { }

	@Target({ ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@AlphaNumeric
	@interface Nested { }

	@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@Two
	@interface One { }

	@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@One
	@interface Two { }

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
