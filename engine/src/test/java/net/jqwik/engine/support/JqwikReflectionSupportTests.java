package net.jqwik.engine.support;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

@Group
class JqwikReflectionSupportTests {

	@Group
	class NewInstanceWithDefaultConstructor {

		@Example
		boolean staticClass() {
			return JqwikReflectionSupport.newInstanceWithDefaultConstructor(Outer.class)
					   instanceof Outer;
		}

		@Example
		boolean innerClassWithoutConstructor() {
			return JqwikReflectionSupport.newInstanceWithDefaultConstructor(Outer.Inner.class)
					   instanceof Outer.Inner;
		}

		@Example
		boolean innerClassWithDefaultConstructor() {
			return JqwikReflectionSupport.newInstanceWithDefaultConstructor(Outer.InnerWithConstructor.class)
					   instanceof Outer.InnerWithConstructor;
		}

		@Example
		boolean staticClassWithDefaultConstructor() {
			return JqwikReflectionSupport.newInstanceWithDefaultConstructor(OuterWithConstructor.class)
					   instanceof OuterWithConstructor;
		}

	}

	@Group
	class NewInstanceInTestContext {

		@Example
		boolean staticInnerClass() {
			return JqwikReflectionSupport.newInstanceInTestContext(Outer.class, null)
					   instanceof Outer;
		}

		@Example
		boolean toplevelClass() {
			return JqwikReflectionSupport.newInstanceInTestContext(JqwikReflectionSupportTests.class, null)
					   instanceof JqwikReflectionSupportTests;
		}

		@Example
		boolean innerClass() {
			return JqwikReflectionSupport.newInstanceInTestContext(Outer.InnerWithConstructor.class, new Outer())
					   instanceof Outer.InnerWithConstructor;
		}

		@Example
		boolean innerClassInherited() {
			return JqwikReflectionSupport.newInstanceInTestContext(OuterBase.Inner.class, new OuterSub())
					   instanceof OuterBase.Inner;

		}

	}

	@Group
	class GetMethodParameters {

		@Example
		void simpleParameters() throws NoSuchMethodException {
			class ClassWithMethod {
				public void method(@ForAll String param1, List<Integer> param2) {
				}
			}

			Method method = ClassWithMethod.class.getMethod("method", String.class, List.class);
			List<MethodParameter> parameters = JqwikReflectionSupport.getMethodParameters(method, ClassWithMethod.class);

			MethodParameter param1 = parameters.get(0);
			assertThat(param1.getType()).isEqualTo(String.class);
			assertThat(param1.getType()).isEqualTo(param1.getAnnotatedType().getType());
			assertThat(param1.isParameterized()).isFalse();
			assertThat(param1.findAnnotation(ForAll.class)).isPresent();

			MethodParameter param2 = parameters.get(1);
			assertThat(param2.getType()).isEqualTo(param2.getAnnotatedType().getType());
			assertThat(param2.isParameterized()).isTrue();
			assertThat(((AnnotatedParameterizedType) param2.getAnnotatedType()).getAnnotatedActualTypeArguments()[0].getType())
				.isEqualTo(Integer.class);
			assertThat(param2.findAllAnnotations()).isEmpty();
		}

		@Example
		void typeVariableParameter() throws NoSuchMethodException {
			class ClassWithTypeVariableMethod {
				public <T> void method(T param1) {
				}
			}

			Method method = ClassWithTypeVariableMethod.class.getMethod("method", Object.class);
			List<MethodParameter> parameters = JqwikReflectionSupport.getMethodParameters(method, ClassWithTypeVariableMethod.class);

			MethodParameter param1 = parameters.get(0);
			assertThat(param1.getType()).isInstanceOf(TypeVariable.class);
			assertThat(param1.isParameterized()).isFalse();
		}

		@Example
		void typeVariableParameterResolvedInSubclass() throws NoSuchMethodException {
			class ClassWithTypeVariable<T> {
				public void method(@ForAll T param1) {
				}
			}

			class ClassWithString extends ClassWithTypeVariable<@AlphaChars String> {
			}

			Method method = ClassWithString.class.getMethod("method", Object.class);
			List<MethodParameter> parameters = JqwikReflectionSupport.getMethodParameters(method, ClassWithString.class);

			MethodParameter param1 = parameters.get(0);
			assertThat(param1.getType()).isEqualTo(String.class);
			assertThat(param1.findAnnotation(ForAll.class)).isPresent();
			assertThat(param1.isParameterized()).isFalse();
		}

		@Example
		void parameterWithGenericTypeResolvedInSubclass() throws NoSuchMethodException {
			class ClassWithGenericType<T> {
				public void method(@ForAll List<T> param1) {
				}
			}

			class ClassWithListOfString extends ClassWithGenericType<String> {
			}

			Method method = ClassWithListOfString.class.getMethod("method", List.class);
			List<MethodParameter> parameters = JqwikReflectionSupport.getMethodParameters(method, ClassWithListOfString.class);

			MethodParameter param1 = parameters.get(0);
			assertThat(param1.getType()).isInstanceOf(ParameterizedType.class);
			assertThat(((ParameterizedType) param1.getType()).getRawType()).isEqualTo(List.class);
			assertThat(((ParameterizedType) param1.getType()).getActualTypeArguments()).containsExactly(String.class);
			assertThat(param1.findAnnotation(ForAll.class)).isPresent();

			assertThat(param1.isParameterized()).isTrue();
		}
	}

	@Example
	void isFunctionalType() {
		assertThat(JqwikReflectionSupport.isFunctionalType(Function.class)).isTrue();
		assertThat(JqwikReflectionSupport.isFunctionalType(Iterable.class)).isTrue();
		assertThat(JqwikReflectionSupport.isFunctionalType(Closeable.class)).isTrue();

		// No method
		assertThat(JqwikReflectionSupport.isFunctionalType(Serializable.class)).isFalse();

		// Too many methods
		assertThat(JqwikReflectionSupport.isFunctionalType(DataOutput.class)).isFalse();

		// Not an interface
		assertThat(JqwikReflectionSupport.isFunctionalType(Writer.class)).isFalse();
	}

	@Example
	void getFunctionMethod() {
		Optional<Method> method = JqwikReflectionSupport.getFunctionMethod(Function.class);
		assertThat(method).isPresent();
		assertThat(method.get().getName()).isEqualTo("apply");

		assertThat(JqwikReflectionSupport.getFunctionMethod(DataOutput.class)).isNotPresent();
	}

	@Example
	void findMethodsPotentiallyOuter() {
		List<Method> methods = JqwikReflectionSupport.findMethodsPotentiallyOuter(
			AbstractClass.ConcreteSubclass.class,
			method -> method.getName().startsWith("method"),
			HierarchyTraversalMode.BOTTOM_UP
		);
		assertThat(methods).hasSize(2);

		List<Method> methodsInSub = JqwikReflectionSupport.findMethodsPotentiallyOuter(
			AbstractClass.ConcreteSubclass.class,
			method -> method.getName().startsWith("methodInConcreteSubclass"),
			HierarchyTraversalMode.BOTTOM_UP
		);
		assertThat(methodsInSub).hasSize(1);

		List<Method> methodsInBase = JqwikReflectionSupport.findMethodsPotentiallyOuter(
			AbstractClass.ConcreteSubclass.class,
			method -> method.getName().startsWith("methodInAbstractClass"),
			HierarchyTraversalMode.BOTTOM_UP
		);
		assertThat(methodsInBase).hasSize(1);
	}

	private static class Outer {

		Inner createInner() {
			return new Inner();
		}

		class Inner {

		}

		class InnerWithConstructor {
			private final String aString;

			public InnerWithConstructor() {
				this.aString = "hallo";
			}
		}
	}

	private static abstract class OuterBase {

		public OuterBase(int i) {

		}

		class Inner {

		}

	}

	private static class OuterSub extends OuterBase {

		public OuterSub() {
			super(0);
		}

	}

	private static class OuterWithConstructor {

	}
}

abstract class AbstractClass {

	void methodInAbstractClass() {}

	static class ConcreteSubclass extends AbstractClass {

		void methodInConcreteSubclass() {}
	}
}