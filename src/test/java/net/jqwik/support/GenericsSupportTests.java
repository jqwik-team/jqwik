package net.jqwik.support;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.lang.reflect.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@Group()
@Label("GenericsSupport")
class GenericsSupportTests {

	@Example
	void createContext() {
		class JustAClass{
		}

		GenericsClassContext context = GenericsSupport.contextFor(JustAClass.class);
		assertThat(context.contextClass()).isSameAs(JustAClass.class);
	}

	@Example
	void contextIsCached() {
		class AnotherClass {
		}

		GenericsClassContext context1 = GenericsSupport.contextFor(AnotherClass.class);
		GenericsClassContext context2 = GenericsSupport.contextFor(AnotherClass.class);
		assertThat(context1).isSameAs(context2);
	}

	@Group
	@Label("parameter resolution")
	class ParameterResolution {

		@Example
		@Label("no generic parameter")
		void nonGenericParameter() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceWithStringAndInteger.class);
			Method methodWithString = MyInterface.class.getMethod("methodWithStringParameter", String.class);
			TypeResolution resolution = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolution.typeHasChanged()).isFalse();
			assertThat(resolution.type()).isEqualTo(String.class);
		}

		@Example
		@Label("type variable from interface")
		void parameterWithTypeVariableFromInterface() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceWithStringAndInteger.class);
			Method methodWithString = MyInterface.class.getMethod("methodWithTypeParameter", Object.class);
			TypeResolution resolution = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolution.typeHasChanged()).isTrue();
			assertThat(resolution.type()).isEqualTo(String.class);
		}

		@Example
		@Label("two type variables")
		void parameterWithTypeVariablesFromDifferentInterfaces() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceWithStringAndInteger.class);
			Method method = MyInterface.class.getMethod("methodWithTwoTypeParameters", Object.class, Object.class);
			assertThat(context.resolveParameter(method.getParameters()[0]).type()).isEqualTo(String.class);
			assertThat(context.resolveParameter(method.getParameters()[1]).type()).isEqualTo(Integer.class);
		}

		@Example
		@Label("type variable from superclass")
		void parameterWithTypeVariableFromSuperclass() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyClassWithString.class);
			Method methodWithString = MyClassWithString.class.getMethod("methodWithTypeParameter", Object.class);
			TypeResolution resolution = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolution.typeHasChanged()).isTrue();
			assertThat(resolution.type()).isEqualTo(String.class);
		}

		@Example
		@Label("type variable resolved to generic type")
		void parameterWithTypeVariableResolvedToGenericType() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyClassWithStringList.class);
			Method methodWithString = MyClass.class.getMethod("methodWithTypeParameter", Object.class);
			TypeResolution resolution = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolution.typeHasChanged()).isTrue();

			ParameterizedType resolvedType = (ParameterizedType) resolution.type();
			assertThat(resolvedType.getRawType()).isEqualTo(List.class);
			assertThat(resolvedType.getActualTypeArguments()[0]).isEqualTo(String.class);
		}

		@Example
		@Label("type variable from method should not be resolved")
		void parameterWithTypeVariableFromMethod() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyClassWithString.class);
			Method method = MyClass.class.getMethod("typedMethodWithParameter", Object.class);
			Parameter parameter = method.getParameters()[0];
			TypeResolution resolution = context.resolveParameter(parameter);
			assertThat(resolution.typeHasChanged()).isFalse();

			Type resolvedType = resolution.type();
			assertThat(resolvedType).isInstanceOf(TypeVariable.class);
			assertThat(resolvedType).isSameAs(parameter.getParameterizedType());
		}

		@Example
		@Label("type variables from further up in class hierarchy")
		void parameterWithTypeVariablesFromFurtherUpInClassHierarchy() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceFurtherDown.class);
			Method method = MyInterface.class.getMethod("methodWithTwoTypeParameters", Object.class, Object.class);
			assertThat(context.resolveParameter(method.getParameters()[0]).type()).isEqualTo(String.class);
			assertThat(context.resolveParameter(method.getParameters()[1]).type()).isEqualTo(Integer.class);
		}

		@Example
		@Label("type variables from further up in interface hierarchy")
		void parameterWithTypeVariablesFromFurtherUpInInterfaceHierarchy() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceFurtherDown.class);
			Method methodWithString = StringPooper.class.getMethod("poop", Object.class);
			TypeResolution resolution = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolution.typeHasChanged()).isTrue();
			assertThat(resolution.type()).isEqualTo(String.class);
		}

		@Example
		@Label("list of type variable")
		void parameterWithListOfTypeVariable() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceWithStringAndInteger.class);
			Method methodWithListOfString = MyInterface.class.getMethod("methodWithListOfTypeParameter", List.class);
			TypeResolution resolution = context.resolveParameter(methodWithListOfString.getParameters()[0]);
			assertThat(resolution.typeHasChanged()).isTrue();

			ParameterizedType resolvedType = (ParameterizedType) resolution.type();
			assertThat(resolvedType.getTypeName()).isEqualTo("java.util.List<java.lang.String>");
		}

		@Example
		@Label("nested type variable")
		void parameterWithNestedTypeVariable() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceWithStringAndInteger.class);
			Method method = MyInterface.class.getMethod("methodWithNestedTypeParameter", List.class);
			TypeResolution resolution = context.resolveParameter(method.getParameters()[0]);
			assertThat(resolution.typeHasChanged()).isTrue();

			ParameterizedType resolvedType = (ParameterizedType) resolution.type();
			assertThat(resolvedType.getTypeName()).isEqualTo("java.util.List<java.lang.Iterable<java.lang.String>>");
		}

		@Example
		@Label("type variable mapped on each other")
		void parameterWithMappedTypeVariables() throws NoSuchMethodException {
			class MyInterfaceWithX<X> implements MyInterface<X, Integer> {
			}

			class AllResolved extends MyInterfaceWithX<String> {
			}

			GenericsClassContext context = GenericsSupport.contextFor(AllResolved.class);

			Method methodWithStringAndInteger = MyInterface.class.getMethod("methodWithTwoTypeParameters", Object.class, Object.class);
			assertThat(context.resolveParameter(methodWithStringAndInteger.getParameters()[0])
							  .type()).isEqualTo(String.class);
			assertThat(context.resolveParameter(methodWithStringAndInteger.getParameters()[1])
							  .type()).isEqualTo(Integer.class);

			Method methodWithListOfString = MyInterface.class.getMethod("methodWithListOfTypeParameter", List.class);
			TypeResolution resolution = context.resolveParameter(methodWithListOfString.getParameters()[0]);
			assertThat(resolution.typeHasChanged()).isTrue();

			ParameterizedType resolvedType = (ParameterizedType) resolution.type();
			assertThat(resolvedType.getTypeName()).isEqualTo("java.util.List<java.lang.String>");
		}

		//@Example
		@Label("annotation is kept when replacing type variable")
		void parameterWithAnnotatedType() throws NoSuchMethodException {

			class AClass<T> {
				public void method(T aT) {}
			}

			class AClassWithAnnotatedString extends AClass<@AlphaChars String> {
			}

			GenericsClassContext context = GenericsSupport.contextFor(AClassWithAnnotatedString.class);
			Method methodWithString = AClass.class.getMethod("method", Object.class);
			TypeResolution resolution = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolution.typeHasChanged()).isTrue();
			assertThat(resolution.type()).isEqualTo(String.class);
			assertThat(resolution.annotatedType()).isNotNull();
			assertThat(resolution.annotatedType().getAnnotations()).isNotEmpty();
		}

		@Example
		@Label("annotation is kept when replacing nested type variable")
		void parameterWithNestedAnnotatedType() throws NoSuchMethodException {

			class AnotherClass<T> {
				public void method(Iterable<@Size(max = 5) List<T>> aT) {}
			}

			class AnotherClassWithAnnotatedString extends AnotherClass<@AlphaChars String> {
			}

			GenericsClassContext context = GenericsSupport.contextFor(AnotherClassWithAnnotatedString.class);
			Method methodWithString = AnotherClass.class.getMethod("method", Iterable.class);
			TypeResolution resolution = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolution.typeHasChanged()).isTrue();
			assertThat(resolution.type().getTypeName()).isEqualTo("java.lang.Iterable<java.util.List<java.lang.String>>");
			assertThat(resolution.annotatedType()).isNotNull();
			AnnotatedType annotatedList = ((AnnotatedParameterizedType) resolution.annotatedType()).getAnnotatedActualTypeArguments()[0];
			assertThat(annotatedList.getAnnotation(Size.class)).isNotNull();
		}

	}

	interface MyInterface<T, U> {
		default void methodWithStringParameter(String param) {}

		default void methodWithTypeParameter(T param) {}

		default void methodWithListOfTypeParameter(List<T> param) {}

		default void methodWithNestedTypeParameter(List<Iterable<T>> param) {}

		default void methodWithTwoTypeParameters(T param1, U param2) {}
	}

	static class MyInterfaceWithStringAndInteger implements MyInterface<String, Integer> {
	}

	interface Pooper<T> {
		default void poop(T aT) {}
	}

	interface StringPooper extends Pooper<String> {
	}

	static class MyInterfaceFurtherDown extends MyInterfaceWithStringAndInteger implements StringPooper {
	}

	static class MyClass<T> {
		public void methodWithTypeParameter(T param) {}

		public <T> void typedMethodWithParameter(T param) {}
	}

	static class MyClassWithString extends MyClass<String> {
	}

	static class MyClassWithStringList extends MyClass<List<String>> {
	}
}
