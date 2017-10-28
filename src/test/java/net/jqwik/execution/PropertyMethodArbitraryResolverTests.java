package net.jqwik.execution;

import static net.jqwik.TestDescriptorBuilder.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Parameter;
import java.math.*;
import java.util.*;
import java.util.stream.Stream;

import org.assertj.core.data.Offset;

import net.jqwik.TestHelper;
import net.jqwik.api.*;
import net.jqwik.api.constraints.WithNull;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import net.jqwik.properties.RandomGenerator;
import net.jqwik.properties.arbitraries.IntegerArbitrary;
import net.jqwik.support.JqwikReflectionSupport;

@Group
public class PropertyMethodArbitraryResolverTests {

	private enum AnEnum {
		One,
		Two,
		Three
	}

	private static class Thing {

	}

	@Group
	class Defaults {

		@Example
		void primitives() throws Exception {
			assertGenerated(Integer.class, "intParam", int.class);
			assertGenerated(Integer.class, "integerParam", Integer.class);

			assertGenerated(Long.class, "longParam", long.class);
			assertGenerated(Long.class, "longerParam", Long.class);

			assertGenerated(Double.class, "doubleParam", double.class);
			assertGenerated(Double.class, "doublerParam", Double.class);

			assertGenerated(Float.class, "floatParam", float.class);
			assertGenerated(Float.class, "floaterParam", Float.class);

			assertGenerated(Boolean.class, "booleanParam", boolean.class);
			assertGenerated(Boolean.class, "boxedBooleanParam", Boolean.class);

			assertGenerated(AnEnum.class, "enumParam", AnEnum.class);

			assertGenerated(String.class, "stringParam", String.class);
		}

		@Example
		void strings() throws Exception {
			assertGenerated(String.class, "stringParam", String.class);
		}

		@Example
		void enums() throws Exception {
			assertGenerated(AnEnum.class, "enumParam", AnEnum.class);
		}

		@Example
		void bigIntegers() throws Exception {
			assertGenerated(BigInteger.class, "bigIntegerParam", BigInteger.class);
		}

		@Example
		void bigDecimals() throws Exception {
			assertGenerated(BigDecimal.class, "bigDecimalParam", BigDecimal.class);
		}

		@Example
		void lists() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(DefaultParams.class, "integerList", List.class);
			Parameter parameter = getParameter(DefaultParams.class, "integerList");
			List actualList = generateCollection(provider, parameter);
			assertThat(actualList.get(0)).isInstanceOf(Integer.class);
		}

		@Example
		void sets() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(DefaultParams.class, "integerSet", Set.class);
			Parameter parameter = getParameter(DefaultParams.class, "integerSet");
			Set actualSet = generateCollection(provider, parameter);
			assertThat(actualSet.iterator().next()).isInstanceOf(Integer.class);
		}

		@Example
		void streams() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(DefaultParams.class, "integerStream", Stream.class);
			Parameter parameter = getParameter(DefaultParams.class, "integerStream");
			Stream<?> actualStream = (Stream<?>) generateObject(provider, parameter);
			actualStream.forEach(o -> assertThat(o).isInstanceOf(Integer.class));
		}

		@Example
		void arrays() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(DefaultParams.class, "integerArray", Integer[].class);
			Parameter parameter = getParameter(DefaultParams.class, "integerArray");
			Integer[] actualArray = (Integer[]) generateObject(provider, parameter);
			Arrays.stream(actualArray).forEach(o -> assertThat(o).isInstanceOf(Integer.class));
		}

		@Example
		void optionals() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(DefaultParams.class, "integerOptional", Optional.class);
			Parameter parameter = getParameter(DefaultParams.class, "integerOptional");
			@SuppressWarnings("unchecked")
			Optional<Object> actualOptional = (Optional<Object>) generateObject(provider, parameter);
			assertThat(actualOptional.orElse(Integer.MAX_VALUE)).isInstanceOf(Integer.class);
		}

		@Example
		void doNotUseDefaultIfForAllHasValue() throws Exception {
			assertNoArbitraryProvided(DefaultParams.class, "enumParamWithForAllValue", AnEnum.class);
		}

		private void assertNoArbitraryProvided(Class<DefaultParams> containerClass, String methodName, Class<?>... paramTypes)
			throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(containerClass, methodName, paramTypes);
			Parameter parameter = getParameter(containerClass, methodName);
			assertThat(provider.forParameter(parameter)).isEmpty();
		}

		private Object assertGenerated(Class<?> expectedType, String methodName, Class... paramTypes) throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(DefaultParams.class, methodName, paramTypes);
			Parameter parameter = getParameter(DefaultParams.class, methodName);
			Object actual = generateObject(provider, parameter);
			assertThat(actual).isInstanceOf(expectedType);
			return actual;
		}

		private class DefaultParams {
			@Property
			boolean intParam(@ForAll int aValue) {
				return true;
			}

			@Property
			boolean integerParam(@ForAll Integer aValue) {
				return true;
			}

			@Property
			boolean longParam(@ForAll long aValue) {
				return true;
			}

			@Property
			boolean longerParam(@ForAll Long aValue) {
				return true;
			}

			@Property
			boolean doubleParam(@ForAll double aValue) {
				return true;
			}

			@Property
			boolean doublerParam(@ForAll Double aValue) {
				return true;
			}

			@Property
			boolean floatParam(@ForAll float aValue) {
				return true;
			}

			@Property
			boolean floaterParam(@ForAll Float aValue) {
				return true;
			}

			@Property
			boolean booleanParam(@ForAll boolean aValue) {
				return true;
			}

			@Property
			boolean boxedBooleanParam(@ForAll Boolean aValue) {
				return true;
			}

			@Property
			boolean enumParam(@ForAll AnEnum aValue) {
				return true;
			}

			@Property
			boolean enumParamWithForAllValue(@ForAll("aValue") AnEnum aValue) {
				return true;
			}

			@Property
			boolean bigIntegerParam(@ForAll BigInteger aValue) {
				return true;
			}

			@Property
			boolean bigDecimalParam(@ForAll BigDecimal aValue) {
				return true;
			}

			@Property
			boolean stringParam(@ForAll String aValue) {
				return true;
			}

			@Property
			boolean integerList(@ForAll List<Integer> aValue) {
				return true;
			}

			@Property
			boolean integerArray(@ForAll Integer[] aValue) {
				return true;
			}

			@Property
			boolean integerSet(@ForAll Set<Integer> aValue) {
				return true;
			}

			@Property
			boolean integerStream(@ForAll Stream<Integer> aValue) {
				return true;
			}

			@Property
			boolean integerOptional(@ForAll Optional<Integer> aValue) {
				return true;
			}

		}

	}

	@Group
	class ProvidedArbitraries {

		@Example
		void unnamedStringGenerator() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(WithUnnamedGenerator.class, "string", String.class);
			Parameter parameter = getParameter(WithUnnamedGenerator.class, "string");
			Object actual = generateObject(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		private class WithUnnamedGenerator {
			@Property
			boolean string(@ForAll String aString) {
				return true;
			}

			@Provide
			Arbitrary<String> aString() {
				return Arbitraries.string('a', 'z');
			}
		}

		@Example
		void findBoxedTypeGenerator() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(WithNamedProviders.class, "longFromBoxedType", long.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "longFromBoxedType");
			Object actual = generateObject(provider, parameter);
			assertThat(actual).isInstanceOf(Long.class);
		}

		@Example
		void findStringGeneratorByName() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(WithNamedProviders.class, "string", String.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "string");
			Object actual = generateObject(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void findStringGeneratorByMethodName() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(WithNamedProviders.class, "stringByMethodName", String.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "stringByMethodName");
			Object actual = generateObject(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void findGeneratorByMethodNameOutsideGroup() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(WithNamedProviders.NestedWithNamedProviders.class, "nestedStringByMethodName", String.class);
			Parameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedStringByMethodName");
			Object actual = generateObject(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void findGeneratorByNameOutsideGroup() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(WithNamedProviders.NestedWithNamedProviders.class, "nestedString", String.class);
			Parameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedString");
			Object actual = generateObject(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void findFirstFitIfNoNameIsGivenInOutsideGroup() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(WithNamedProviders.NestedWithNamedProviders.class, "nestedThing", Thing.class);
			Parameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedThing");
			Object actual = generateObject(provider, parameter);
			assertThat(actual).isInstanceOf(Thing.class);
		}

		@Example
		void namedStringGeneratorNotFound() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(WithNamedProviders.class, "otherString", String.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "otherString");
			assertThat(provider.forParameter(parameter)).isEmpty();
		}

		@Example
		void findListOfProvidedStrings() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(WithNamedProviders.class, "listOfGeneratedStrings", List.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "listOfGeneratedStrings");
			List actualList = generateCollection(provider, parameter);
			assertThat(actualList.get(0)).isInstanceOf(String.class);
			assertThat(((String) actualList.get(0)).length()).isBetween(3, 10);
		}

		@Example
		void findFirstFitIfNoNameIsGiven() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(WithNamedProviders.class, "listOfThingWithoutName", List.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "listOfThingWithoutName");
			List actualList = generateCollection(provider, parameter);
			assertThat(actualList.get(0)).isInstanceOf(Thing.class);
		}

		private class WithNamedProviders {
			@Property
			boolean string(@ForAll("aString") String aString) {
				return true;
			}

			@Provide("aString")
			Arbitrary<String> aString() {
				return Arbitraries.string('a', 'z');
			}

			@Property
			boolean otherString(@ForAll("otherString") String aString) {
				return true;
			}

			@Property
			boolean stringByMethodName(@ForAll("byMethodName") String aString) {
				return true;
			}

			@Provide
			Arbitrary<String> byMethodName() {
				return Arbitraries.string('x', 'y');
			}

			@Property
			boolean longFromBoxedType(@ForAll("longBetween1and10") long aLong) {
				return true;
			}

			@Provide
			Arbitrary<Long> longBetween1and10() {
				return Arbitraries.longInteger(1L, 10L);
			}

			@Property
			boolean listOfGeneratedStrings(@ForAll("aName") List<String> nameList) {
				return true;
			}

			@Provide("aName")
			Arbitrary<String> aNameForList() {
				return Arbitraries.string('a', 'b', 10).filter(name -> name.length() > 2);
			}

			@Property
			boolean listOfThingWithoutName(@ForAll List<Thing> thingList) {
				return true;
			}

			@Provide()
			Arbitrary<Thing> aThing() {
				return Arbitraries.of(new Thing());
			}

			@Group
			class NestedWithNamedProviders {
				@Property
				boolean nestedStringByMethodName(@ForAll("byMethodName") String aString) {
					return true;
				}

				@Property
				boolean nestedString(@ForAll("aString") String aString) {
					return true;
				}

				@Property
				boolean nestedThing(@ForAll Thing aThing) {
					return true;
				}

			}
		}

	}

	static double nullProbability = 0.0;

	static class MockArbitrary implements Arbitrary<Object> {

		@Override
		public RandomGenerator<Object> generator(int tries) {
			return null;
		}

		public void configure(WithNull withNull) {
			nullProbability = withNull.value();
		}
	}

	@Group
	class Configuration {

		@Example
		void configureIsCalledOnDefaultArbitrary() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(WithConfiguration.class, "aNullableInteger", Integer.class);
			Parameter parameter = getParameter(WithConfiguration.class, "aNullableInteger");
			IntegerArbitrary integerArbitrary = (IntegerArbitrary) provider.forParameter(parameter).get().inner();

			assertThat(integerArbitrary.getNullProbability()).isCloseTo(0.42, Offset.offset(0.01));
		}

		@Example
		void configureIsCalledOnProvidedArbitrary() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(WithConfiguration.class, "aNullableMock", Object.class);
			Parameter parameter = getParameter(WithConfiguration.class, "aNullableMock");
			Optional<Arbitrary<Object>> arbitraryOptional = provider.forParameter(parameter);

			assertThat(arbitraryOptional).isPresent();
			assertThat(nullProbability).isCloseTo(0.41, Offset.offset(0.01));
		}

		private class WithConfiguration {
			@Property
			void aNullableInteger(@ForAll @WithNull(0.42) Integer anInt) {
			}

			@Property
			void aNullableMock(@ForAll("mockObject") @WithNull(0.41) Object anObject) {
			}

			@Provide
			Arbitrary<Object> mockObject() {
				return new MockArbitrary();
			}
		}

	}

	@SuppressWarnings("unchecked")
	private <T extends Collection> T generateCollection(PropertyMethodArbitraryResolver provider, Parameter parameter) {
		Object actual = generateObject(provider, parameter);
		T actualCollection = (T) actual;
		while (actualCollection.isEmpty()) {
			actualCollection = (T) generateObject(provider, parameter);
		}
		return actualCollection;
	}

	private static Object generateObject(PropertyMethodArbitraryResolver provider, Parameter parameter) {
		return TestHelper.generate(provider.forParameter(parameter).get());
	}

	private static PropertyMethodArbitraryResolver getProvider(Class<?> container, String methodName, Class<?>... parameterTypes)
		throws NoSuchMethodException, IllegalAccessException, InstantiationException {
		PropertyMethodDescriptor descriptor = getDescriptor(container, methodName, parameterTypes);
		return new PropertyMethodArbitraryResolver(descriptor, JqwikReflectionSupport.newInstanceWithDefaultConstructor(container));
	}

	private static PropertyMethodDescriptor getDescriptor(Class container, String methodName, Class... parameterTypes)
		throws NoSuchMethodException {
		return (PropertyMethodDescriptor) forMethod(container, methodName, parameterTypes).build();
	}

	private static Parameter getParameter(Class container, String methodName) {
		return TestHelper.getParametersFor(container, methodName).get(0);
	}

}
