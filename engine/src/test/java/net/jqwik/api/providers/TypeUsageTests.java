package net.jqwik.api.providers;

import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.stateful.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.support.*;
import net.jqwik.engine.support.types.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.providers.TypeUsage.of;

@Label("TypeUsage")
class TypeUsageTests {

	interface Recursive<E extends Recursive<E>> {}

	interface OtherRecursive<E extends OtherRecursive<E>> {}

	@Example
	void isAssignable() {
		assertThat(TypeUsage.of(CharSequence.class).isAssignableFrom(String.class)).isTrue();
		assertThat(TypeUsage.of(BigInteger.class).isAssignableFrom(BigInteger.class)).isTrue();
		assertThat(TypeUsage.of(String.class).isAssignableFrom(CharSequence.class)).isFalse();
	}

	@Example
	void isVoid() {
		assertThat(TypeUsage.of(Void.class).isVoid()).isTrue();
		assertThat(TypeUsage.of(void.class).isVoid()).isTrue();
		assertThat(TypeUsage.of(Object.class).isVoid()).isFalse();
	}

	@Example
	void nullability() {
		TypeUsage stringType = of(String.class);
		assertThat(stringType.isNullable()).isFalse();
		TypeUsage nullableStringType = of(String.class).asNullable();
		assertThat(nullableStringType.isNullable()).isTrue();
		assertThat(nullableStringType.asNotNullable().isNullable()).isFalse();

		assertThat(nullableStringType.toString()).isEqualTo("String?");

		assertThat(stringType).isNotEqualTo(nullableStringType);
		assertThat(stringType).isEqualTo(nullableStringType.asNotNullable());
	}

	@Example
	void metaInfo() {
		TypeUsage stringType = of(String.class);
		assertThat(stringType.getMetaInfo("key1")).isEmpty();

		TypeUsage withMetaInfo = stringType
			.withMetaInfo("key1", 1)
			.withMetaInfo("key2", 2)
			.withMetaInfo("key1", 11);

		assertThat(stringType).isNotSameAs(withMetaInfo);
		assertThat(withMetaInfo.getMetaInfo("key1").get()).isEqualTo(11);
		assertThat(withMetaInfo.getMetaInfo("key2").get()).isEqualTo(2);
	}

	@Example
	void withAddedAnnotations() throws NoSuchMethodException {

		class LocalClass {
			@SuppressWarnings("WeakerAccess")
			public void withAnnotation(@StringLength(5) String length) {}
		}

		Method method = LocalClass.class.getMethod("withAnnotation", String.class);
		MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
		StringLength stringLength = parameter.getAnnotation(StringLength.class);

		TypeUsage stringType = of(String.class);
		assertThat(stringType.getAnnotations()).isEmpty();

		TypeUsage stringWithAnnotation = stringType.withAnnotation(stringLength);
		assertThat(stringWithAnnotation.getAnnotations()).hasSize(1);
		Optional<StringLength> annotation = stringWithAnnotation.findAnnotation(StringLength.class);
		assertThat(annotation).isPresent();
		annotation.ifPresent(length -> assertThat(length.value()).isEqualTo(5));
	}

	@Group
	@Label("of()")
	class Of {
		@Example
		@Label("simple type")
		void simpleType() {
			TypeUsage stringType = TypeUsage.of(String.class);
			assertThat(stringType.getRawType()).isEqualTo(String.class);
			assertThat(stringType.getType()).isEqualTo(String.class);
			assertThat(stringType.isOfType(String.class)).isTrue();
			assertThat(stringType.isGeneric()).isFalse();
			assertThat(stringType.isArray()).isFalse();
			assertThat(stringType.isEnum()).isFalse();

			assertThat(stringType.toString()).isEqualTo("String");

			assertThat(stringType.equals(TypeUsage.of(String.class))).isTrue();
			assertThat(stringType.equals(TypeUsage.of(Number.class))).isFalse();

			assertThat(stringType.getSuperclass()).isPresent();
			assertThat(stringType.getSuperclass().get()).isEqualTo(TypeUsage.of(Object.class));
			assertThat(stringType.getInterfaces()).contains(
				TypeUsage.of(Serializable.class),
				TypeUsage.of(Comparable.class, TypeUsage.of(String.class)),
				TypeUsage.of(CharSequence.class)
			);
		}

		@Example
		@Label("parameterized type")
		void parameterizedType() {
			TypeUsage tupleType = TypeUsage.of(Tuple2.class, of(String.class), of(Integer.class));
			assertThat(tupleType.getRawType()).isEqualTo(Tuple2.class);
			assertThat(tupleType.getType()).isEqualTo(Tuple2.class);
			assertThat(tupleType.isOfType(Tuple2.class)).isTrue();
			assertThat(tupleType.isGeneric()).isTrue();
			assertThat(tupleType.isArray()).isFalse();
			assertThat(tupleType.isEnum()).isFalse();

			assertThat(tupleType.toString()).isEqualTo("Tuple2<String, Integer>");
		}

		@Example
		@Label("parameterized types equality")
		void parameterizedTypeEquality() {
			TypeUsage tupleType = TypeUsage.of(Tuple2.class, of(String.class), of(Integer.class));

			TypeUsage equalType = TypeUsage.of(Tuple2.class, of(String.class), of(Integer.class));
			assertThat(tupleType.equals(equalType)).isTrue();
			assertThat(equalType.equals(tupleType)).isTrue();

			TypeUsage nonEqualType = TypeUsage.of(Tuple2.class, of(String.class), of(Number.class));
			assertThat(tupleType.equals(nonEqualType)).isFalse();
		}

		@Example
		@Label("array")
		void arrayTypes() {
			TypeUsage arrayType = TypeUsage.of(String[].class);
			assertThat(arrayType.getRawType()).isEqualTo(String[].class);
			assertThat(arrayType.isOfType(String[].class)).isTrue();
			assertThat(arrayType.isGeneric()).isFalse();
			assertThat(arrayType.isArray()).isTrue();
			assertThat(arrayType.isEnum()).isFalse();

			assertThat(arrayType.getComponentType().get().isOfType(String.class));
			assertThat(arrayType.toString()).isEqualTo("String[]");
		}

		@Example
		@Label("fails if parameterized type is not generic")
		void specifyingTypeParametersForNonGenericTypeFails() {
			assertThatThrownBy(() -> TypeUsage.of(String.class, of(String.class))).isInstanceOf(JqwikException.class);
		}

	}

	@Example
	@Label("forType() with parameterized type")
	void forType() throws NoSuchMethodException {
		class LocalClass {
			@SuppressWarnings("WeakerAccess")
			public Tuple2<String, Integer> withReturn() {return null;}
		}

		Type type = LocalClass.class.getMethod("withReturn").getAnnotatedReturnType().getType();
		TypeUsage tupleType = TypeUsage.forType(type);
		assertThat(tupleType.getRawType()).isEqualTo(Tuple2.class);
		assertThat(tupleType.getType()).isEqualTo(type);
		assertThat(tupleType.isOfType(Tuple2.class)).isTrue();
		assertThat(tupleType.isGeneric()).isTrue();
		assertThat(tupleType.isArray()).isFalse();
		assertThat(tupleType.isEnum()).isFalse();

		assertThat(tupleType.toString()).isEqualTo("Tuple2<String, Integer>");
	}

	@Example
	@Label("wildcard()")
	void wildcard() {
		TypeUsage wildcardWithUpperBound = TypeUsage.wildcard(TypeUsage.of(Collection.class));

		assertThat(wildcardWithUpperBound.getRawType()).isEqualTo(Object.class);
		assertThat(wildcardWithUpperBound.getType()).isEqualTo(Object.class);
		assertThat(wildcardWithUpperBound.isWildcard()).isTrue();
		assertThat(wildcardWithUpperBound.isTypeVariableOrWildcard()).isTrue();
		assertThat(wildcardWithUpperBound.isGeneric()).isFalse();
		assertThat(wildcardWithUpperBound.isArray()).isFalse();
		assertThat(wildcardWithUpperBound.isEnum()).isFalse();

		assertThat(wildcardWithUpperBound.toString()).isEqualTo("? extends Collection");
	}

	@Group
	@Label("TypeUsageImpl.forParameter()")
	class ForParameter {
		@Example
		void simpleParameter() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withParameter(String aString) {}
			}

			Method method = LocalClass.class.getMethod("withParameter", String.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage stringType = TypeUsageImpl.forParameter(parameter);
			assertThat(stringType.getRawType()).isEqualTo(String.class);
			assertThat(stringType.getType()).isEqualTo(parameter.getType());
			assertThat(stringType.isOfType(String.class)).isTrue();
			assertThat(stringType.isGeneric()).isFalse();
			assertThat(stringType.isArray()).isFalse();
			assertThat(stringType.isEnum()).isFalse();

			assertThat(stringType.isNullable()).isFalse();

			assertThat(stringType.toString()).isEqualTo("String");
		}

		@Example
		void twoGenericParameters() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withParameter(Tuple2<String, Integer> tuple) {}
			}

			Method method = LocalClass.class.getMethod("withParameter", Tuple2.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage tupleType = TypeUsageImpl.forParameter(parameter);
			assertThat(tupleType.getRawType()).isEqualTo(Tuple2.class);
			assertThat(tupleType.getType()).isEqualTo(parameter.getType());
			assertThat(tupleType.isOfType(Tuple2.class)).isTrue();
			assertThat(tupleType.isGeneric()).isTrue();
			assertThat(tupleType.isArray()).isFalse();
			assertThat(tupleType.isEnum()).isFalse();

			assertThat(tupleType.toString()).isEqualTo("Tuple2<String, Integer>");
		}

		@Example
		void threeGenericParametersAndAnnotation() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withParameters(@ForAll Tuple3<BigInteger, BigInteger, BigInteger> tuple) {}
			}

			Method method = LocalClass.class.getMethod("withParameters", Tuple3.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage tupleType = TypeUsageImpl.forParameter(parameter);
			assertThat(tupleType.isOfType(Tuple3.class)).isTrue();

			// TODO: annotations are differently toStringed in JDKs >= 11
			assertThat(tupleType.toString()).contains(
				"@net.jqwik.api.ForAll",
				"Tuple3<BigInteger, BigInteger, BigInteger>"
			);
			// JDK8:
			// assertThat(tupleType.toString())
			// 	.isEqualTo("@net.jqwik.api.ForAll(value=) Tuple3<BigInteger, BigInteger, BigInteger>");
			// JDK11:
			// assertThat(tupleType.toString())
			// 	.isEqualTo("@net.jqwik.api.ForAll(value=\"\") Tuple3<BigInteger, BigInteger, BigInteger>");
		}

		@Example
		void annotations() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withList(@Size(max = 2) List list) {}

				public void withNonEqualList(@Size(max = 3) List list) {}
			}

			Method method = LocalClass.class.getMethod("withList", List.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage parameterType = TypeUsageImpl.forParameter(parameter);
			assertThat(parameterType.getRawType()).isEqualTo(List.class);
			assertThat(parameterType.getAnnotations().get(0)).isInstanceOf(Size.class);

			assertThat(parameterType.findAnnotation(Size.class)).isPresent();
			assertThat(parameterType.findAnnotation(WithNull.class)).isNotPresent();

			assertThat(parameterType.isAnnotated(Size.class)).isTrue();
			assertThat(parameterType.isAnnotated(WithNull.class)).isFalse();

			// JDK 8
			// assertThat(parameterType.toString()).isEqualTo("@net.jqwik.api.constraints.Size(value=0, max=2, min=0) List");
			assertThat(parameterType.toString()).contains(
				"@net.jqwik.api.constraints.Size",
				"value=0",
				"max=2",
				"min=0",
				"List"
			);

			TypeUsage equalParameterType = TypeUsageImpl.forParameter(parameter);
			assertThat(parameterType.equals(equalParameterType)).isTrue();

			Method methodWithNonEqualList = LocalClass.class.getMethod("withNonEqualList", List.class);
			MethodParameter nonEqualParameter = JqwikReflectionSupport.getMethodParameters(methodWithNonEqualList, LocalClass.class).get(0);
			TypeUsage nonEqualParameterType = TypeUsageImpl.forParameter(nonEqualParameter);
			assertThat(parameterType.equals(nonEqualParameterType)).isFalse();

		}

		// This behaviour was disabled in version 1.6.2
		@Example
		void annotationsOfArrayAreNotPropagatedToComponentType() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void stringArray(@Size(2) @StringLength(3) String[] strings) {}
			}

			Method method = LocalClass.class.getMethod("stringArray", String[].class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage arrayType = TypeUsageImpl.forParameter(parameter);
			assertThat(arrayType.isArray()).isTrue();
			assertThat(arrayType.getRawType()).isEqualTo(String[].class);
			assertThat(arrayType.getAnnotations().get(0)).isInstanceOf(Size.class);
			assertThat(arrayType.getAnnotations().get(1)).isInstanceOf(StringLength.class);

			TypeUsage componentType = arrayType.getComponentType().get();
			assertThat(componentType.getRawType()).isEqualTo(String.class);
			assertThat(componentType.getAnnotations()).isEmpty();
		}

		@Example
		void wildcard() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withWildcard(Tuple2<? extends CharSequence, ? super String> tuple) {}
			}

			Method method = LocalClass.class.getMethod("withWildcard", Tuple2.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage wildcardType = TypeUsageImpl.forParameter(parameter);

			TypeUsage first = wildcardType.getTypeArguments().get(0);
			assertThat(first.isWildcard()).isTrue();
			assertThat(first.isTypeVariableOrWildcard()).isTrue();
			assertThat(first.isTypeVariable()).isFalse();
			assertThat(first.getLowerBounds()).isEmpty();
			assertThat(first.getUpperBounds()).containsExactly(TypeUsage.of(CharSequence.class));

			TypeUsage second = wildcardType.getTypeArguments().get(1);
			assertThat(second.isWildcard()).isTrue();
			assertThat(second.isTypeVariableOrWildcard()).isTrue();
			assertThat(second.isTypeVariable()).isFalse();
			assertThat(second.getLowerBounds()).isNotEmpty();
			assertThat(second.getUpperBounds()).containsExactly(TypeUsage.of(Object.class));

			assertThat(wildcardType.toString()).isEqualTo("Tuple2<? extends CharSequence, ? super String>");
		}

		@Example
		void wildcard_equality() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withWildcard(Tuple2<? extends CharSequence, ? super String> tuple) {}

				public void withNonEqualWildcard(Tuple2<? extends CharSequence, ? super Number> tuple) {}

				public <T extends CharSequence> void withTypeVariable(Tuple2<T, ? super String> tuple) {}
			}

			Method method = LocalClass.class.getMethod("withWildcard", Tuple2.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage wildcardType = TypeUsageImpl.forParameter(parameter);

			TypeUsage equalWildcardType = TypeUsageImpl.forParameter(parameter);
			assertThat(wildcardType.equals(equalWildcardType)).isTrue();

			Method methodWithNonEqualWildcard = LocalClass.class.getMethod("withNonEqualWildcard", Tuple2.class);
			MethodParameter nonEqualParameter =
				JqwikReflectionSupport.getMethodParameters(methodWithNonEqualWildcard, LocalClass.class).get(0);
			TypeUsage nonEqualWildcardType = TypeUsageImpl.forParameter(nonEqualParameter);
			assertThat(wildcardType.equals(nonEqualWildcardType)).isFalse();

			Method methodWithTypeVariable = LocalClass.class.getMethod("withTypeVariable", Tuple2.class);
			MethodParameter typeVariableParameter =
				JqwikReflectionSupport.getMethodParameters(methodWithTypeVariable, LocalClass.class).get(0);
			TypeUsage typeVariableType = TypeUsageImpl.forParameter(typeVariableParameter);
			assertThat(wildcardType.equals(typeVariableType)).isFalse();
		}

		@Example
		void wildcardWithAnnotatedUpperBound() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void annotatedUpperBound(List<? extends @StringLength(1) String> list) {}
			}

			Method method = LocalClass.class.getMethod("annotatedUpperBound", List.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage wildcardType = TypeUsageImpl.forParameter(parameter);

			TypeUsage first = wildcardType.getTypeArguments().get(0);
			assertThat(first.isWildcard()).isTrue();
			assertThat(first.getUpperBounds().get(0).isOfType(String.class)).isTrue();

			assertThat(wildcardType.toString())
				.isEqualTo("List<? extends @net.jqwik.api.constraints.StringLength(value=1, max=0, min=0) String>");

			TypeUsage equalWildcardType = TypeUsageImpl.forParameter(parameter);
			assertThat(wildcardType.equals(equalWildcardType)).isTrue();
		}

		@Example
		void wildcardWithSingleUpperBoundAppendsAnnotationsFromUpperBound() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void annotatedUpperBound(List<@From("oops") ? extends @StringLength(1) String> list) {}
			}

			Method method = LocalClass.class.getMethod("annotatedUpperBound", List.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage wildcardType = TypeUsageImpl.forParameter(parameter).getTypeArgument(0);

			assertThat(wildcardType.getAnnotations()).hasSize(2);
			Stream<String> annotationNames = wildcardType.getAnnotations().stream().map(a -> a.annotationType().getSimpleName());
			assertThat(annotationNames).containsExactlyInAnyOrder("From", "StringLength");
		}

		@Example
		void typeVariable() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public <T extends CharSequence, U extends Serializable & Cloneable> void withTypeVariable(
					Tuple2<T, U> tuple
				) {}
			}

			Method method = LocalClass.class.getMethod("withTypeVariable", Tuple2.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage typeVariableType = TypeUsageImpl.forParameter(parameter);

			TypeUsage first = typeVariableType.getTypeArguments().get(0);
			assertThat(first.isWildcard()).isFalse();
			assertThat(first.isTypeVariableOrWildcard()).isTrue();
			assertThat(first.isTypeVariable()).isTrue();
			assertThat(first.getLowerBounds()).isEmpty();
			assertThat(first.getUpperBounds()).containsExactly(TypeUsage.of(CharSequence.class));

			TypeUsage second = typeVariableType.getTypeArguments().get(1);
			assertThat(second.isWildcard()).isFalse();
			assertThat(second.isTypeVariableOrWildcard()).isTrue();
			assertThat(second.isTypeVariable()).isTrue();
			assertThat(second.getLowerBounds()).isEmpty();
			assertThat(second.getUpperBounds()).containsExactly(
				TypeUsage.of(Serializable.class),
				TypeUsage.of(Cloneable.class)
			);

			assertThat(typeVariableType.toString()).isEqualTo("Tuple2<T extends CharSequence, U extends Serializable & Cloneable>");
		}

		@Example
		void typeVariable_equality() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public <T extends CharSequence, U extends Serializable & Cloneable> void withTypeVariable(
					Tuple2<T, U> tuple
				) {}

				@SuppressWarnings("WeakerAccess")
				public <S extends CharSequence, U extends Serializable & Cloneable> void differentByName(
					Tuple2<S, U> tuple
				) {}

				@SuppressWarnings("WeakerAccess")
				public <T, U extends Serializable & Cloneable> void differentByLowerBounds(
					Tuple2<T, U> tuple
				) {}

				@SuppressWarnings("WeakerAccess")
				public <U extends Serializable & Cloneable> void sameButWildcard(
					Tuple2<? extends CharSequence, U> tuple
				) {}
			}

			Method method = LocalClass.class.getMethod("withTypeVariable", Tuple2.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage typeVariableType = TypeUsageImpl.forParameter(parameter);

			TypeUsage equalTypeVariableType = TypeUsageImpl.forParameter(parameter);
			assertThat(typeVariableType.equals(equalTypeVariableType)).isTrue();

			Method differentByNameMethod = LocalClass.class.getMethod("differentByName", Tuple2.class);
			MethodParameter differentByNameParameter = JqwikReflectionSupport
				.getMethodParameters(differentByNameMethod, LocalClass.class).get(0);
			TypeUsage differentByNameType = TypeUsageImpl.forParameter(differentByNameParameter);
			assertThat(typeVariableType.equals(differentByNameType)).isFalse();

			Method differentByLowerBoundsMethod = LocalClass.class.getMethod("differentByLowerBounds", Tuple2.class);
			MethodParameter differentByLowerBoundsParameter = JqwikReflectionSupport
				.getMethodParameters(differentByLowerBoundsMethod, LocalClass.class)
				.get(0);
			TypeUsage differentByLowerBoundsType = TypeUsageImpl.forParameter(differentByLowerBoundsParameter);
			assertThat(typeVariableType.equals(differentByLowerBoundsType)).isFalse();

			Method sameButWildcard = LocalClass.class.getMethod("sameButWildcard", Tuple2.class);
			MethodParameter sameButWildcardParameter = JqwikReflectionSupport
				.getMethodParameters(sameButWildcard, LocalClass.class)
				.get(0);
			TypeUsage sameButWildcardType = TypeUsageImpl.forParameter(sameButWildcardParameter);
			assertThat(typeVariableType.equals(sameButWildcardType)).isFalse();
		}

		@Example
		void typeVariableRecursive() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public <T extends Comparable<T>> void recursiveTypeVariable(T element) {}

			}

			Method method = LocalClass.class.getMethod("recursiveTypeVariable", Comparable.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage typeVariableType = TypeUsageImpl.forParameter(parameter);

			assertThat(typeVariableType.equals(typeVariableType)).isTrue();

			assertThat(typeVariableType.isTypeVariableOrWildcard()).isTrue();
			assertThat(typeVariableType.isTypeVariable()).isTrue();
			assertThat(typeVariableType.getLowerBounds()).isEmpty();
			assertThat(typeVariableType.getUpperBounds()).hasSize(1);
			assertThat(typeVariableType.getUpperBounds().get(0).getRawType()).isEqualTo(Comparable.class);

			assertThat(typeVariableType.toString()).isEqualTo("T extends Comparable<T>");
		}

		@Example
		void typeVariableWithSingleUpperBoundTakesTypeArgumentsFromUpperBound() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public <T extends Map<String, Integer>> void typeVariableWithSingleUpperBound(T element) {}

			}

			Method method = LocalClass.class.getMethod("typeVariableWithSingleUpperBound", Map.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage typeVariableType = TypeUsageImpl.forParameter(parameter);

			assertThat(typeVariableType.getUpperBounds().get(0).getRawType()).isEqualTo(Map.class);
			assertThat(typeVariableType.toString()).isEqualTo("T extends Map<String, Integer>");

			assertThat(typeVariableType.isAssignableFrom(Map.class)).isTrue();
			List<TypeUsage> typeArguments = typeVariableType.getTypeArguments();
			assertThat(typeArguments).hasSize(2);
			assertThat(typeArguments.get(0)).isEqualTo(TypeUsage.of(String.class));
			assertThat(typeArguments.get(1)).isEqualTo(TypeUsage.of(Integer.class));
		}

		@Example
		void typeVariableInGenericArray() throws NoSuchMethodException {
			class LocalClass {
				public <T extends Comparable<T>> void withArrayOfTypeVariable(T[] array) {}
			}

			Method method = LocalClass.class.getMethod("withArrayOfTypeVariable", Comparable[].class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage arrayType = TypeUsageImpl.forParameter(parameter);
			assertThat(arrayType.isArray()).isTrue();
			assertThat(arrayType.getComponentType()).isNotEmpty();

			TypeUsage typeVariableType = arrayType.getComponentType().get();
			assertThat(typeVariableType.isWildcard()).isFalse();
			assertThat(typeVariableType.isTypeVariableOrWildcard()).isTrue();
			assertThat(typeVariableType.isTypeVariable()).isTrue();

			assertThat(typeVariableType.getLowerBounds()).isEmpty();
			assertThat(typeVariableType.getUpperBounds()).containsExactly(
				TypeUsage.of(Comparable.class, typeVariableType)
			);

			assertThat(typeVariableType.toString()).isEqualTo("T extends Comparable<T>");
		}

		@Example
		void typeVariableWithAnnotationInUpperBound() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public <T extends List<@StringLength(10) String>> void annotationInUpperBound(T element) {}
			}

			Method method = LocalClass.class.getMethod("annotationInUpperBound", List.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage typeVariableType = TypeUsageImpl.forParameter(parameter);

			assertThat(typeVariableType.getUpperBounds().get(0).getRawType()).isEqualTo(List.class);
			assertThat(typeVariableType.toString()).isEqualTo(
				"T extends List<@net.jqwik.api.constraints.StringLength(value=10, max=0, min=0) String>"
			);

			assertThat(typeVariableType.isAssignableFrom(List.class)).isTrue();
			List<TypeUsage> upperBounds = typeVariableType.getUpperBounds();
			assertThat(upperBounds).hasSize(1);
			assertThat(upperBounds.get(0).isOfType(List.class)).isTrue();
			List<TypeUsage> typeArguments = upperBounds.get(0).getTypeArguments();
			assertThat(typeArguments.get(0).getAnnotations()).hasSize(1);
			assertThat(typeArguments.get(0).getAnnotations().get(0).annotationType()).isEqualTo(StringLength.class);
		}

		@Example
		void genericTypeWithAnnotatedParameters() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withList(
					@Size(max = 5) List<
										   @StringLength(max = 2)
										   @CharRange(from = 'a', to = 'z') String
										   > list
				) {}
			}

			Method method = LocalClass.class.getMethod("withList", List.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage listType = TypeUsageImpl.forParameter(parameter);
			assertThat(listType.getAnnotations().get(0)).isInstanceOf(Size.class);
			TypeUsage stringType = listType.getTypeArguments().get(0);
			assertThat(stringType.isOfType(String.class)).isTrue();
			assertThat(stringType.getAnnotations().get(0)).isInstanceOf(StringLength.class);
			assertThat(stringType.getAnnotations().get(1)).isInstanceOf(CharRange.class);

			// This string changes from Java 8 to 9 :-(
			// TODO: Remove as soon as min Java version is >= 9
			String normalizedToString = stringType.toString()
												  .replaceAll("from=a", "from='a'")
												  .replaceAll("to=z", "to='z'");
			assertThat(normalizedToString).isEqualTo("@net.jqwik.api.constraints.StringLength(value=0, max=2, min=0) "
														 + "@net.jqwik.api.constraints.CharRange(from='a', to='z') String");
		}

		@Example
		void optionalArrayWithAnnotations() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withOptionalArray(
					Optional<@Size(max = 5) @StringLength(max = 2) String[]> optional
				) {}
			}

			Method method = LocalClass.class.getMethod("withOptionalArray", Optional.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage optionalType = TypeUsageImpl.forParameter(parameter);
			TypeUsage arrayType = optionalType.getTypeArguments().get(0);
			assertThat(arrayType.getAnnotations().get(0)).isInstanceOf(Size.class);
			assertThat(arrayType.getAnnotations().get(1)).isInstanceOf(StringLength.class);
		}

		@Example
		void recursiveTypes() throws NoSuchMethodException {

			class RecursiveOne<X> implements Recursive<RecursiveOne<X>> {}

			class RecursiveTwo<Y> implements Recursive<RecursiveTwo<Y>> {}

			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withRecursiveTypes(
					RecursiveOne one, RecursiveTwo two
				) {}
			}

			Method method = LocalClass.class.getMethod("withRecursiveTypes", RecursiveOne.class, RecursiveTwo.class);
			MethodParameter parameter1 = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage recursiveType1 = TypeUsageImpl.forParameter(parameter1);
			MethodParameter parameter2 = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(1);
			TypeUsage recursiveType2 = TypeUsageImpl.forParameter(parameter2);

			assertThat(recursiveType1.equals(recursiveType1)).isTrue();
			assertThat(recursiveType1.equals(recursiveType2)).isFalse();
		}

		@Example
		void recursiveTypeAsBounds() throws NoSuchMethodException {

			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public <E extends Recursive<E>, F extends OtherRecursive<F>> void withRecursiveTypes(
					List<E> one, List<E> two, List<F> three
				) {}
			}

			Method method = LocalClass.class.getMethod("withRecursiveTypes", List.class, List.class, List.class);
			MethodParameter parameter1 = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);
			TypeUsage recursiveType1 = TypeUsageImpl.forParameter(parameter1);
			MethodParameter parameter2 = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(1);
			TypeUsage recursiveType2 = TypeUsageImpl.forParameter(parameter2);
			MethodParameter parameter3 = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(2);
			TypeUsage recursiveType3 = TypeUsageImpl.forParameter(parameter3);

			assertThat(recursiveType1.equals(recursiveType1)).isTrue();
			assertThat(recursiveType1.equals(recursiveType2)).isTrue();
			assertThat(recursiveType1.equals(recursiveType3)).isFalse();
			assertThat(recursiveType3.equals(recursiveType1)).isFalse();
		}

	}

	@Group
	@Label("canBeAssigned(TypeUsage)")
	class CanBeAssigned {

		@Example
		void nonGenericTypes() {
			TypeUsage stringType = TypeUsage.of(String.class);

			assertThat(stringType.canBeAssignedTo(stringType)).isTrue();
			assertThat(stringType.canBeAssignedTo(TypeUsage.of(CharSequence.class))).isTrue();
			assertThat(TypeUsage.of(CharSequence.class).canBeAssignedTo(stringType)).isFalse();
		}

		@Example
		void primitiveAndBoxedTypes() {
			TypeUsage bigInt = TypeUsage.of(Integer.class);
			TypeUsage smallInt = TypeUsage.of(int.class);

			assertThat(bigInt.canBeAssignedTo(smallInt)).isTrue();
			assertThat(smallInt.canBeAssignedTo(bigInt)).isTrue();
			assertThat(bigInt.canBeAssignedTo(TypeUsage.of(Number.class))).isTrue();

			assertThat(smallInt.canBeAssignedTo(TypeUsage.of(Object.class))).isTrue();
		}

		@Example
		void arrayTypes() {
			TypeUsage stringArray = TypeUsage.of(String[].class);
			TypeUsage csArray = TypeUsage.of(CharSequence[].class);

			assertThat(stringArray.canBeAssignedTo(stringArray)).isTrue();
			assertThat(stringArray.canBeAssignedTo(csArray)).isTrue();
			assertThat(csArray.canBeAssignedTo(stringArray)).isFalse();
		}

		@Example
		void primitiveArrayTypes() {
			TypeUsage intArray = TypeUsage.of(int[].class);
			TypeUsage integerArray = TypeUsage.of(Integer[].class);

			assertThat(intArray.canBeAssignedTo(intArray)).isTrue();
			assertThat(intArray.canBeAssignedTo(integerArray)).isFalse();
			assertThat(integerArray.canBeAssignedTo(intArray)).isFalse();
		}

		@Example
		void parameterizedTypes() {
			TypeUsage listOfString = TypeUsage.of(List.class, TypeUsage.of(String.class));
			TypeUsage rawList = TypeUsage.of(List.class);
			TypeUsage listOfInteger = TypeUsage.of(List.class, TypeUsage.of(Integer.class));

			assertThat(listOfString.canBeAssignedTo(listOfString)).isTrue();
			assertThat(listOfString.canBeAssignedTo(rawList)).isTrue();
			assertThat(rawList.canBeAssignedTo(listOfString)).isTrue();
			assertThat(listOfString.canBeAssignedTo(listOfInteger)).isFalse();
		}

		@Example
		void parameterizedTypesWithPrimitives() {
			// This is not possible in Java but in jqwik
			TypeUsage listOfInteger = TypeUsage.of(List.class, TypeUsage.of(Integer.class));
			TypeUsage listOfInt = TypeUsage.of(List.class, TypeUsage.of(int.class));
			assertThat(listOfInt.canBeAssignedTo(listOfInteger)).isTrue();
			assertThat(listOfInteger.canBeAssignedTo(listOfInt)).isTrue();

			TypeUsage listOfString = TypeUsage.of(List.class, TypeUsage.of(String.class));
			assertThat(listOfInt.canBeAssignedTo(listOfString)).isFalse();
			assertThat(listOfString.canBeAssignedTo(listOfInt)).isFalse();
		}

		@Example
		void parameterizedTypesWithWildcards() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public List rawList() {return null;}

				@SuppressWarnings("WeakerAccess")
				public List<?> listOfWildcard() {return null;}

				@SuppressWarnings("WeakerAccess")
				public Collection<?> collectionOfWildcard() {return null;}

				@SuppressWarnings("WeakerAccess")
				public List<? extends Arbitrary> listOfBoundWildcard() {return null;}
			}

			Type wildcardType = LocalClass.class.getMethod("listOfWildcard").getAnnotatedReturnType().getType();
			TypeUsage listOfWildcard = TypeUsage.forType(wildcardType);

			Type collectionWildcardType = LocalClass.class.getMethod("collectionOfWildcard").getAnnotatedReturnType().getType();
			TypeUsage collectionOfWildcard = TypeUsage.forType(collectionWildcardType);

			Type boundWildcardType = LocalClass.class.getMethod("listOfBoundWildcard").getAnnotatedReturnType().getType();
			TypeUsage listOfBoundWildcard = TypeUsage.forType(boundWildcardType);

			Type rawListType = LocalClass.class.getMethod("rawList").getAnnotatedReturnType().getType();
			TypeUsage rawList = TypeUsage.forType(rawListType);

			TypeUsage listOfString = TypeUsage.of(List.class, TypeUsage.of(String.class));
			TypeUsage listOfNativeInt = TypeUsage.of(List.class, TypeUsage.of(int.class));

			assertThat(listOfBoundWildcard.canBeAssignedTo(listOfWildcard)).isTrue();
			assertThat(listOfBoundWildcard.canBeAssignedTo(collectionOfWildcard)).isTrue();

			assertThat(listOfWildcard.canBeAssignedTo(listOfBoundWildcard)).isFalse();
			assertThat(listOfWildcard.canBeAssignedTo(listOfWildcard)).isTrue();
			assertThat(listOfWildcard.canBeAssignedTo(collectionOfWildcard)).isTrue();
			assertThat(collectionOfWildcard.canBeAssignedTo(listOfWildcard)).isFalse();

			assertThat(listOfString.canBeAssignedTo(listOfWildcard)).isTrue();
			assertThat(listOfString.canBeAssignedTo(collectionOfWildcard)).isTrue();
			assertThat(listOfString.canBeAssignedTo(listOfBoundWildcard)).isFalse();

			assertThat(listOfWildcard.canBeAssignedTo(listOfString)).isFalse();

			// Violates variance rules
			// assertThat(listOfWildcard.canBeAssignedTo(rawList)).isTrue();

			assertThat(listOfNativeInt.canBeAssignedTo(listOfWildcard)).isTrue();
			assertThat(listOfNativeInt.canBeAssignedTo(listOfBoundWildcard)).isFalse();

			assertThat(rawList.canBeAssignedTo(listOfWildcard)).isTrue();
		}

		@Example
		void parameterizedTypesWithBoundWildcards() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public List<? extends Tuple> listOfBoundWildcard() {return null;}
			}

			Type boundWildcardType = LocalClass.class.getMethod("listOfBoundWildcard").getAnnotatedReturnType().getType();
			TypeUsage listOfBoundWildcard = TypeUsage.forType(boundWildcardType);

			TypeUsage listOfTypeThatMatchesBound = TypeUsage.of(List.class, TypeUsage.of(Tuple1.class, TypeUsage.of(String.class)));
			assertThat(listOfTypeThatMatchesBound.canBeAssignedTo(listOfBoundWildcard)).isTrue();
			assertThat(listOfBoundWildcard.canBeAssignedTo(listOfTypeThatMatchesBound)).isFalse();

			TypeUsage listOfTypeThatDoesntMatchBound = TypeUsage.of(List.class, TypeUsage.of(Tuple.class));
			assertThat(listOfBoundWildcard.canBeAssignedTo(listOfTypeThatDoesntMatchBound)).isFalse();
		}

		@Example
		void upperBoundWildcardTypes() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public List<? extends String> listOfWildcardString() {return null;}

				@SuppressWarnings("WeakerAccess")
				public List<? extends Serializable> listOfWildcardSerializable() {return null;}
			}

			Type wildcardStringType = LocalClass.class.getMethod("listOfWildcardString").getAnnotatedReturnType().getType();
			TypeUsage listOfWildcardString = TypeUsage.forType(wildcardStringType);

			Type wildcardSerializableType = LocalClass.class.getMethod("listOfWildcardSerializable").getAnnotatedReturnType().getType();
			TypeUsage listOfWildcardSerializable = TypeUsage.forType(wildcardSerializableType);

			TypeUsage listOfString = TypeUsage.of(List.class, TypeUsage.of(String.class));
			TypeUsage listOfArbitrary = TypeUsage.of(List.class, TypeUsage.of(Arbitrary.class));

			assertThat(listOfWildcardString.canBeAssignedTo(listOfWildcardString)).isTrue();

			assertThat(listOfString.canBeAssignedTo(listOfWildcardString)).isTrue();
			assertThat(listOfString.canBeAssignedTo(listOfWildcardSerializable)).isTrue();
			assertThat(listOfArbitrary.canBeAssignedTo(listOfWildcardString)).isFalse();

			assertThat(listOfWildcardString.canBeAssignedTo(listOfString)).isFalse();
			assertThat(listOfWildcardSerializable.canBeAssignedTo(listOfString)).isFalse();
		}

		@Example
		void lowerBoundWildcardTypes() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public List<? super String> listOfWildcardSuperString() {return null;}

			}

			Type wildcardStringType = LocalClass.class.getMethod("listOfWildcardSuperString").getAnnotatedReturnType().getType();
			TypeUsage listOfWildcardSuperString = TypeUsage.forType(wildcardStringType);

			TypeUsage listOfString = TypeUsage.of(List.class, TypeUsage.of(String.class));
			TypeUsage listOfCharSequence = TypeUsage.of(List.class, TypeUsage.of(CharSequence.class));
			TypeUsage listOfArbitrary = TypeUsage.of(List.class, TypeUsage.of(Arbitrary.class));

			assertThat(listOfWildcardSuperString.canBeAssignedTo(listOfWildcardSuperString)).isTrue();

			assertThat(listOfCharSequence.canBeAssignedTo(listOfWildcardSuperString)).isTrue();
			assertThat(listOfWildcardSuperString.canBeAssignedTo(listOfCharSequence)).isFalse();

			assertThat(listOfString.canBeAssignedTo(listOfWildcardSuperString)).isTrue();
			assertThat(listOfArbitrary.canBeAssignedTo(listOfWildcardSuperString)).isFalse();

		}

		@Example
		void multipleBoundTypeVariables() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public <T extends Serializable & CharSequence> List<T> listOfVariableSerializable() {return null;}
			}

			Type variableSerializableType = LocalClass.class.getMethod("listOfVariableSerializable").getAnnotatedReturnType().getType();
			TypeUsage listOfVariableSerializable = TypeUsage.forType(variableSerializableType);
			TypeUsage listOfString = TypeUsage.of(List.class, TypeUsage.of(String.class));

			assertThat(listOfVariableSerializable.canBeAssignedTo(listOfVariableSerializable)).isTrue();

			assertThat(listOfString.canBeAssignedTo(listOfVariableSerializable)).isFalse();
			assertThat(listOfVariableSerializable.canBeAssignedTo(listOfString)).isFalse();
		}

		@Example
		void parameterizedTypesWithTypeVariable() throws NoSuchMethodException {
			class LocalClass {
				public <T, M extends String> void test(
					List<T> listOfUnboundTypeVariable,
					List<M> listOfBoundTypeVariable,
					List<String> listOfString,
					List rawList
				) {
					// listOfUnboundTypeVariable = listOfBoundTypeVariable;
					// listOfBoundTypeVariable = listOfUnboundTypeVariable;
					// listOfUnboundTypeVariable = listOfString;
					// listOfArbitrary = listOfBoundTypeVariable;
					// listOfBoundTypeVariable = listOfString;
					// listOfString = listOfBoundTypeVariable;
					rawList = listOfUnboundTypeVariable;
					rawList = listOfBoundTypeVariable;
					rawList = listOfString;
					listOfUnboundTypeVariable = rawList;
					listOfBoundTypeVariable = rawList;
					listOfString = rawList;
				}

			}

			Method method = LocalClass.class.getMethod("test", List.class, List.class, List.class, List.class);
			List<MethodParameter> parameters = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class);

			TypeUsage listOfUnboundTypeVariable = TypeUsageImpl.forParameter(parameters.get(0));
			TypeUsage listOfBoundTypeVariable = TypeUsageImpl.forParameter(parameters.get(1));
			TypeUsage listOfString = TypeUsageImpl.forParameter(parameters.get(2));
			TypeUsage rawList = TypeUsageImpl.forParameter(parameters.get(3));

			assertThat(listOfUnboundTypeVariable.canBeAssignedTo(listOfUnboundTypeVariable)).isTrue();
			assertThat(listOfUnboundTypeVariable.canBeAssignedTo(listOfBoundTypeVariable)).isFalse();
			assertThat(listOfUnboundTypeVariable.canBeAssignedTo(listOfString)).isFalse();
			assertThat(listOfUnboundTypeVariable.canBeAssignedTo(rawList)).isTrue();

			assertThat(listOfBoundTypeVariable.canBeAssignedTo(listOfUnboundTypeVariable)).isFalse();
			assertThat(listOfBoundTypeVariable.canBeAssignedTo(listOfBoundTypeVariable)).isTrue();
			assertThat(listOfBoundTypeVariable.canBeAssignedTo(listOfString)).isFalse();
			assertThat(listOfBoundTypeVariable.canBeAssignedTo(rawList)).isTrue();

			assertThat(listOfString.canBeAssignedTo(listOfUnboundTypeVariable)).isFalse();
			assertThat(listOfString.canBeAssignedTo(listOfBoundTypeVariable)).isFalse();
			assertThat(listOfString.canBeAssignedTo(listOfString)).isTrue();
			assertThat(listOfString.canBeAssignedTo(rawList)).isTrue();

			assertThat(rawList.canBeAssignedTo(listOfUnboundTypeVariable)).isTrue();
			assertThat(rawList.canBeAssignedTo(listOfBoundTypeVariable)).isTrue();
			assertThat(rawList.canBeAssignedTo(listOfString)).isTrue();
			assertThat(rawList.canBeAssignedTo(rawList)).isTrue();
		}

		@Example
		void superTypes() {
			class LocalStringArbitrary extends DefaultStringArbitrary {
			}

			TypeUsage localStringArbitrary = TypeUsage.of(LocalStringArbitrary.class);

			assertThat(localStringArbitrary.canBeAssignedTo(TypeUsage.of(Object.class))).isTrue();
			assertThat(localStringArbitrary.canBeAssignedTo(TypeUsage.of(StringArbitrary.class))).isTrue();
			assertThat(localStringArbitrary.canBeAssignedTo(TypeUsage.of(String.class))).isFalse();

			TypeUsage stringArbitrary = TypeUsage.of(Arbitrary.class, TypeUsage.of(String.class));
			assertThat(localStringArbitrary.canBeAssignedTo(stringArbitrary)).isTrue();

			TypeUsage integerArbitrary = TypeUsage.of(Arbitrary.class, TypeUsage.of(Integer.class));
			assertThat(localStringArbitrary.canBeAssignedTo(integerArbitrary)).isFalse();
		}

		// See https://github.com/jqwik-team/jqwik/issues/499#issuecomment-1625949262
		@Example
		void canBeAssignedToParameterized() throws NoSuchFieldException, NoSuchMethodException {
			class LocalClass {
				public void test(
					Predicate<Double> predicateDouble,
					Predicate<Number> predicateNumber,
					Predicate<? super Number> predicateSuperNumber,
					Predicate<? extends Number> predicateExtendsNumber
				) {
					// predicateNumber = predicateExtendsNumber;
				}
			}
			Method method = LocalClass.class.getMethod("test", Predicate.class, Predicate.class, Predicate.class, Predicate.class);
			List<MethodParameter> parameters = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class);
			TypeUsage predicateDouble = TypeUsageImpl.forParameter(parameters.get(0));
			TypeUsage predicateNumber = TypeUsageImpl.forParameter(parameters.get(1));
			TypeUsage predicateSuperNumber = TypeUsageImpl.forParameter(parameters.get(2));
			TypeUsage predicateExtendsNumber = TypeUsageImpl.forParameter(parameters.get(3));

			assertThat(predicateSuperNumber.getTypeArgument(0).isSuperWildcard()).isTrue();
			assertThat(predicateSuperNumber.getTypeArgument(0).isExtendsConstraint()).isFalse();
			assertThat(predicateExtendsNumber.getTypeArgument(0).isSuperWildcard()).isFalse();
			assertThat(predicateExtendsNumber.getTypeArgument(0).isExtendsConstraint()).isTrue();

			assertThat(predicateDouble.canBeAssignedTo(predicateDouble)).isTrue();
			assertThat(predicateDouble.canBeAssignedTo(predicateNumber)).isFalse();
			assertThat(predicateDouble.canBeAssignedTo(predicateSuperNumber)).isFalse();
			assertThat(predicateDouble.canBeAssignedTo(predicateExtendsNumber)).isTrue();

			assertThat(predicateNumber.canBeAssignedTo(predicateDouble)).isFalse();
			assertThat(predicateNumber.canBeAssignedTo(predicateNumber)).isTrue();
			assertThat(predicateNumber.canBeAssignedTo(predicateSuperNumber)).isTrue();
			assertThat(predicateNumber.canBeAssignedTo(predicateExtendsNumber)).isTrue();

			assertThat(predicateSuperNumber.canBeAssignedTo(predicateDouble)).isFalse();
			assertThat(predicateSuperNumber.canBeAssignedTo(predicateNumber)).isFalse();
			assertThat(predicateSuperNumber.canBeAssignedTo(predicateSuperNumber)).isTrue();
			assertThat(predicateSuperNumber.canBeAssignedTo(predicateExtendsNumber)).isFalse();

			assertThat(predicateExtendsNumber.canBeAssignedTo(predicateDouble)).isFalse();
			assertThat(predicateExtendsNumber.canBeAssignedTo(predicateNumber)).isFalse();
			assertThat(predicateExtendsNumber.canBeAssignedTo(predicateSuperNumber)).isFalse();
			assertThat(predicateExtendsNumber.canBeAssignedTo(predicateExtendsNumber)).isTrue();
		}

		@Example
		void canBeAssignedToWithTwoParameters() throws NoSuchMethodException {
			abstract class StrFunction<T extends Number> implements Function<CharSequence, T> {
			}
			class LocalClass {
				public void test(
					Function<? extends CharSequence, Integer> functionExtendsCsInteger,
					Function<? extends CharSequence, Number> functionExtendsCsNumber,
					StrFunction<Number> customNumber,
					StrFunction<Integer> customInteger
				) {
					// functionExtendsCsInteger = customNumber; // fails to compile
					functionExtendsCsNumber = customNumber;
					functionExtendsCsInteger = customInteger;
					// functionExtendsCsNumber = customInteger; // fails to compile
				}
			}
			Method method = LocalClass.class.getMethod("test", Function.class, Function.class, StrFunction.class, StrFunction.class);
			List<MethodParameter> parameters = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class);
			TypeUsage functionExtendsCsInteger = TypeUsageImpl.forParameter(parameters.get(0));
			TypeUsage functionExtendsCsNumber = TypeUsageImpl.forParameter(parameters.get(1));
			TypeUsage customNumber = TypeUsageImpl.forParameter(parameters.get(2));
			TypeUsage customInteger = TypeUsageImpl.forParameter(parameters.get(3));

			assertThat(customNumber.canBeAssignedTo(functionExtendsCsInteger)).isFalse();
			assertThat(customNumber.canBeAssignedTo(functionExtendsCsNumber)).isTrue();

			assertThat(customInteger.canBeAssignedTo(functionExtendsCsInteger)).isTrue();
			assertThat(customInteger.canBeAssignedTo(functionExtendsCsNumber)).isFalse();
		}

		@Example
		void typedSuperTypesCanBeAssigned() throws NoSuchMethodException {
			class LocalClass {
				public ActionSequenceArbitrary<String> actionSequenceArbitraryString() {
					return null;
				}
			}

			Type stringActionSequenceArbitrary = LocalClass.class.getMethod("actionSequenceArbitraryString")
																 .getAnnotatedReturnType()
																 .getType();
			TypeUsage actionSequenceArbitraryStringType = TypeUsage.forType(stringActionSequenceArbitrary);
			TypeUsage arbitraryActionSequenceStringType = TypeUsage.of(
				Arbitrary.class,
				TypeUsage.of(
					ActionSequence.class,
					TypeUsage.of(String.class)
				)
			);
			assertThat(actionSequenceArbitraryStringType.canBeAssignedTo(arbitraryActionSequenceStringType)).isTrue();

			TypeUsage arbitraryActionSequenceIntegerType = TypeUsage.of(
				Arbitrary.class,
				TypeUsage.of(
					ActionSequence.class,
					TypeUsage.of(Integer.class)
				)
			);
			assertThat(actionSequenceArbitraryStringType.canBeAssignedTo(arbitraryActionSequenceIntegerType))
				.describedAs("%s can be assigned to %s but should not", actionSequenceArbitraryStringType, arbitraryActionSequenceIntegerType)
				.isFalse();
		}

		@Example
		void recursiveTypesCanBeAssigned() throws NoSuchMethodException {
			abstract class MyComparable<T> implements Comparable<MyComparable<T>> {
			}

			class LocalClass {
				public <T> void test(
					MyComparable<T> myComparableT,
					Comparable<MyComparable<T>> comparableMyComparableT,
					MyComparable<String> myComparableString,
					Comparable<MyComparable<String>> comparableMyComparableString
				) {
					// myComparableT = comparableMyComparableT;
					comparableMyComparableT = myComparableT;
					// myComparableString = comparableMyComparableString;
					comparableMyComparableString = myComparableString;

					// myComparableT = myComparableString;
					// myComparableString = myComparableT;
				}
			}
			Method method = LocalClass.class.getMethod("test", MyComparable.class, Comparable.class, MyComparable.class, Comparable.class);
			List<MethodParameter> parameters = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class);
			TypeUsage myComparableT = TypeUsageImpl.forParameter(parameters.get(0));
			TypeUsage comparableMyComparableT = TypeUsageImpl.forParameter(parameters.get(1));
			TypeUsage myComparableString = TypeUsageImpl.forParameter(parameters.get(2));
			TypeUsage comparableMyComparableString = TypeUsageImpl.forParameter(parameters.get(3));

			assertThat(myComparableT.canBeAssignedTo(comparableMyComparableT)).isTrue();
			assertThat(comparableMyComparableT.canBeAssignedTo(myComparableT)).isFalse();
			assertThat(myComparableString.canBeAssignedTo(comparableMyComparableString)).isTrue();
			assertThat(comparableMyComparableString.canBeAssignedTo(myComparableString)).isFalse();

			assertThat(myComparableT.canBeAssignedTo(myComparableString)).isFalse();
			assertThat(myComparableString.canBeAssignedTo(myComparableT)).isFalse();
		}
	}

	@Group
	class TypeUsageEnhancers {

		@Example
		void enhancersAreCalledForParameter() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withParameter(String aString) {}
			}

			Method method = LocalClass.class.getMethod("withParameter", String.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class).get(0);

			final TypeUsage typeUsageFromEnhancer1 = TypeUsage.forType(String.class);
			TypeUsage.Enhancer enhancer1 = new TypeUsage.Enhancer() {
				@Override
				public TypeUsage forParameter(TypeUsage original, Parameter parameter) {
					assertThat(parameter.getType()).isEqualTo(String.class);
					return typeUsageFromEnhancer1;
				}
			};

			TypeUsage.Enhancer enhancer2 = new TypeUsage.Enhancer() {
				@Override
				public TypeUsage forParameter(TypeUsage original, Parameter parameter) {
					assertThat(parameter.getType()).isEqualTo(String.class);
					return original;
				}
			};

			TypeUsage stringType = TypeUsageImpl.forParameter(parameter, Arrays.asList(enhancer1, enhancer2));
			assertThat(stringType).isSameAs(typeUsageFromEnhancer1);
		}

	}
}
