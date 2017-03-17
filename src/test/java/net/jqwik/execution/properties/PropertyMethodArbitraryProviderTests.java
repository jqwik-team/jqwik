package net.jqwik.execution.properties;

import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import org.assertj.core.api.Assertions;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Random;

import static net.jqwik.TestDescriptorBuilder.forMethod;

public class PropertyMethodArbitraryProviderTests {

	@Example
	void defaults() throws NoSuchMethodException {
		assertGenerated(Integer.class, "intParam", int.class);
		assertGenerated(Integer.class, "integerParam", Integer.class);
	}

	@Example
	void noDefaultForString() throws NoSuchMethodException {
		PropertyMethodArbitraryProvider provider = getProvider("stringParam", String.class);
		Parameter parameter = getParameter("stringParam");
		Assertions.assertThat(provider.forParameter(parameter)).isEmpty();
	}

	//	@Example
	void listOfKnownType() throws NoSuchMethodException {
		List<Integer> actual = (List<Integer>) assertGenerated(List.class, "integerList", List.class);

	}

	private Object assertGenerated(Class<?> expectedType, String methodName, Class... paramTypes) throws NoSuchMethodException {
		PropertyMethodArbitraryProvider provider = getProvider(methodName, paramTypes);
		Parameter parameter = getParameter(methodName);
		Object actual = generateObject(provider, parameter);
		Assertions.assertThat(actual).isInstanceOf(expectedType);
		return actual;
	}

	private Object generateObject(PropertyMethodArbitraryProvider provider, Parameter parameter) {
		return provider.forParameter(parameter).get().apply(1).apply(new Random());
	}

	private PropertyMethodArbitraryProvider getProvider(String methodName, Class<?>... paramterTypes) throws NoSuchMethodException {
		PropertyMethodDescriptor descriptor = getDescriptor(methodName, paramterTypes);
		return new PropertyMethodArbitraryProvider(descriptor, this);
	}

	private PropertyMethodDescriptor getDescriptor(String methodName, Class... parameterTypes) throws NoSuchMethodException {
		return (PropertyMethodDescriptor) forMethod(PropertyParams.class, methodName, parameterTypes).build();
	}

	private Parameter getParameter(String methodName) {
		return ParameterHelper.getParametersFor(PropertyParams.class, methodName).get(0);
	}

	private static class PropertyParams {
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
