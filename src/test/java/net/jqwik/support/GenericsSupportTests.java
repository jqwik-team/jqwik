package net.jqwik.support;

import net.jqwik.api.*;

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
	@Label("generic supertypes")
	class GenericSupertypes {

		@Example
		void withInterface() {
			class ClassWithInterface implements Iterator<Integer> {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public Integer next() {
					return null;
				}
			}

			GenericsClassContext context = GenericsSupport.contextFor(ClassWithInterface.class);

			Set<Type> genericSupertypes = context.genericSupertypes();
			assertThat(genericSupertypes).hasSize(1);

			Type supertype = genericSupertypes.iterator().next();
			assertThat(supertype).isInstanceOf(ParameterizedType.class);
			assertThat(supertype.getTypeName()).isEqualTo("java.util.Iterator<java.lang.Integer>");
		}

		@Example
		void withSuperclass() {
			class ClassWithSuperclass extends AbstractList<Integer> {
				@Override
				public Integer get(int index) {
					return null;
				}

				@Override
				public int size() {
					return 0;
				}
			}

			GenericsClassContext context = GenericsSupport.contextFor(ClassWithSuperclass.class);

			Set<Type> genericSupertypes = context.genericSupertypes();
			assertThat(genericSupertypes).hasSize(1);

			Type supertype = genericSupertypes.iterator().next();
			assertThat(supertype).isInstanceOf(ParameterizedType.class);
			assertThat(supertype.getTypeName()).isEqualTo("java.util.AbstractList<java.lang.Integer>");
		}

		@Example
		void withPlainSuperclass() {
			class ClassWithPlainSuperclass extends Exception {
			}

			GenericsClassContext context = GenericsSupport.contextFor(ClassWithPlainSuperclass.class);

			Set<Type> genericSupertypes = context.genericSupertypes();
			assertThat(genericSupertypes).hasSize(1);

			Type supertype = genericSupertypes.iterator().next();
			assertThat(supertype).isEqualTo(Exception.class);
		}

		@Example
		void withSeveralInterfaces() {
			class ClassWithSeveralInterfaces implements Iterator<Integer>, Comparable<Integer>, AutoCloseable {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public Integer next() {
					return null;
				}

				@Override
				public void close() {
				}

				@Override
				public int compareTo(Integer o) {
					return 0;
				}
			}

			GenericsClassContext context = GenericsSupport.contextFor(ClassWithSeveralInterfaces.class);
			assertThat(context.contextClass()).isEqualTo(ClassWithSeveralInterfaces.class);

			Set<Type> genericSupertypes = context.genericSupertypes();
			assertThat(genericSupertypes).hasSize(3);
		}

	}

	@Group
	@Label("parameter resolution")
	class ParameterResolution {

		@Example
		@Label("no generic parameter")
		void nonGenericParameter() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceWithStringAndInteger.class);
			Method methodWithString = MyInterfaceWithStringAndInteger.class.getMethod("methodWithStringParameter", String.class);
			Type resolvedType = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolvedType).isEqualTo(String.class);
		}

		@Example
		@Label("type variable from interface")
		void parameterWithTypeVariableFromInterface() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceWithStringAndInteger.class);
			Method methodWithString = MyInterfaceWithStringAndInteger.class.getMethod("methodWithTypeParameter", Object.class);
			Type resolvedType = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolvedType).isEqualTo(String.class);
		}

		@Example
		@Label("two type variables")
		void parameterWithTypeVariablesFromDifferentInterfaces() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceWithStringAndInteger.class);
			Method methodWithString = MyInterfaceWithStringAndInteger.class.getMethod("methodWithTwoTypeParameters", Object.class, Object.class);
			Type resolvedType1 = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolvedType1).isEqualTo(String.class);
			Type resolvedType2 = context.resolveParameter(methodWithString.getParameters()[1]);
			assertThat(resolvedType2).isEqualTo(Integer.class);
		}

		@Example
		@Label("type variable from superclass")
		void parameterWithTypeVariableFromSuperclass() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyClassWithString.class);
			Method methodWithString = MyClassWithString.class.getMethod("methodWithTypeParameter", Object.class);
			Type resolvedType = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolvedType).isEqualTo(String.class);
		}

		@Example
		@Label("type variable from method should not be resolved")
		void parameterWithTypeVariableFromMethod() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyClassWithString.class);
			Method methodWithString = MyClassWithString.class.getMethod("typedMethodWithParameter", Object.class);
			Parameter parameter = methodWithString.getParameters()[0];
			Type resolvedType = context.resolveParameter(parameter);
			assertThat(resolvedType).isInstanceOf(TypeVariable.class);
			assertThat(resolvedType).isSameAs(parameter.getParameterizedType());
		}

		// TODO: Many more tests
		// List<T> -> List<String>
		// List<Iterable<T>> -> List<Iterable<String>>
		// several resolved types variable
		// type variable from super super type
	}

	interface MyInterface<T, U> {
		default void methodWithStringParameter(String param) {}

		default void methodWithTypeParameter(T param) {}

		default void methodWithTwoTypeParameters(T param1, U param2) {}
	}

	static class MyInterfaceWithStringAndInteger implements MyInterface<String, Integer> {
	}

	static class MyClass<T> {
		public void methodWithTypeParameter(T param) {}

		public <T> void typedMethodWithParameter(T param) {}
	}

	static class MyClassWithString extends MyClass<String> {
	}
}
