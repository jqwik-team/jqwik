package net.jqwik.api.providers;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.Tuples.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.arbitraries.*;
import net.jqwik.support.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import static net.jqwik.api.providers.GenericType.*;
import static org.assertj.core.api.Assertions.*;

@Label("GenericType")
class GenericTypeTests {

	@Example
	void isAssignable() {
		assertThat(GenericType.of(CharSequence.class).isAssignableFrom(String.class)).isTrue();
		assertThat(GenericType.of(String.class).isAssignableFrom(CharSequence.class)).isFalse();
	}

	@Group
	@Label("of()")
	class Of {
		@Example
		@Label("simple type")
		void simpleType() {
			GenericType stringType = GenericType.of(String.class);
			assertThat(stringType.getRawType()).isEqualTo(String.class);
			assertThat(stringType.isOfType(String.class)).isTrue();
			assertThat(stringType.isGeneric()).isFalse();
			assertThat(stringType.isArray()).isFalse();
			assertThat(stringType.isEnum()).isFalse();

			assertThat(stringType.toString()).isEqualTo("String");
		}

		@Example
		@Label("parameterized type")
		void parameterizedType() {
			GenericType tupleType = GenericType.of(Tuple2.class, of(String.class), of(Integer.class));
			assertThat(tupleType.getRawType()).isEqualTo(Tuple2.class);
			assertThat(tupleType.isOfType(Tuple2.class)).isTrue();
			assertThat(tupleType.isGeneric()).isTrue();
			assertThat(tupleType.isArray()).isFalse();
			assertThat(tupleType.isEnum()).isFalse();

			assertThat(tupleType.toString()).isEqualTo("Tuple2<String, Integer>");
		}

		@Example
		@Label("array")
		void arrayTypes() {
			GenericType arrayType = GenericType.of(String[].class);
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
			assertThatThrownBy(() -> GenericType.of(String.class, of(String.class))).isInstanceOf(JqwikException.class);
		}

	}

	@Example
	@Label("forType() with parameterized type")
	void forType() throws NoSuchMethodException {
		class LocalClass {
			@SuppressWarnings("WeakerAccess")
			public Tuple2<String, Integer> withReturn() { return null; }
		}

		Type type = LocalClass.class.getMethod("withReturn").getAnnotatedReturnType().getType();
		GenericType tupleType = GenericType.forType(type);
		assertThat(tupleType.getRawType()).isEqualTo(Tuple2.class);
		assertThat(tupleType.isOfType(Tuple2.class)).isTrue();
		assertThat(tupleType.isGeneric()).isTrue();
		assertThat(tupleType.isArray()).isFalse();
		assertThat(tupleType.isEnum()).isFalse();

		assertThat(tupleType.toString()).isEqualTo("Tuple2<String, Integer>");
	}

	@Group
	@Label("forParameter()")
	class ForParameter {
		@Example
		void genericParameter() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withParameter(Tuple2<String, Integer> tuple) {}
			}

			Method method = LocalClass.class.getMethod("withParameter", Tuple2.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method)[0];
			GenericType tupleType = GenericType.forParameter(parameter);
			assertThat(tupleType.getRawType()).isEqualTo(Tuple2.class);
			assertThat(tupleType.isOfType(Tuple2.class)).isTrue();
			assertThat(tupleType.isGeneric()).isTrue();
			assertThat(tupleType.isArray()).isFalse();
			assertThat(tupleType.isEnum()).isFalse();

			assertThat(tupleType.toString()).isEqualTo("Tuple2<String, Integer>");
		}

		@Example
		void annotations() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withList(@Size(max = 2) List list) {}
			}

			Method method = LocalClass.class.getMethod("withList", List.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method)[0];
			GenericType parameterType = GenericType.forParameter(parameter);
			assertThat(parameterType.getRawType()).isEqualTo(List.class);
			assertThat(parameterType.getAnnotations().get(0)).isInstanceOf(Size.class);

			assertThat(parameterType.getAnnotation(Size.class)).isPresent();
			assertThat(parameterType.getAnnotation(WithNull.class)).isNotPresent();

			assertThat(parameterType.toString()).isEqualTo("@net.jqwik.api.constraints.Size(value=0, max=2, min=0) List");
		}

		@Example
		void wildcard() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withWildcard(Tuple2<? extends CharSequence, ? super String> tuple) {}
			}

			Method method = LocalClass.class.getMethod("withWildcard", Tuple2.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method)[0];
			GenericType wildcardType = GenericType.forParameter(parameter);

			GenericType first = wildcardType.getTypeArguments().get(0);
			assertThat(first.isWildcard()).isTrue();
			assertThat(first.isTypeVariableOrWildcard()).isTrue();
			assertThat(first.isTypeVariable()).isFalse();
			assertThat(first.hasLowerBounds()).isFalse();
			assertThat(first.hasUpperBounds()).isTrue();

			GenericType second = wildcardType.getTypeArguments().get(1);
			assertThat(second.isWildcard()).isTrue();
			assertThat(second.isTypeVariableOrWildcard()).isTrue();
			assertThat(second.isTypeVariable()).isFalse();
			assertThat(second.hasLowerBounds()).isTrue();
			assertThat(second.hasUpperBounds()).isFalse();

			assertThat(wildcardType.toString()).isEqualTo("Tuple2<? extends CharSequence, ? super String>");
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
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method)[0];
			GenericType typeVariableType = GenericType.forParameter(parameter);

			GenericType first = typeVariableType.getTypeArguments().get(0);
			assertThat(first.isWildcard()).isFalse();
			assertThat(first.isTypeVariableOrWildcard()).isTrue();
			assertThat(first.isTypeVariable()).isTrue();
			assertThat(first.hasLowerBounds()).isFalse();
			assertThat(first.hasUpperBounds()).isTrue();

			GenericType second = typeVariableType.getTypeArguments().get(0);
			assertThat(second.isWildcard()).isFalse();
			assertThat(second.isTypeVariableOrWildcard()).isTrue();
			assertThat(second.isTypeVariable()).isTrue();
			assertThat(second.hasLowerBounds()).isFalse();
			assertThat(second.hasUpperBounds()).isTrue();

			assertThat(typeVariableType.toString()).isEqualTo("Tuple2<T extends CharSequence, U extends Serializable & Cloneable>");
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
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method)[0];
			GenericType listType = GenericType.forParameter(parameter);
			assertThat(listType.getAnnotations().get(0)).isInstanceOf(Size.class);
			GenericType stringType = listType.getTypeArguments().get(0);
			assertThat(stringType.isOfType(String.class)).isTrue();
			assertThat(stringType.getAnnotations().get(0)).isInstanceOf(StringLength.class);
			assertThat(stringType.getAnnotations().get(1)).isInstanceOf(CharRange.class);

			// TODO: This string changes from Java 8 to 9 :-(
			// assertThat(stringType.toString()).isEqualTo("@net.jqwik.api.constraints.StringLength(value=0, max=2, min=0) "
			//												+ "@net.jqwik.api.constraints.CharRange(from=a, to=z) String");

		}

	}

	@Group
	@Label("findSuperType()")
	class FindSuperType {

		@Example
		@Label("direct super types")
		void findDirectSuperTypes() {
			class LocalStringArbitrary extends AbstractArbitraryBase implements Arbitrary<String> {
				@Override
				public RandomGenerator<String> generator(int genSize) {
					return null;
				}
			}

			GenericType stringArbitrary = GenericType.of(LocalStringArbitrary.class);
			assertThat(stringArbitrary.getRawType()).isEqualTo(LocalStringArbitrary.class);

			Optional<GenericType> superClass = stringArbitrary.findSuperType(AbstractArbitraryBase.class);
			assertThat(superClass.get().isOfType(AbstractArbitraryBase.class)).isTrue();

			Optional<GenericType> arbitraryInterface = stringArbitrary.findSuperType(Arbitrary.class);
			assertThat(arbitraryInterface.get().isOfType(Arbitrary.class)).isTrue();
			assertThat(arbitraryInterface.get().isGeneric()).isTrue();
			assertThat(arbitraryInterface.get().getTypeArguments().get(0).isOfType(String.class)).isTrue();

			assertThat(stringArbitrary.findSuperType(String.class)).isNotPresent();
		}

		@Example
		@Label("remote super types")
		void findRemoteSuperTypes() {
			class LocalStringArbitrary extends DefaultStringArbitrary {
			}

			GenericType stringArbitrary = GenericType.of(LocalStringArbitrary.class);

			assertThat(stringArbitrary.findSuperType(Object.class)).isPresent();
			assertThat(stringArbitrary.findSuperType(AbstractArbitraryBase.class)).isPresent();

			Optional<GenericType> arbitraryInterface = stringArbitrary.findSuperType(Arbitrary.class);
			assertThat(arbitraryInterface.get().isOfType(Arbitrary.class)).isTrue();
			assertThat(arbitraryInterface.get().isGeneric()).isTrue();
			assertThat(arbitraryInterface.get().getTypeArguments().get(0).isOfType(String.class)).isTrue();
		}
	}

	@Group
	@Label("canBeAssigned(GenericType)")
	class CanBeAssigned {

		@Example
		void nonGenericTypes() {
			GenericType stringType = GenericType.of(String.class);

			assertThat(stringType.canBeAssignedTo(stringType)).isTrue();
			assertThat(stringType.canBeAssignedTo(GenericType.of(CharSequence.class))).isTrue();
			assertThat(GenericType.of(CharSequence.class).canBeAssignedTo(stringType)).isFalse();
		}

		@Example
		void primitiveAndBoxedTypes() {
			GenericType bigInt = GenericType.of(Integer.class);
			GenericType smallInt = GenericType.of(int.class);

			assertThat(bigInt.canBeAssignedTo(smallInt)).isTrue();
			assertThat(smallInt.canBeAssignedTo(bigInt)).isTrue();
			assertThat(bigInt.canBeAssignedTo(GenericType.of(Number.class))).isTrue();
		}

		@Example
		void arrayTypes() {
			GenericType stringArray = GenericType.of(String[].class);
			GenericType csArray = GenericType.of(CharSequence[].class);

			assertThat(stringArray.canBeAssignedTo(stringArray)).isTrue();
			assertThat(stringArray.canBeAssignedTo(csArray)).isTrue();
			assertThat(csArray.canBeAssignedTo(stringArray)).isFalse();
		}

		@Example
		void primitiveArrayTypes() {
			GenericType intArray = GenericType.of(int[].class);
			GenericType integerArray = GenericType.of(Integer[].class);

			assertThat(intArray.canBeAssignedTo(intArray)).isTrue();
			assertThat(intArray.canBeAssignedTo(integerArray)).isFalse();
			assertThat(integerArray.canBeAssignedTo(intArray)).isFalse();
		}

		@Example
		void parameterizedTypes() {
			GenericType listOfString = GenericType.of(List.class, GenericType.of(String.class));
			GenericType rawList = GenericType.of(List.class);
			GenericType listOfInteger = GenericType.of(List.class, GenericType.of(Integer.class));

			assertThat(listOfString.canBeAssignedTo(listOfString)).isTrue();
			assertThat(listOfString.canBeAssignedTo(rawList)).isTrue();
			assertThat(rawList.canBeAssignedTo(listOfString)).isTrue();
			assertThat(listOfString.canBeAssignedTo(listOfInteger)).isFalse();
		}

		@Example
		void parameterizedTypesWithWildcards() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public List<?> listOfWildcard() { return null; }

				@SuppressWarnings("WeakerAccess")
				public List<? extends Arbitrary> listOfBoundWildcard() { return null; }
			}

			Type wildcardType = LocalClass.class.getMethod("listOfWildcard").getAnnotatedReturnType().getType();
			GenericType listOfWildcard = GenericType.forType(wildcardType);

			Type boundWildcardType = LocalClass.class.getMethod("listOfBoundWildcard").getAnnotatedReturnType().getType();
			GenericType listOfBoundWildcard = GenericType.forType(boundWildcardType);

			GenericType listOfString = GenericType.of(List.class, GenericType.of(String.class));
			GenericType rawList = GenericType.of(List.class);

			assertThat(listOfBoundWildcard.canBeAssignedTo(listOfWildcard)).isTrue();
			assertThat(listOfWildcard.canBeAssignedTo(listOfBoundWildcard)).isFalse();
			assertThat(listOfWildcard.canBeAssignedTo(listOfWildcard)).isTrue();
			assertThat(listOfString.canBeAssignedTo(listOfWildcard)).isTrue();
			assertThat(listOfString.canBeAssignedTo(listOfBoundWildcard)).isFalse();
			assertThat(listOfWildcard.canBeAssignedTo(listOfString)).isFalse();
			assertThat(listOfWildcard.canBeAssignedTo(rawList)).isTrue();
			assertThat(rawList.canBeAssignedTo(listOfWildcard)).isTrue();
		}

		@Example
		void upperBoundWildcardTypes() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public List<? extends String> listOfWildcardString() { return null; }

				@SuppressWarnings("WeakerAccess")
				public List<? extends Serializable> listOfWildcardSerializable() { return null; }
			}

			Type wildcardStringType = LocalClass.class.getMethod("listOfWildcardString").getAnnotatedReturnType().getType();
			GenericType listOfWildcardString = GenericType.forType(wildcardStringType);

			Type wildcardSerializableType = LocalClass.class.getMethod("listOfWildcardSerializable").getAnnotatedReturnType().getType();
			GenericType listOfWildcardSerializable = GenericType.forType(wildcardSerializableType);

			GenericType listOfString = GenericType.of(List.class, GenericType.of(String.class));
			GenericType listOfArbitrary = GenericType.of(List.class, GenericType.of(Arbitrary.class));

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
				public List<? super String> listOfWildcardSuperString() { return null; }

			}

			Type wildcardStringType = LocalClass.class.getMethod("listOfWildcardSuperString").getAnnotatedReturnType().getType();
			GenericType listOfWildcardSuperString = GenericType.forType(wildcardStringType);

			GenericType listOfString = GenericType.of(List.class, GenericType.of(String.class));
			GenericType listOfCharSequence = GenericType.of(List.class, GenericType.of(CharSequence.class));
			GenericType listOfArbitrary = GenericType.of(List.class, GenericType.of(Arbitrary.class));

			assertThat(listOfWildcardSuperString.canBeAssignedTo(listOfWildcardSuperString)).isTrue();

			assertThat(listOfCharSequence.canBeAssignedTo(listOfWildcardSuperString)).isTrue();
			assertThat(listOfWildcardSuperString.canBeAssignedTo(listOfCharSequence)).isFalse();

			assertThat(listOfString.canBeAssignedTo(listOfWildcardSuperString)).isTrue();
			assertThat(listOfArbitrary.canBeAssignedTo(listOfWildcardSuperString)).isFalse();

		}

		@Example
		void boundTypeVariables() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public <T extends String> List<T> listOfVariableString() { return null; }

				@SuppressWarnings("WeakerAccess")
				public <T extends Serializable & CharSequence> List<T> listOfVariableSerializable() { return null; }
			}

			Type variableStringType = LocalClass.class.getMethod("listOfVariableString").getAnnotatedReturnType().getType();
			GenericType listOfVariableString = GenericType.forType(variableStringType);

			Type variableSerializableType = LocalClass.class.getMethod("listOfVariableSerializable").getAnnotatedReturnType().getType();
			GenericType listOfVariableSerializable = GenericType.forType(variableSerializableType);

			GenericType listOfString = GenericType.of(List.class, GenericType.of(String.class));
			GenericType listOfArbitrary = GenericType.of(List.class, GenericType.of(Arbitrary.class));

			assertThat(listOfVariableString.canBeAssignedTo(listOfVariableString)).isTrue();

			assertThat(listOfString.canBeAssignedTo(listOfVariableString)).isTrue();
			assertThat(listOfString.canBeAssignedTo(listOfVariableSerializable)).isTrue();
			assertThat(listOfArbitrary.canBeAssignedTo(listOfVariableString)).isFalse();

			assertThat(listOfVariableString.canBeAssignedTo(listOfString)).isFalse();
			assertThat(listOfVariableSerializable.canBeAssignedTo(listOfString)).isFalse();
		}

		@Example
		void parameterizedTypesWithTypeVariable() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public <T> List<T> listOfTypeVariable() { return null; }

				@SuppressWarnings("WeakerAccess")
				public <T extends Arbitrary> List<T> listOfBoundTypeVariable() { return null; }
			}

			Type variableType = LocalClass.class.getMethod("listOfTypeVariable").getAnnotatedReturnType().getType();
			GenericType listOfVariable = GenericType.forType(variableType);

			Type boundVariableType = LocalClass.class.getMethod("listOfBoundTypeVariable").getAnnotatedReturnType().getType();
			GenericType listOfBoundVariable = GenericType.forType(boundVariableType);

			GenericType listOfString = GenericType.of(List.class, GenericType.of(String.class));
			GenericType rawList = GenericType.of(List.class);

			assertThat(listOfBoundVariable.canBeAssignedTo(listOfVariable)).isTrue();
			assertThat(listOfVariable.canBeAssignedTo(listOfBoundVariable)).isFalse();
			assertThat(listOfVariable.canBeAssignedTo(listOfVariable)).isTrue();
			assertThat(listOfString.canBeAssignedTo(listOfVariable)).isTrue();
			assertThat(listOfString.canBeAssignedTo(listOfBoundVariable)).isFalse();
			assertThat(listOfVariable.canBeAssignedTo(listOfString)).isFalse();
			assertThat(listOfVariable.canBeAssignedTo(rawList)).isTrue();
			assertThat(rawList.canBeAssignedTo(listOfVariable)).isTrue();
		}

		@Example
		void superTypes() {
			class LocalStringArbitrary extends DefaultStringArbitrary {
			}

			GenericType localStringArbitrary = GenericType.of(LocalStringArbitrary.class);

			assertThat(localStringArbitrary.canBeAssignedTo(GenericType.of(Object.class))).isTrue();
			assertThat(localStringArbitrary.canBeAssignedTo(GenericType.of(AbstractArbitraryBase.class))).isTrue();
			assertThat(localStringArbitrary.canBeAssignedTo(GenericType.of(String.class))).isFalse();

			GenericType stringArbitrary = GenericType.of(Arbitrary.class, GenericType.of(String.class));
			assertThat(localStringArbitrary.canBeAssignedTo(stringArbitrary)).isTrue();

			GenericType integerArbitrary = GenericType.of(Arbitrary.class, GenericType.of(Integer.class));
			// TODO: jqwik is too loose here which might result in a class cast exception during property resolution
			// assertThat(localStringArbitrary.canBeAssignedTo(integerArbitrary)).isFalse();
		}

		@Example
		void typedSuperTypes() throws NoSuchMethodException {
			class LocalClass {
				public ActionSequenceArbitrary<String> stringActionSequenceArbitrary() {
					return null;
				}
			}

			Type stringActionSequenceArbitrary = LocalClass.class.getMethod("stringActionSequenceArbitrary")
																 .getAnnotatedReturnType()
																 .getType();
			GenericType actionSequenceStringArbitraryType = GenericType.forType(stringActionSequenceArbitrary);
			GenericType actionSequenceArbitrary = GenericType.of(
				Arbitrary.class,
				GenericType.of(
					ActionSequence.class,
					GenericType.of(String.class)
				)
			);
			assertThat(actionSequenceStringArbitraryType.canBeAssignedTo(actionSequenceArbitrary)).isTrue();

			GenericType actionSequenceIntegerArbitrary = GenericType.of(
				Arbitrary.class,
				GenericType.of(
					ActionSequence.class,
					GenericType.of(Integer.class)
				)
			);
			// TODO: jqwik is too loose here which might result in a class cast exception during property resolution
			// assertThat(actionSequenceStringArbitraryType.canBeAssignedTo(actionSequenceIntegerArbitrary)).isFalse();
		}
	}

}
