package net.jqwik.execution.properties;

import javaslang.test.Arbitrary;
import javaslang.test.Gen;
import net.jqwik.api.*;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import net.jqwik.support.JqwikReflectionSupport;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Random;

import static net.jqwik.TestDescriptorBuilder.forMethod;
import static org.assertj.core.api.Assertions.assertThat;

@Group
public class PropertyMethodArbitraryProviderTests {

	@Group
	static class Defaults {

		@Example
		void defaults() throws Exception {
			assertGenerated(Integer.class, "intParam", int.class);
			assertGenerated(Integer.class, "integerParam", Integer.class);
		}

		@Example
		void noDefaultForString() throws Exception {
			PropertyMethodArbitraryProvider provider = getProvider(DefaultParams.class, "stringParam", String.class);
			Parameter parameter = getParameter(DefaultParams.class, "stringParam");
			assertThat(provider.forParameter(parameter)).isEmpty();
		}

		private static Object assertGenerated(Class<?> expectedType, String methodName, Class... paramTypes) throws Exception {
			PropertyMethodArbitraryProvider provider = getProvider(DefaultParams.class, methodName, paramTypes);
			Parameter parameter = getParameter(DefaultParams.class, methodName);
			Object actual = generateObject(provider, parameter);
			assertThat(actual).isInstanceOf(expectedType);
			return actual;
		}

		private static class DefaultParams {
			@Property
			boolean intParam(@ForAll int anInt) {
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
	static class ProvidedArbitraries {

		@Example
		void stringArbitrary() throws Exception {
			PropertyMethodArbitraryProvider provider = getProvider(WithProviders.class, "string", String.class);
			Parameter parameter = getParameter(WithProviders.class, "string");
			Object actual = generateObject(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		private static class WithProviders {
			@Property
			boolean string(@ForAll String aString) { return true; }

			@Generate
			Arbitrary<String> aString() {
				return Arbitrary.string(Gen.choose('a', 'z'));
			}
		}

	}

	//	@Example
	//	void listOfKnownType() throws NoSuchMethodException {
	//		List<Integer> actual = (List<Integer>) assertGenerated(List.class, "integerList", List.class);
	//	}


	private static Object generateObject(PropertyMethodArbitraryProvider provider, Parameter parameter) {
		return provider.forParameter(parameter).get().apply(1).apply(new Random());
	}

	private static PropertyMethodArbitraryProvider getProvider(Class container, String methodName,
															   Class<?>... parameterTypes) throws NoSuchMethodException, IllegalAccessException, InstantiationException {
		PropertyMethodDescriptor descriptor = getDescriptor(container, methodName, parameterTypes);
		return new PropertyMethodArbitraryProvider(descriptor, JqwikReflectionSupport.newInstance(container));
	}

	private static PropertyMethodDescriptor getDescriptor(Class container, String methodName, Class... parameterTypes) throws NoSuchMethodException {
		return (PropertyMethodDescriptor) forMethod(container, methodName, parameterTypes).build();
	}

	private static Parameter getParameter(Class container, String methodName) {
		return ParameterHelper.getParametersFor(container, methodName).get(0);
	}

}
