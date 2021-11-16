package net.jqwik.engine.support;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

@Group
class JqwikAnnotationSupportTests {

	@Group
	class FindAllAnnotations {
		@Example
		void singlePresentAnnotationIsFound() throws NoSuchMethodException {
			Stream<Class<? extends Annotation>> types = annotationTypesFromMethod("singleAnnotation");
			assertThat(types).containsExactly(ForAll.class);
		}

		@Example
		void twoAnnotationsAreFound() throws NoSuchMethodException {
			Stream<Class<? extends Annotation>> types = annotationTypesFromMethod("twoAnnotations");
			assertThat(types).containsExactly(ForAll.class, StringLength.class);
		}

		@Example
		void repeatableAnnotationsAreFound() throws NoSuchMethodException {
			Stream<Class<? extends Annotation>> types = annotationTypesFromMethod("repeatableAnnotations");
			assertThat(types).containsExactly(CharsList.class);
		}

		@Example
		void mixedAnnotationsAreFound() throws NoSuchMethodException {
			Stream<Class<? extends Annotation>> types = annotationTypesFromMethod("mixedAnnotations");
			assertThat(types).containsExactly(ForAll.class, CharsList.class);
		}
	}

	@Group
	class AllMetaAnnotations {
		@Example
		void simpleMetaAnnotation() throws NoSuchMethodException {
			Stream<Class<? extends Annotation>> types = annotationTypesFromMethod("simpleMetaAnnotations");
			assertThat(types).containsExactly(ForAny.class, ForAll.class);
		}

		@Example
		void repeatedMetaAnnotations() throws NoSuchMethodException {
			Stream<Class<? extends Annotation>> types = annotationTypesFromMethod("repeatedMetaAnnotations");
			assertThat(types).containsExactly(
				AlphaChars.class,
				UpperChars.class, CharRange.class,
				LowerChars.class, CharRange.class
			);
		}

		@Example
		void allMetaAnnotations() throws NoSuchMethodException {
			Parameter param = firstParameterFromMethod("repeatedMetaAnnotations");
			AlphaChars alphaChars = param.getDeclaredAnnotation(AlphaChars.class);
			List<Annotation> annotations = JqwikAnnotationSupport.allMetaAnnotations(alphaChars);
			Stream<Class<? extends Annotation>> types = annotations.stream().map(Annotation::annotationType);
			assertThat(types).containsExactlyInAnyOrder(
				UpperChars.class, CharRange.class,
				LowerChars.class, CharRange.class
			);
		}

		@Example
		void nestedMetaAnnotations() throws NoSuchMethodException {
			Stream<Class<? extends Annotation>> types = annotationTypesFromMethod("nestedMetaAnnotations");
			assertThat(types).containsExactly(Nested.class, AlphaNumeric.class, CharsList.class, StringLength.class);
		}

		@Example
		void handleCircularMetaAnnotations() throws NoSuchMethodException {
			Stream<Class<? extends Annotation>> types = annotationTypesFromMethod("circularMetaAnnotations");
			assertThat(types).containsExactly(One.class, Two.class);
		}

	}

	@Group
	class FindRepeatableAnnotations {

		@Example
		void repeatableAnnotationsAtMethodAndClass() throws NoSuchMethodException {
			Method method = AClass.class.getDeclaredMethod("repeatableAnnotations");
			List<MyRepeatable> annotations = JqwikAnnotationSupport.findRepeatableAnnotationOnElementOrContainer(method, MyRepeatable.class);
			assertThat(annotations).hasSize(4);

			List<String> annotationValues = annotations.stream().map(MyRepeatable::value).collect(Collectors.toList());
			assertThat(annotationValues).containsExactly("atMethod1", "atMethod2", "atClass1", "atClass2");
		}
	}

	@Group
	class FindContainerAnnotations {

		@Example
		void notInheritedAnnotation() {
			List<MyNotInheritedAnnotation> annotations = JqwikAnnotationSupport.findContainerAnnotations(MyChild.class, MyNotInheritedAnnotation.class);

			List<String> annotationValues = annotations.stream().map(MyNotInheritedAnnotation::value).collect(Collectors.toList());
			assertThat(annotationValues).containsExactly("atChild");
		}

		@Example
		void inheritedAnnotation() {
			List<MyInheritedAnnotation> annotations = JqwikAnnotationSupport.findContainerAnnotations(MyChild.class, MyInheritedAnnotation.class);

			List<String> annotationValues = annotations.stream().map(MyInheritedAnnotation::value).collect(Collectors.toList());
			assertThat(annotationValues).containsExactly("atChild", "atParent", "atInterface");
		}

	}

	private List<Annotation> annotationsFromMethod(String methodName) throws NoSuchMethodException {
		Parameter param = firstParameterFromMethod(methodName);
		return JqwikAnnotationSupport.findAllAnnotations(param);
	}

	private Parameter firstParameterFromMethod(String methodName) throws NoSuchMethodException {
		return AClass.class.getDeclaredMethod(methodName, String.class).getParameters()[0];
	}

	private Stream<Class<? extends Annotation>> annotationTypesFromMethod(String methodName) throws NoSuchMethodException {
		List<Annotation> annotations = annotationsFromMethod(methodName);
		return annotations.stream().map(Annotation::annotationType);
	}

	@MyRepeatable("atClass1")
	@MyRepeatable("atClass2")
	private static class AClass {
		void singleAnnotation(@ForAll String param) {
		}

		void twoAnnotations(@ForAll @StringLength(max = 10) String param) {
		}

		void repeatableAnnotations(@Chars({ 'a' }) @Chars({ 'b' }) String param) {
		}

		void mixedAnnotations(@ForAll @Chars({ 'a' }) @Chars({ 'b' }) String param) {
		}

		void simpleMetaAnnotations(@ForAny String param) {
		}

		void repeatedMetaAnnotations(@AlphaChars String param) {
		}

		void nestedMetaAnnotations(@Nested String param) {
		}

		void circularMetaAnnotations(@One String param) {
		}

		@MyRepeatable("atMethod1")
		@MyRepeatable("atMethod2")
		void repeatableAnnotations() {
		}
	}

	@MyInheritedAnnotation("atParent")
	@MyNotInheritedAnnotation("atParent")
	private static class MyParent {
	}

	@MyInheritedAnnotation("atInterface")
	@MyNotInheritedAnnotation("atInterface")
	private interface MyInterface {
	}

	@MyInheritedAnnotation("atChild")
	@MyNotInheritedAnnotation("atChild")
	private static class MyChild extends MyParent implements MyInterface {
	}

	@Target({ ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@ForAll
	@interface ForAny {
	}

	@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@Chars({ 'a' })
	@Chars({ '1' })
	@StringLength(max = 100)
	@interface AlphaNumeric {
	}

	@Target({ ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@AlphaNumeric
	@interface Nested {
	}

	@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@Two
	@interface One {
	}

	@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@One
	@interface Two {
	}

	@Target({ ElementType.TYPE, ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	@interface MyNotInheritedAnnotation {
		String value();
	}

	@Target({ ElementType.TYPE, ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@interface MyInheritedAnnotation {
		String value();
	}

	@Target({ ElementType.TYPE, ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	@Repeatable(MyRepeatableList.class)
	@Inherited
	@interface MyRepeatable {
		String value();
	}

	@Target({ ElementType.TYPE, ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@interface MyRepeatableList {
		MyRepeatable[] value();
	}

}
