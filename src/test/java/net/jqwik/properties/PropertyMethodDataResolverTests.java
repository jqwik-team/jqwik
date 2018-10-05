package net.jqwik.properties;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.support.*;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("unchecked")
@Group
class PropertyMethodDataResolverTests {

	@Example
	void findStringGeneratorByName() {
		PropertyMethodDataResolver provider = getResolver(NamedResolvers.class);
		Method parameter = getMethod(NamedResolvers.class, "string");
		Optional<Iterable<? extends Tuple>> optionalData = provider.forMethod(parameter);
		assertThat(optionalData).isPresent();
		Iterable<Tuple1<String>> data = (Iterable<Tuple1<String>>) optionalData.get();
		assertThat(data).containsExactly(
			Tuple.of("1"),
			Tuple.of("2"),
			Tuple.of("3")
		);
	}

	//	@Example
	void findStringGeneratorByMethodName() throws NoSuchMethodException {
		PropertyMethodDataResolver provider = getResolver(NamedResolvers.class);
		Method parameter = getMethod(NamedResolvers.class, "stringByMethodName");
		Optional<Iterable<? extends Tuple>> optionalData = provider.forMethod(parameter);
		assertThat(optionalData).isPresent();
	}

	//	@Example
	void findWithMoreThanOneParameter() {
		assertThat(false).isTrue();
	}

	//	@Example
	void findGeneratorByMethodNameOutsideGroup() throws NoSuchMethodException {
		PropertyMethodDataResolver provider = getResolver(NamedResolvers.NestedNamedProviders.class);
		Method parameter = getMethod(NamedResolvers.NestedNamedProviders.class, "nestedStringByMethodName");
		Optional<Iterable<? extends Tuple>> optionalData = provider.forMethod(parameter);
		assertThat(optionalData).isPresent();
	}

	//	@Example
	void findGeneratorByNameOutsideGroup() throws NoSuchMethodException {
		PropertyMethodDataResolver provider = getResolver(NamedResolvers.NestedNamedProviders.class);
		Method parameter = getMethod(NamedResolvers.NestedNamedProviders.class, "nestedString");
		Optional<Iterable<? extends Tuple>> optionalData = provider.forMethod(parameter);
		assertThat(optionalData).isPresent();
	}

	//	@Example
	void namedStringGeneratorNotFound() throws NoSuchMethodException {
		PropertyMethodDataResolver provider = getResolver(NamedResolvers.class);
		Method parameter = getMethod(NamedResolvers.class, "otherString");
		assertThat(provider.forMethod(parameter)).isEmpty();
	}

	private class NamedResolvers {
		@Property
		@DataFrom("aString")
		boolean string(@ForAll String aString) {
			return true;
		}

		@Data("aString")
		Iterable<Tuple1<String>>  aString() {
			return Table.of("1", "2", "3");
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
		Iterable<Tuple1<String>> byMethodName() {
			return null;
		}

		@Group
		class NestedNamedProviders {
			@Property
			@DataFrom("byMethodName")
			boolean nestedStringByMethodName(@ForAll String aString) {
				return true;
			}

			@Property
			@DataFrom("aString")
			boolean nestedString(@ForAll String aString) {
				return true;
			}

		}
	}

	private static PropertyMethodDataResolver getResolver(Class<?> container) {
		return new PropertyMethodDataResolver(container, JqwikReflectionSupport.newInstanceWithDefaultConstructor(container));
	}

	private static Method getMethod(Class container, String methodName) {
		return TestHelper.getMethod(container, methodName);
	}

}
