package net.jqwik.execution.properties;

import static net.jqwik.TestDescriptorBuilder.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.*;
import java.util.*;

import javaslang.test.*;
import net.jqwik.api.*;
import net.jqwik.api.properties.*;
import net.jqwik.api.properties.Property;
import net.jqwik.descriptor.*;
import net.jqwik.support.*;

@Group
public class PropertyMethodArbitraryProviderTests {

	private enum Count {
		One,
		Two,
		Three
	}

	@Group
	class Defaults {

		@Example
		void defaults() throws Exception {
			assertGenerated(Integer.class, "intParam", int.class);
			assertGenerated(Integer.class, "integerParam", Integer.class);
			assertGenerated(Count.class, "enumParam", Count.class);
		}

		@Example
		void noDefaultForString() throws Exception {
			assertNoArbitraryProvided(DefaultParams.class, "stringParam", String.class);
		}

		@Example
		void doNotUseDefaultIfForAllHasValue() throws Exception {
			assertNoArbitraryProvided(DefaultParams.class, "enumParamWithForAllValue", Count.class);
		}

		private void assertNoArbitraryProvided(Class<DefaultParams> containerClass, String methodName, Class<?>... paramTypes)
				throws Exception {
			PropertyMethodArbitraryProvider provider = getProvider(containerClass, methodName, paramTypes);
			Parameter parameter = getParameter(containerClass, methodName);
			assertThat(provider.forParameter(parameter)).isEmpty();
		}

		private Object assertGenerated(Class<?> expectedType, String methodName, Class... paramTypes) throws Exception {
			PropertyMethodArbitraryProvider provider = getProvider(DefaultParams.class, methodName, paramTypes);
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
			boolean enumParam(@ForAll Count oneTwoThree) {
				return true;
			}

			@Property
			boolean enumParamWithForAllValue(@ForAll("aValue") Count count) {
				return true;
			}

			@Property
			boolean integerParam(@ForAll Integer anInt) {
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
		}

	}

	@Group
	class ProvidedArbitraries {

		@Example
		void unnamedStringGenerator() throws Exception {
			PropertyMethodArbitraryProvider provider = getProvider(WithUnnamedGenerator.class, "string", String.class);
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
				return Arbitrary.string(Gen.choose('a', 'z'));
			}
		}

		@Example
		void findBoxedTypeGenerator() throws Exception {
			PropertyMethodArbitraryProvider provider = getProvider(WithNamedProviders.class, "longFromBoxedType", long.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "longFromBoxedType");
			Object actual = generateObject(provider, parameter);
			assertThat(actual).isInstanceOf(Long.class);
		}

		@Example
		void findStringGeneratorByName() throws Exception {
			PropertyMethodArbitraryProvider provider = getProvider(WithNamedProviders.class, "string", String.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "string");
			Object actual = generateObject(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void findStringGeneratorByMethodName() throws Exception {
			PropertyMethodArbitraryProvider provider = getProvider(WithNamedProviders.class, "stringByMethodName", String.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "stringByMethodName");
			Object actual = generateObject(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void namedStringGeneratorNotFound() throws Exception {
			PropertyMethodArbitraryProvider provider = getProvider(WithNamedProviders.class, "otherString", String.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "otherString");
			assertThat(provider.forParameter(parameter)).isEmpty();
		}

		private class WithNamedProviders {
			@Property
			boolean string(@ForAll("aString") String aString) {
				return true;
			}

			@Generate("aString")
			Arbitrary<String> aString() {
				return Generator.string('a', 'z');
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
				return Generator.string('x', 'y');
			}

			@Property
			boolean longFromBoxedType(@ForAll("longBetween1and10") long aLong) { return true; }

			@Generate
			Arbitrary<Long> longBetween1and10() {
				return Generator.integer(1L, 10L);
			}

		}

	}

	// @Example
	// void listOfKnownType() throws NoSuchMethodException {
	// List<Integer> actual = (List<Integer>) assertGenerated(List.class, "integerList", List.class);
	// }

	private static Object generateObject(PropertyMethodArbitraryProvider provider, Parameter parameter) {
		return provider.forParameter(parameter).get().apply(1).apply(new Random());
	}

	private static PropertyMethodArbitraryProvider getProvider(Class container, String methodName, Class<?>... parameterTypes)
			throws NoSuchMethodException, IllegalAccessException, InstantiationException {
		PropertyMethodDescriptor descriptor = getDescriptor(container, methodName, parameterTypes);
		return new PropertyMethodArbitraryProvider(descriptor, JqwikReflectionSupport.newInstanceWithDefaultConstructor(container));
	}

	private static PropertyMethodDescriptor getDescriptor(Class container, String methodName, Class... parameterTypes)
			throws NoSuchMethodException {
		return (PropertyMethodDescriptor) forMethod(container, methodName, parameterTypes).build();
	}

	private static Parameter getParameter(Class container, String methodName) {
		return ParameterHelper.getParametersFor(container, methodName).get(0);
	}

}
