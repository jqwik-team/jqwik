package net.jqwik.properties;

import java.util.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.support.*;

import static org.assertj.core.api.Assertions.*;

@Group
class PropertyMethodDataResolverTests {

	//	@Example
	void findStringGeneratorByName() {
		PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
		MethodParameter parameter = getParameter(WithNamedProviders.class, "string");
		Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
		assertThat(arbitraries.iterator().next()).isInstanceOf(StringArbitrary.class);
	}

	//	@Example
	void findStringGeneratorByMethodName() {
		PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
		MethodParameter parameter = getParameter(WithNamedProviders.class, "stringByMethodName");
		Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
		assertThat(arbitraries.iterator().next()).isInstanceOf(StringArbitrary.class);
	}

	//	@Example
	void findWithMoreThanOneParameter() {
		assertThat(false).isTrue();
	}

	//	@Example
	void findGeneratorByMethodNameOutsideGroup() {
		PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.NestedWithNamedProviders.class);
		MethodParameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedStringByMethodName");
		Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
		assertThat(arbitraries.iterator().next()).isInstanceOf(StringArbitrary.class);
	}

	//	@Example
	void findGeneratorByNameOutsideGroup() {
		PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.NestedWithNamedProviders.class);
		MethodParameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedString");
		Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
		assertThat(arbitraries.iterator().next()).isInstanceOf(StringArbitrary.class);
	}

	//	@Example
	void namedStringGeneratorNotFound() {
		PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
		MethodParameter parameter = getParameter(WithNamedProviders.class, "otherString");
		assertThat(provider.forParameter(parameter)).isEmpty();
	}

	private class WithNamedProviders {
		@Property
		@DataFrom("aString")
		boolean string(@ForAll String aString) {
			return true;
		}

		@Data("aString")
		Iterable<String> aString() {
			return null;
		}

		@Property
		@DataFrom("otherString")
		boolean otherString(@ForAll String aString) {
			return true;
		}

		@Property
		@DataFrom("byMethodName")
		boolean stringByMethodName(String aString) {
			return true;
		}

		@Data
		Iterable<String> byMethodName() {
			return null;
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

		}
	}

	private static PropertyMethodArbitraryResolver getResolver(Class<?> container) {
		return new PropertyMethodArbitraryResolver(container, JqwikReflectionSupport.newInstanceWithDefaultConstructor(container));
	}

	private static MethodParameter getParameter(Class container, String methodName) {
		return TestHelper.getParametersFor(container, methodName).get(0);
	}

}
