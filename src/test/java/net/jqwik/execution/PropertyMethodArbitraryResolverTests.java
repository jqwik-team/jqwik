package net.jqwik.execution;

import static net.jqwik.TestDescriptorBuilder.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.providers.*;
import org.assertj.core.data.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.descriptor.*;
import net.jqwik.properties.arbitraries.*;
import net.jqwik.support.*;

@Group
public class PropertyMethodArbitraryResolverTests {

	private static class Thing {

	}

	@Group
	class Defaults {

		@Example
		void defaultProvidersAreUsedIfNothingIsProvided() throws Exception {
			PropertyMethodArbitraryResolver provider = getResolver(DefaultParams.class, "intParam", int.class);
			Parameter parameter = getParameter(DefaultParams.class, "intParam");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(Integer.class);
		}

		@Example
		void doNotUseDefaultIfForAllHasValue() throws Exception {
			PropertyMethodArbitraryResolver resolver = getResolver(DefaultParams.class, "intParamWithForAllValue", int.class);
			Parameter parameter = getParameter(DefaultParams.class, "intParamWithForAllValue");
			assertThat(resolver.forParameter(parameter)).isEmpty();
		}

		@Example
		void useNextDefaultProviderIfFirstDoesNotProvideAnArbitrary() throws Exception {
			PropertyMethodDescriptor descriptor = getDescriptor(DefaultParams.class, "aString", String.class);
			List<ArbitraryProvider> defaultProviders = Arrays.asList(
				createProvider(String.class, null),
				createProvider(String.class, new Arbitrary<String>() {
					@Override
					public RandomGenerator<String> generator(int tries) {
						return random -> Shrinkable.unshrinkable("an arbitrary string");
					}
				})
			);
			PropertyMethodArbitraryResolver resolver = new PropertyMethodArbitraryResolver(descriptor, new DefaultParams(), defaultProviders);
			Parameter parameter = getParameter(DefaultParams.class, "aString");
			Object actual = generateFirst(resolver, parameter);
			assertThat(actual).isEqualTo("an arbitrary string");
		}

		private ArbitraryProvider createProvider(Class targetClass, Arbitrary<?> arbitrary) {
			return new ArbitraryProvider() {
				@Override
				public boolean canProvideFor(GenericType targetType) {
					return targetType.isAssignableFrom(targetClass);
				}

				@Override
				public Arbitrary<?> provideFor(
					GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider
				) {
					return arbitrary;
				}
			};
		}

		private class DefaultParams {
			@Property
			boolean intParam(@ForAll int aValue) {
				return true;
			}

			@Property
			boolean intParamWithForAllValue(@ForAll("someInt") int aValue) {
				return true;
			}

			@Property
			boolean aString(@ForAll String aString) {
				return true;
			}

		}

	}

	@Group
	class ProvidedArbitraries {

		@Example
		void unnamedStringGenerator() throws Exception {
			PropertyMethodArbitraryResolver provider = getResolver(WithUnnamedGenerator.class, "string", String.class);
			Parameter parameter = getParameter(WithUnnamedGenerator.class, "string");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		private class WithUnnamedGenerator {
			@Property
			boolean string(@ForAll String aString) {
				return true;
			}

			@Provide
			Arbitrary<String> aString() {
				return Arbitraries.strings('a', 'z');
			}
		}

		@Example
		void findBoxedTypeGenerator() throws Exception {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class, "longFromBoxedType", long.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "longFromBoxedType");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(Long.class);
		}

		@Example
		void findStringGeneratorByName() throws Exception {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class, "string", String.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "string");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void findStringGeneratorByMethodName() throws Exception {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class, "stringByMethodName", String.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "stringByMethodName");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void findGeneratorByMethodNameOutsideGroup() throws Exception {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.NestedWithNamedProviders.class,
					"nestedStringByMethodName", String.class);
			Parameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedStringByMethodName");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void findGeneratorByNameOutsideGroup() throws Exception {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.NestedWithNamedProviders.class, "nestedString",
					String.class);
			Parameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedString");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void findFirstFitIfNoNameIsGivenInOutsideGroup() throws Exception {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.NestedWithNamedProviders.class, "nestedThing",
					Thing.class);
			Parameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedThing");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(Thing.class);
		}

		@Example
		void namedStringGeneratorNotFound() throws Exception {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class, "otherString", String.class);
			Parameter parameter = getParameter(WithNamedProviders.class, "otherString");
			assertThat(provider.forParameter(parameter)).isEmpty();
		}

		@Example
		void findFirstFitIfNoNameIsGiven() throws Exception {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class, "listOfThingWithoutName", List.class);
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
				return Arbitraries.strings('a', 'z');
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
				return Arbitraries.strings('x', 'y');
			}

			@Property
			boolean longFromBoxedType(@ForAll("longBetween1and10") long aLong) {
				return true;
			}

			@Provide
			Arbitrary<Long> longBetween1and10() {
				return Arbitraries.longs(1L, 10L);
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
			PropertyMethodArbitraryResolver provider = getResolver(WithConfiguration.class, "aNullableInteger", Integer.class);
			Parameter parameter = getParameter(WithConfiguration.class, "aNullableInteger");
			IntegerArbitrary integerArbitrary = (IntegerArbitrary) provider.forParameter(parameter).get().inner();

			assertThat(integerArbitrary.getNullProbability()).isCloseTo(0.42, Offset.offset(0.01));
		}

		@Example
		void configureIsCalledOnProvidedArbitrary() throws Exception {
			PropertyMethodArbitraryResolver provider = getResolver(WithConfiguration.class, "aNullableMock", Object.class);
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

	private static RandomGenerator<Object> getGenerator(PropertyMethodArbitraryResolver provider, Parameter parameter) {
		return provider.forParameter(parameter).get().generator(1);
	}

	@SuppressWarnings("unchecked")
	private <T extends Collection> T generateCollection(PropertyMethodArbitraryResolver provider, Parameter parameter) {
		RandomGenerator<Object> generator = getGenerator(provider, parameter);
		return (T) TestHelper.generateUntil(generator, o -> {
			T c = (T) o;
			return !c.isEmpty();
		});
	}

	private static Object generateFirst(PropertyMethodArbitraryResolver provider, Parameter parameter) {
		return TestHelper.generateFirst(provider.forParameter(parameter).get());
	}

	private static PropertyMethodArbitraryResolver getResolver(Class<?> container, String methodName, Class<?>... parameterTypes) {
		PropertyMethodDescriptor descriptor = getDescriptor(container, methodName, parameterTypes);
		return new PropertyMethodArbitraryResolver(descriptor, JqwikReflectionSupport.newInstanceWithDefaultConstructor(container));
	}

	private static PropertyMethodDescriptor getDescriptor(Class container, String methodName, Class... parameterTypes) {
		return (PropertyMethodDescriptor) forMethod(container, methodName, parameterTypes).build();
	}

	private static Parameter getParameter(Class container, String methodName) {
		return TestHelper.getParametersFor(container, methodName).get(0);
	}

}
