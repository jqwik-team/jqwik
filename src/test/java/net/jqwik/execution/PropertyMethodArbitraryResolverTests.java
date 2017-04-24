package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;
import net.jqwik.support.*;
import org.assertj.core.data.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import static net.jqwik.TestDescriptorBuilder.*;
import static org.assertj.core.api.Assertions.*;

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
		void simpleDefaults() throws Exception {
			assertGenerated(Integer.class, "intParam", int.class);
			assertGenerated(Integer.class, "integerParam", Integer.class);

			assertGenerated(Long.class, "longParam", long.class);
			assertGenerated(Long.class, "longerParam", Long.class);

			assertGenerated(Boolean.class, "booleanParam", boolean.class);
			assertGenerated(Boolean.class, "boxedBooleanParam", Boolean.class);

			assertGenerated(AnEnum.class, "enumParam", AnEnum.class);

			assertGenerated(String.class, "stringParam", String.class);
		}

		@Example
		void listDefaults() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(DefaultParams.class, "integerList", List.class);
			Parameter parameter = getParameter(DefaultParams.class, "integerList");
			List actualList = generateCollection(provider, parameter);
			assertThat(actualList.get(0)).isInstanceOf(Integer.class);
		}

		@Example
		void setDefaults() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(DefaultParams.class, "integerSet", Set.class);
			Parameter parameter = getParameter(DefaultParams.class, "integerSet");
			Set actualSet = generateCollection(provider, parameter);
			assertThat(actualSet.iterator().next()).isInstanceOf(Integer.class);
		}

		@Example
		void streamDefaults() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(DefaultParams.class, "integerStream", Stream.class);
			Parameter parameter = getParameter(DefaultParams.class, "integerStream");
			Stream actualStream = (Stream) generateObject(provider, parameter);
			actualStream.forEach(o -> assertThat(o).isInstanceOf(Integer.class));
		}

		@Example
		void arrayDefaults() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(DefaultParams.class, "integerArray", Integer[].class);
			Parameter parameter = getParameter(DefaultParams.class, "integerArray");
			Integer[] actualArray = (Integer[]) generateObject(provider, parameter);
			Arrays.stream(actualArray).forEach(o -> assertThat(o).isInstanceOf(Integer.class));
		}

		@Example
		void optionalDefaults() throws Exception {
			PropertyMethodArbitraryResolver provider = getProvider(DefaultParams.class, "integerOptional", Optional.class);
			Parameter parameter = getParameter(DefaultParams.class, "integerOptional");
			Optional actualOptional = (Optional) generateObject(provider, parameter);
			assertThat(actualOptional.orElseGet(() -> Integer.MAX_VALUE)).isInstanceOf(Integer.class);
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
			boolean intParam(@ForAll int anInt) {
				return true;
			}

			@Property
			boolean integerParam(@ForAll Integer anInt) {
				return true;
			}

			@Property
			boolean longParam(@ForAll long anInt) {
				return true;
			}

			@Property
			boolean longerParam(@ForAll Long anInt) {
				return true;
			}

			@Property
			boolean booleanParam(@ForAll boolean aBoolean) {
				return true;
			}

			@Property
			boolean boxedBooleanParam(@ForAll Boolean aBoolean) {
				return true;
			}

			@Property
			boolean enumParam(@ForAll AnEnum oneTwoThree) {
				return true;
			}

			@Property
			boolean enumParamWithForAllValue(@ForAll("aValue") AnEnum count) {
				return true;
			}

			@Property
			boolean stringParam(@ForAll String aString) {
				return true;
			}

			@Property
			boolean integerList(@ForAll List<Integer> aList) {
				return true;
			}

			@Property
			boolean integerArray(@ForAll Integer[] anArray) {
				return true;
			}

			@Property
			boolean integerSet(@ForAll Set<Integer> aSet) {
				return true;
			}

			@Property
			boolean integerStream(@ForAll Stream<Integer> aStream) {
				return true;
			}

			@Property
			boolean integerOptional(@ForAll Optional<Integer> anOptional) {
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

			@Generate
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

			@Generate("aString")
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

			@Generate
			Arbitrary<String> byMethodName() {
				return Arbitraries.string('x', 'y');
			}

			@Property
			boolean longFromBoxedType(@ForAll("longBetween1and10") long aLong) {
				return true;
			}

			@Generate
			Arbitrary<Long> longBetween1and10() {
				return Arbitraries.longInteger(1L, 10L);
			}

			@Property
			boolean listOfGeneratedStrings(@ForAll("aName") List<String> nameList) {
				return true;
			}

			@Generate("aName")
			Arbitrary<String> aNameForList() {
				return Arbitraries.string('a', 'b', 10).filter(name -> name.length() > 2);
			}

			@Property
			boolean listOfThingWithoutName(@ForAll List<Thing> thingList) {
				return true;
			}

			@Generate()
			Arbitrary<Thing> aThing() {
				return Arbitraries.of(new Thing());
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

			@Generate
			Arbitrary<Object> mockObject() {
				return new MockArbitrary();
			}
		}

	}

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

	private static PropertyMethodArbitraryResolver getProvider(Class container, String methodName, Class<?>... parameterTypes)
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
