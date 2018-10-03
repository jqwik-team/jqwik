package net.jqwik.api.providers;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.arbitraries.*;
import net.jqwik.support.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.providers.TypeUsage.*;

@Label("TypeUsage")
class TypeUsageTests {

	@Example
	void isAssignable() {
		assertThat(TypeUsage.of(CharSequence.class).isAssignableFrom(String.class)).isTrue();
		assertThat(TypeUsage.of(String.class).isAssignableFrom(CharSequence.class)).isFalse();
	}

	@Group
	@Label("of()")
	class Of {
		@Example
		@Label("simple type")
		void simpleType() {
			TypeUsage stringType = TypeUsage.of(String.class);
			assertThat(stringType.getRawType()).isEqualTo(String.class);
			assertThat(stringType.isOfType(String.class)).isTrue();
			assertThat(stringType.isGeneric()).isFalse();
			assertThat(stringType.isArray()).isFalse();
			assertThat(stringType.isEnum()).isFalse();

			assertThat(stringType.toString()).isEqualTo("String");

			assertThat(stringType.equals(TypeUsage.of(String.class))).isTrue();
			assertThat(stringType.equals(TypeUsage.of(Number.class))).isFalse();
		}

		@Example
		@Label("parameterized type")
		void parameterizedType() {
			TypeUsage tupleType = TypeUsage.of(Tuple2.class, of(String.class), of(Integer.class));
			assertThat(tupleType.getRawType()).isEqualTo(Tuple2.class);
			assertThat(tupleType.isOfType(Tuple2.class)).isTrue();
			assertThat(tupleType.isGeneric()).isTrue();
			assertThat(tupleType.isArray()).isFalse();
			assertThat(tupleType.isEnum()).isFalse();

			assertThat(tupleType.toString()).isEqualTo("Tuple2<String, Integer>");

			TypeUsage equalType = TypeUsage.of(Tuple2.class, of(String.class), of(Integer.class));
			assertThat(tupleType.equals(equalType)).isTrue();
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
			public Tuple2<String, Integer> withReturn() { return null; }
		}

		Type type = LocalClass.class.getMethod("withReturn").getAnnotatedReturnType().getType();
		TypeUsage tupleType = TypeUsage.forType(type);
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
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class)[0];
			TypeUsage tupleType = TypeUsage.forParameter(parameter);
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

				public void withNonEqualList(@Size(max = 3) List list) {}
			}

			Method method = LocalClass.class.getMethod("withList", List.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class)[0];
			TypeUsage parameterType = TypeUsage.forParameter(parameter);
			assertThat(parameterType.getRawType()).isEqualTo(List.class);
			assertThat(parameterType.getAnnotations().get(0)).isInstanceOf(Size.class);

			assertThat(parameterType.findAnnotation(Size.class)).isPresent();
			assertThat(parameterType.findAnnotation(WithNull.class)).isNotPresent();

			assertThat(parameterType.isAnnotated(Size.class)).isTrue();
			assertThat(parameterType.isAnnotated(WithNull.class)).isFalse();

			assertThat(parameterType.toString()).isEqualTo("@net.jqwik.api.constraints.Size(value=0, max=2, min=0) List");

			TypeUsage equalParameterType = TypeUsage.forParameter(parameter);
			assertThat(parameterType.equals(equalParameterType)).isTrue();

			Method methodWithNonEqualList = LocalClass.class.getMethod("withNonEqualList", List.class);
			MethodParameter nonEqualParameter = JqwikReflectionSupport.getMethodParameters(methodWithNonEqualList, LocalClass.class)[0];
			TypeUsage nonEqualParameterType = TypeUsage.forParameter(nonEqualParameter);
			assertThat(parameterType.equals(nonEqualParameterType)).isFalse();

		}

		@Example
		void wildcard() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public void withWildcard(Tuple2<? extends CharSequence, ? super String> tuple) {}

				public void withNonEqualWildcard(Tuple2<? extends CharSequence, ? super Number> tuple) {}
			}

			Method method = LocalClass.class.getMethod("withWildcard", Tuple2.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class)[0];
			TypeUsage wildcardType = TypeUsage.forParameter(parameter);

			TypeUsage first = wildcardType.getTypeArguments().get(0);
			assertThat(first.isWildcard()).isTrue();
			assertThat(first.isTypeVariableOrWildcard()).isTrue();
			assertThat(first.isTypeVariable()).isFalse();
			assertThat(first.hasLowerBounds()).isFalse();
			assertThat(first.hasUpperBounds()).isTrue();

			TypeUsage second = wildcardType.getTypeArguments().get(1);
			assertThat(second.isWildcard()).isTrue();
			assertThat(second.isTypeVariableOrWildcard()).isTrue();
			assertThat(second.isTypeVariable()).isFalse();
			assertThat(second.hasLowerBounds()).isTrue();
			assertThat(second.hasUpperBounds()).isFalse();

			assertThat(wildcardType.toString()).isEqualTo("Tuple2<? extends CharSequence, ? super String>");

			TypeUsage equalWildcardType = TypeUsage.forParameter(parameter);
			assertThat(wildcardType.equals(equalWildcardType)).isTrue();

			Method methodWithNonEqualWildcard = LocalClass.class.getMethod("withNonEqualWildcard", Tuple2.class);
			MethodParameter nonEqualParameter = JqwikReflectionSupport.getMethodParameters(methodWithNonEqualWildcard, LocalClass.class)[0];
			TypeUsage nonEqualWildcardType = TypeUsage.forParameter(nonEqualParameter);
			assertThat(wildcardType.equals(nonEqualWildcardType)).isFalse();
		}

		@Example
		void typeVariable() throws NoSuchMethodException {
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
			}

			Method method = LocalClass.class.getMethod("withTypeVariable", Tuple2.class);
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class)[0];
			TypeUsage typeVariableType = TypeUsage.forParameter(parameter);

			TypeUsage first = typeVariableType.getTypeArguments().get(0);
			assertThat(first.isWildcard()).isFalse();
			assertThat(first.isTypeVariableOrWildcard()).isTrue();
			assertThat(first.isTypeVariable()).isTrue();
			assertThat(first.hasLowerBounds()).isFalse();
			assertThat(first.hasUpperBounds()).isTrue();

			TypeUsage second = typeVariableType.getTypeArguments().get(0);
			assertThat(second.isWildcard()).isFalse();
			assertThat(second.isTypeVariableOrWildcard()).isTrue();
			assertThat(second.isTypeVariable()).isTrue();
			assertThat(second.hasLowerBounds()).isFalse();
			assertThat(second.hasUpperBounds()).isTrue();

			assertThat(typeVariableType.toString()).isEqualTo("Tuple2<T extends CharSequence, U extends Serializable & Cloneable>");

			TypeUsage equalTypeVariableType = TypeUsage.forParameter(parameter);
			assertThat(typeVariableType.equals(equalTypeVariableType)).isTrue();


			Method differentByNameMethod = LocalClass.class.getMethod("differentByName", Tuple2.class);
			MethodParameter differentByNameParameter = JqwikReflectionSupport.getMethodParameters(differentByNameMethod, LocalClass.class)[0];
			TypeUsage differentByNameType = TypeUsage.forParameter(differentByNameParameter);
			assertThat(typeVariableType.equals(differentByNameType)).isFalse();

			Method differentByLowerBoundsMethod = LocalClass.class.getMethod("differentByLowerBounds", Tuple2.class);
			MethodParameter differentByLowerBoundsParameter = JqwikReflectionSupport.getMethodParameters(differentByLowerBoundsMethod, LocalClass.class)[0];
			TypeUsage differentByLowerBoundsType = TypeUsage.forParameter(differentByLowerBoundsParameter);
			assertThat(typeVariableType.equals(differentByLowerBoundsType)).isFalse();

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
			MethodParameter parameter = JqwikReflectionSupport.getMethodParameters(method, LocalClass.class)[0];
			TypeUsage listType = TypeUsage.forParameter(parameter);
			assertThat(listType.getAnnotations().get(0)).isInstanceOf(Size.class);
			TypeUsage stringType = listType.getTypeArguments().get(0);
			assertThat(stringType.isOfType(String.class)).isTrue();
			assertThat(stringType.getAnnotations().get(0)).isInstanceOf(StringLength.class);
			assertThat(stringType.getAnnotations().get(1)).isInstanceOf(CharRange.class);

			// TODO: This string changes from Java 8 to 9 :-(
			// assertThat(stringType.toString()).isEqualTo("@net.jqwik.api.constraints.StringLength(value=0, max=2, min=0) "
			// Java 8:										+ "@net.jqwik.api.constraints.CharRange(from=a, to=z) String");
			// Java 9:										+ "@net.jqwik.api.constraints.CharRange(from='a', to='z') String");
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

			TypeUsage stringArbitrary = TypeUsage.of(LocalStringArbitrary.class);
			assertThat(stringArbitrary.getRawType()).isEqualTo(LocalStringArbitrary.class);

			Optional<TypeUsage> superClass = stringArbitrary.findSuperType(AbstractArbitraryBase.class);
			assertThat(superClass.get().isOfType(AbstractArbitraryBase.class)).isTrue();

			Optional<TypeUsage> arbitraryInterface = stringArbitrary.findSuperType(Arbitrary.class);
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

			TypeUsage stringArbitrary = TypeUsage.of(LocalStringArbitrary.class);

			assertThat(stringArbitrary.findSuperType(Object.class)).isPresent();
			assertThat(stringArbitrary.findSuperType(AbstractArbitraryBase.class)).isPresent();

			Optional<TypeUsage> arbitraryInterface = stringArbitrary.findSuperType(Arbitrary.class);
			assertThat(arbitraryInterface.get().isOfType(Arbitrary.class)).isTrue();
			assertThat(arbitraryInterface.get().isGeneric()).isTrue();
			assertThat(arbitraryInterface.get().getTypeArguments().get(0).isOfType(String.class)).isTrue();
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
		void parameterizedTypesWithWildcards() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public List<?> listOfWildcard() { return null; }

				@SuppressWarnings("WeakerAccess")
				public List<? extends Arbitrary> listOfBoundWildcard() { return null; }
			}

			Type wildcardType = LocalClass.class.getMethod("listOfWildcard").getAnnotatedReturnType().getType();
			TypeUsage listOfWildcard = TypeUsage.forType(wildcardType);

			Type boundWildcardType = LocalClass.class.getMethod("listOfBoundWildcard").getAnnotatedReturnType().getType();
			TypeUsage listOfBoundWildcard = TypeUsage.forType(boundWildcardType);

			TypeUsage listOfString = TypeUsage.of(List.class, TypeUsage.of(String.class));
			TypeUsage rawList = TypeUsage.of(List.class);

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
				public List<? super String> listOfWildcardSuperString() { return null; }

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
		void boundTypeVariables() throws NoSuchMethodException {
			class LocalClass {
				@SuppressWarnings("WeakerAccess")
				public <T extends String> List<T> listOfVariableString() { return null; }

				@SuppressWarnings("WeakerAccess")
				public <T extends Serializable & CharSequence> List<T> listOfVariableSerializable() { return null; }
			}

			Type variableStringType = LocalClass.class.getMethod("listOfVariableString").getAnnotatedReturnType().getType();
			TypeUsage listOfVariableString = TypeUsage.forType(variableStringType);

			Type variableSerializableType = LocalClass.class.getMethod("listOfVariableSerializable").getAnnotatedReturnType().getType();
			TypeUsage listOfVariableSerializable = TypeUsage.forType(variableSerializableType);

			TypeUsage listOfString = TypeUsage.of(List.class, TypeUsage.of(String.class));
			TypeUsage listOfArbitrary = TypeUsage.of(List.class, TypeUsage.of(Arbitrary.class));

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
			TypeUsage listOfVariable = TypeUsage.forType(variableType);

			Type boundVariableType = LocalClass.class.getMethod("listOfBoundTypeVariable").getAnnotatedReturnType().getType();
			TypeUsage listOfBoundVariable = TypeUsage.forType(boundVariableType);

			TypeUsage listOfString = TypeUsage.of(List.class, TypeUsage.of(String.class));
			TypeUsage rawList = TypeUsage.of(List.class);

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

			TypeUsage localStringArbitrary = TypeUsage.of(LocalStringArbitrary.class);

			assertThat(localStringArbitrary.canBeAssignedTo(TypeUsage.of(Object.class))).isTrue();
			assertThat(localStringArbitrary.canBeAssignedTo(TypeUsage.of(AbstractArbitraryBase.class))).isTrue();
			assertThat(localStringArbitrary.canBeAssignedTo(TypeUsage.of(String.class))).isFalse();

			TypeUsage stringArbitrary = TypeUsage.of(Arbitrary.class, TypeUsage.of(String.class));
			assertThat(localStringArbitrary.canBeAssignedTo(stringArbitrary)).isTrue();

			TypeUsage integerArbitrary = TypeUsage.of(Arbitrary.class, TypeUsage.of(Integer.class));
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
			TypeUsage actionSequenceStringArbitraryType = TypeUsage.forType(stringActionSequenceArbitrary);
			TypeUsage actionSequenceArbitrary = TypeUsage.of(
				Arbitrary.class,
				TypeUsage.of(
					ActionSequence.class,
					TypeUsage.of(String.class)
				)
			);
			assertThat(actionSequenceStringArbitraryType.canBeAssignedTo(actionSequenceArbitrary)).isTrue();

			TypeUsage actionSequenceIntegerArbitrary = TypeUsage.of(
				Arbitrary.class,
				TypeUsage.of(
					ActionSequence.class,
					TypeUsage.of(Integer.class)
				)
			);
			// TODO: jqwik is too loose here which might result in a class cast exception during property resolution
			// assertThat(actionSequenceStringArbitraryType.canBeAssignedTo(actionSequenceIntegerArbitrary)).isFalse();
		}
	}

}
