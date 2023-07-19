package net.jqwik.engine.support;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.types.*;

import static org.assertj.core.api.Assertions.*;

@Group()
@Label("GenericsSupport")
class GenericsSupportTests {

	@Example
	void createContextFromClass() {
		class JustAClass implements Function<String, Integer> {
			@Override
			public Integer apply(String s) {
				return null;
			}
		}

		GenericsClassContext context = GenericsSupport.contextFor(JustAClass.class);
		assertThat(context.contextType().getRawType()).isSameAs(JustAClass.class);
	}

	private interface PartialFunction<T> extends Function<T, String> {}

	@Example
	void createContextFromTypeUsage() throws NoSuchMethodException {
		TypeUsage integerToStringFunction =
			TypeUsage.of(PartialFunction.class, TypeUsage.of(Integer.class));

		GenericsClassContext context = GenericsSupport.contextFor(integerToStringFunction);
		assertThat(context.contextType()).isSameAs(integerToStringFunction);

		Method functionMethod = Function.class.getMethod("apply", Object.class);

		TypeResolution parameterResolution = context.resolveParameter(functionMethod.getParameters()[0]);
		assertThat(parameterResolution.type().getTypeName()).isEqualTo("java.lang.Integer");

		TypeResolution returnTypeResolution = context.resolveReturnType(functionMethod);
		assertThat(returnTypeResolution.type().getTypeName()).isEqualTo("java.lang.String");
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

		@Example
		@Label("annotation is kept when replacing nested type variable")
		void parameterWithNestedAnnotatedType() throws NoSuchMethodException {

			class AnotherClass<T> {
				public void method(Iterable<@Size(max = 5) List<T>> aT) {}
			}

			class AnotherClassWithAnnotatedString extends AnotherClass<String> {
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

		@Example
		void parameterWithRecursiveType() throws NoSuchMethodException {

			class AnotherClass {
				public <T extends Comparable<T>> void method(Comparable<T> aT) {}
			}

			GenericsClassContext context = GenericsSupport.contextFor(AnotherClass.class);
			Method methodWithComparable = AnotherClass.class.getMethod("method", Comparable.class);
			TypeResolution resolution = context.resolveParameter(methodWithComparable.getParameters()[0]);
			assertThat(resolution.type().getTypeName()).isEqualTo("java.lang.Comparable<T>");
			assertThat(resolution.annotatedType()).isNotNull();
		}

		@Example
		@Label("unresolved variable keeps annotation")
		void resolveUnresolvedGeneric() throws NoSuchMethodException {

			class AnotherClass<T> {
				public void method(Iterable<@Size(max = 5) List<T>> aT) {}
			}

			GenericsClassContext context = GenericsSupport.contextFor(AnotherClass.class);
			Method methodWithString = AnotherClass.class.getMethod("method", Iterable.class);
			TypeResolution resolution = context.resolveParameter(methodWithString.getParameters()[0]);

			assertThat(resolution.typeHasChanged()).isFalse();
			assertThat(resolution.type().getTypeName()).isEqualTo("java.lang.Iterable<java.util.List<T>>");
			assertThat(resolution.annotatedType()).isNotNull();
			AnnotatedType annotatedList = ((AnnotatedParameterizedType) resolution.annotatedType()).getAnnotatedActualTypeArguments()[0];
			assertThat(annotatedList.getAnnotation(Size.class)).isNotNull();
		}

		@Example
		@Label("Bug from issue#492")
		void bug492() throws NoSuchMethodException {

			abstract class Inner1<PARAMS> {
				public void methodWithParams(PARAMS params) {}
			}

			class GenericType<T> {}

			class Inner2<T> extends Inner1<GenericType<T>> {}

			class Inner3 extends Inner2<String> {}


			GenericsClassContext context = GenericsSupport.contextFor(Inner3.class);
			Method method = Inner1.class.getMethod("methodWithParams", Object.class);

			TypeResolution resolution = context.resolveParameter(method.getParameters()[0]);
			TypeUsage typeUsage = TypeUsageImpl.forResolution(resolution);

			assertThat(typeUsage.isOfType(GenericType.class)).isTrue();
			assertThat(typeUsage.getTypeArguments()).hasSize(1);
			assertThat(typeUsage.getTypeArgument(0).isOfType(String.class)).isTrue();

		}

	}

	@Group
	@Label("resolve return type")
	class ResolveReturnType {

		@Example
		@Label("generic return type")
		void genericReturnType() throws NoSuchMethodException {

			abstract class ClassReturnsT<T> {
				public abstract T method();
			}

			class ClassReturnsString extends ClassReturnsT<String> {
				@Override
				public String method() {
					return null;
				}
			}

			GenericsClassContext context = GenericsSupport.contextFor(ClassReturnsString.class);
			Method methodWithString = ClassReturnsString.class.getMethod("method");
			TypeResolution resolution = context.resolveReturnType(methodWithString);

			assertThat(resolution.type().getTypeName()).isEqualTo("java.lang.String");
			assertThat(resolution.annotatedType()).isNotNull();

			TypeUsage typeUsage = TypeUsage.forType(resolution.type());
			assertThat(typeUsage.getRawType()).isEqualTo(String.class);
			assertThat(typeUsage.getAnnotations()).isEmpty();
		}

		@Example
		@Label("generic function type")
		void genericFunctionType() throws NoSuchMethodException {
			GenericsClassContext context = GenericsSupport.contextFor(IntegerToStringFunction.class);
			Method function = IntegerToStringFunction.class.getMethod("apply", Object.class);

			TypeResolution resolution = context.resolveReturnType(function);
			assertThat(resolution.type().getTypeName()).isEqualTo("java.lang.String");

			TypeUsage typeUsage = TypeUsage.forType(resolution.type());
			assertThat(typeUsage.getRawType()).isEqualTo(String.class);
		}

		@Example
		@Label("unresolved return type")
		void unresolvedReturnType() throws NoSuchMethodException {

			abstract class ClassReturnsT<T, P> {
				public abstract T method(P p);
			}

			class ClassTakesString<T1> extends ClassReturnsT<T1, String> {
				@Override
				public T1 method(String p) {
					return null;
				}
			}

			GenericsClassContext context = GenericsSupport.contextFor(ClassTakesString.class);
			Method methodWithString = ClassReturnsT.class.getMethod("method", Object.class);
			TypeResolution resolution = context.resolveReturnType(methodWithString);

			assertThat(resolution.typeHasChanged()).isTrue();
			assertThat(resolution.type().getTypeName()).isEqualTo("T1");
			assertThat(resolution.annotatedType()).isNotNull();

			TypeUsage typeUsage = TypeUsage.forType(resolution.type());
			assertThat(typeUsage.isTypeVariable()).isTrue();
		}

		@Example
		@Label("non generic return type")
		void nonGenericReturnType() throws NoSuchMethodException {

			abstract class ClassReturnsT<T> {
				public abstract String method(T param);
			}

			class ClassReturnsString extends ClassReturnsT<Integer> {
				@Override
				public String method(Integer param) {
					return null;
				}
			}

			GenericsClassContext context = GenericsSupport.contextFor(ClassReturnsString.class);
			Method methodWithString = ClassReturnsT.class.getMethod("method", Object.class);
			TypeResolution resolution = context.resolveReturnType(methodWithString);

			assertThat(resolution.typeHasChanged()).isFalse();
			assertThat(resolution.type().getTypeName()).isEqualTo("java.lang.String");
			assertThat(resolution.annotatedType()).isNotNull();
		}

	}

	interface IntegerToStringFunction extends Function<Integer, String> {
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

	static class MyInterfaceString<S> implements MyInterface<String, S> {
	}

	static class MyInterfaceStringInteger extends MyInterfaceString<Integer> {
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
