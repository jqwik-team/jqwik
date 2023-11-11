package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.engine.*;
import net.jqwik.engine.support.*;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("unchecked")
@Group
class PropertyMethodDataResolverTests {

	@Group
	class SingleParameter {
		@Example
		void findStringGeneratorByName() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.class);
			Method parameter = getMethod(NamedResolvers.class, "string");
			Optional<Iterable<? extends Tuple>> optionalData = resolver.forMethod(parameter);

			Iterable<Tuple1<String>> data = (Iterable<Tuple1<String>>) optionalData.get();
			assertThat(data).containsExactly(
				Tuple.of("1"),
				Tuple.of("2"),
				Tuple.of("3")
			);
		}

		@Example
		void findStringGeneratorByMethodName() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.class);
			Method parameter = getMethod(NamedResolvers.class, "stringByMethodName");
			Optional<Iterable<? extends Tuple>> optionalData = resolver.forMethod(parameter);

			Iterable<Tuple1<String>> data = (Iterable<Tuple1<String>>) optionalData.get();
			assertThat(data).containsExactly(
				Tuple.of("4"),
				Tuple.of("5"),
				Tuple.of("6")
			);
		}

		@Example
		void findGeneratorByMethodNameOutsideGroup() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.NestedNamedProviders.class);
			Method parameter = getMethod(NamedResolvers.NestedNamedProviders.class, "nestedStringByMethodName");

			Optional<Iterable<? extends Tuple>> optionalData = resolver.forMethod(parameter);
			Iterable<Tuple1<String>> data = (Iterable<Tuple1<String>>) optionalData.get();
			assertThat(data).containsExactly(
				Tuple.of("4"),
				Tuple.of("5"),
				Tuple.of("6")
			);
		}

		@Example
		void findGeneratorByNameOutsideGroup() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.NestedNamedProviders.class);
			Method parameter = getMethod(NamedResolvers.NestedNamedProviders.class, "nestedString");
			Optional<Iterable<? extends Tuple>> optionalData = resolver.forMethod(parameter);

			Iterable<Tuple1<String>> data = (Iterable<Tuple1<String>>) optionalData.get();
			assertThat(data).containsExactly(
				Tuple.of("1"),
				Tuple.of("2"),
				Tuple.of("3")
			);
		}

		@Example
		void noFromDataAnnotation() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.class);
			Method parameter = getMethod(NamedResolvers.class, "noFromData");
			assertThat(resolver.forMethod(parameter)).isEmpty();
		}

		@Example
		void unknownGeneratorName() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.class);
			Method parameter = getMethod(NamedResolvers.class, "unknownGeneratorName");
			assertThatThrownBy(() ->resolver.forMethod(parameter)).isInstanceOf(JqwikException.class);
		}

	}

	@Group
	class MoreThanOneParameter {
		@Example
		void twoParameters() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.class);
			Method parameter = getMethod(NamedResolvers.class, "twoParameters");
			Optional<Iterable<? extends Tuple>> optionalData = resolver.forMethod(parameter);

			Iterable<Tuple2<String, Integer>> data = (Iterable<Tuple2<String, Integer>>) optionalData.get();
			assertThat(data).containsExactly(
				Tuple.of("4", 4),
				Tuple.of("5", 5),
				Tuple.of("6", 6)
			);
		}

		@Example
		void eightParameters() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.class);
			Method parameter = getMethod(NamedResolvers.class, "eightParameters");
			Optional<Iterable<? extends Tuple>> optionalData = resolver.forMethod(parameter);

			Iterable<Tuple8> data = (Iterable<Tuple8>) optionalData.get();
			assertThat(data).containsExactly(
				Tuple.of(1, 2, 3, 4, 5, 6, 7, 8),
				Tuple.of(11, 22, 33, 44, 55, 66, 77, 88)
			);
		}
	}


	private class NamedResolvers {
		@Property
		@FromData("aString")
		boolean string(@ForAll String aString) {
			return true;
		}

		@Data("aString")
		Iterable<Tuple1<String>>  aString() {
			return Table.of("1", "2", "3");
		}

		@Property
		boolean noFromData(@ForAll String aString) {
			return true;
		}

		@Property
		@FromData("unknown")
		boolean unknownGeneratorName(@ForAll String aString) {
			return true;
		}

		@Property
		@FromData("byMethodName")
		boolean stringByMethodName(String aString) {
			return true;
		}

		@Data
		Iterable<? extends Tuple> byMethodName() {
			return Table.of(
				Tuple.of("4"),
				Tuple.of("5"),
				Tuple.of("6")
			);
		}

		@Property
		@FromData("twos")
		boolean twoParameters(@ForAll String aString) {
			return true;
		}

		@Data
		Iterable twos() {
			return Table.of(
				Tuple.of("4", 4),
				Tuple.of("5", 5),
				Tuple.of("6", 6)
			);
		}

		@Property
		@FromData("eight")
		boolean eightParameters(@ForAll String aString) {
			return true;
		}

		@Data
		Iterable eight() {
			return Table.of(
				Tuple.of(1, 2, 3, 4, 5, 6, 7, 8),
				Tuple.of(11, 22, 33, 44, 55, 66, 77, 88)
			);
		}

		@Group
		class NestedNamedProviders {
			@Property
			@FromData("byMethodName")
			boolean nestedStringByMethodName(@ForAll String aString) {
				return true;
			}

			@Property
			@FromData("aString")
			boolean nestedString(@ForAll String aString) {
				return true;
			}

		}
	}

	private static PropertyMethodDataResolver getResolver(Class<?> container) {
		return new PropertyMethodDataResolver(
			container,
			JqwikReflectionSupport.newInstancesWithDefaultConstructor(container)
		);
	}

	private static Method getMethod(Class container, String methodName) {
		return TestHelper.getMethod(container, methodName);
	}

}
