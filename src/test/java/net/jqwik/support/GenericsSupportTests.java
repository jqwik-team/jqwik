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

		GenericsContext context = GenericsSupport.contextFor(JustAClass.class);
		assertThat(context.contextClass()).isSameAs(JustAClass.class);
	}

	@Example
	void contextIsCached() {
		class AnotherClass {
		}

		GenericsContext context1 = GenericsSupport.contextFor(AnotherClass.class);
		GenericsContext context2 = GenericsSupport.contextFor(AnotherClass.class);
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

			GenericsContext context = GenericsSupport.contextFor(ClassWithInterface.class);

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

			GenericsContext context = GenericsSupport.contextFor(ClassWithSuperclass.class);

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

			GenericsContext context = GenericsSupport.contextFor(ClassWithPlainSuperclass.class);

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

			GenericsContext context = GenericsSupport.contextFor(ClassWithSeveralInterfaces.class);
			assertThat(context.contextClass()).isEqualTo(ClassWithSeveralInterfaces.class);

			Set<Type> genericSupertypes = context.genericSupertypes();
			assertThat(genericSupertypes).hasSize(3);
		}

	}

	@Group
	@Label("parameter resolution")
	class ParameterResolution {

		private GenericsContext context = GenericsSupport.contextFor(ClassWithString.class);

		@Example
		void nonGenericParameter() throws NoSuchMethodException {
			Method methodWithString = ClassWithString.class.getMethod("methodWithStringParameter", String.class);
			Type resolvedType = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolvedType).isEqualTo(String.class);
		}

		@Example
		void parameterWithTypeVariable() throws NoSuchMethodException {
			Method methodWithString = ClassWithString.class.getMethod("methodWithTypeParameter", Object.class);
			Type resolvedType = context.resolveParameter(methodWithString.getParameters()[0]);
			assertThat(resolvedType).isEqualTo(String.class);
		}
	}

	interface MyInterface<T> {
		default void methodWithStringParameter(String param) {}

		default void methodWithTypeParameter(T param) {}
	}

	class ClassWithString implements MyInterface<String> {
	}
}
