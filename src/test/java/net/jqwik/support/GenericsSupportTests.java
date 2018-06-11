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
			Method methodWithString = MyInterface.class.getMethod("methodWithStringParameter", String.class);
			Type resolvedType = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolvedType).isEqualTo(String.class);
		}

		@Example
		@Label("type variable from interface")
		void parameterWithTypeVariableFromInterface() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceWithStringAndInteger.class);
			Method methodWithString = MyInterface.class.getMethod("methodWithTypeParameter", Object.class);
			Type resolvedType = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolvedType).isEqualTo(String.class);
		}

		@Example
		@Label("two type variables")
		void parameterWithTypeVariablesFromDifferentInterfaces() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceWithStringAndInteger.class);
			Method method = MyInterface.class.getMethod("methodWithTwoTypeParameters", Object.class, Object.class);
			assertThat(context.resolveParameter(method.getParameters()[0])).isEqualTo(String.class);
			assertThat(context.resolveParameter(method.getParameters()[1])).isEqualTo(Integer.class);
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
		@Label("type variable resolved to generic type")
		void parameterWithTypeVariableResolvedToGenericType() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyClassWithStringList.class);
			Method methodWithString = MyClass.class.getMethod("methodWithTypeParameter", Object.class);
			ParameterizedType resolvedType = (ParameterizedType) context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolvedType.getRawType()).isEqualTo(List.class);
			assertThat(resolvedType.getActualTypeArguments()[0]).isEqualTo(String.class);
		}

		@Example
		@Label("type variable from method should not be resolved")
		void parameterWithTypeVariableFromMethod() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyClassWithString.class);
			Method method = MyClass.class.getMethod("typedMethodWithParameter", Object.class);
			Parameter parameter = method.getParameters()[0];
			Type resolvedType = context.resolveParameter(parameter);
			assertThat(resolvedType).isInstanceOf(TypeVariable.class);
			assertThat(resolvedType).isSameAs(parameter.getParameterizedType());
		}

		@Example
		@Label("type variables from further up in class hierarchy")
		void parameterWithTypeVariablesFromFurtherUpInClassHierarchy() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceFurtherDown.class);
			Method method = MyInterface.class.getMethod("methodWithTwoTypeParameters", Object.class, Object.class);
			assertThat(context.resolveParameter(method.getParameters()[0])).isEqualTo(String.class);
			assertThat(context.resolveParameter(method.getParameters()[1])).isEqualTo(Integer.class);
		}

		@Example
		@Label("type variables from further up in interface hierarchy")
		void parameterWithTypeVariablesFromFurtherUpInInterfaceHierarchy() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceFurtherDown.class);
			Method methodWithString = StringPooper.class.getMethod("poop", Object.class);
			assertThat(context.resolveParameter(methodWithString.getParameters()[0])).isEqualTo(String.class);
		}

		@Example
		@Label("list of type variable")
		void parameterWithListOfTypeVariable() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceWithStringAndInteger.class);
			Method methodWithListOfString = MyInterface.class.getMethod("methodWithListOfTypeParameter", List.class);
			ParameterizedType resolvedType = (ParameterizedType) context.resolveParameter(methodWithListOfString.getParameters()[0]);
			assertThat(resolvedType.getTypeName()).isEqualTo("java.util.List<java.lang.String>");
		}

		@Example
		@Label("nested type variable")
		void parameterWithNestedTypeVariable() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(MyInterfaceWithStringAndInteger.class);
			Method method = MyInterface.class.getMethod("methodWithNestedTypeParameter", List.class);
			ParameterizedType resolvedType = (ParameterizedType) context.resolveParameter(method.getParameters()[0]);
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
			assertThat(context.resolveParameter(methodWithStringAndInteger.getParameters()[0])).isEqualTo(String.class);
			assertThat(context.resolveParameter(methodWithStringAndInteger.getParameters()[1])).isEqualTo(Integer.class);

			Method methodWithListOfString = MyInterface.class.getMethod("methodWithListOfTypeParameter", List.class);
			ParameterizedType resolvedType = (ParameterizedType) context.resolveParameter(methodWithListOfString.getParameters()[0]);
			assertThat(resolvedType.getTypeName()).isEqualTo("java.util.List<java.lang.String>");
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
