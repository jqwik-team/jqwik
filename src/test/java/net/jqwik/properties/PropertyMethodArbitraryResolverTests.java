package net.jqwik.properties;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.execution.*;
import net.jqwik.properties.arbitraries.*;
import net.jqwik.support.*;

import java.util.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

@Group
class PropertyMethodArbitraryResolverTests {

	private static class Thing {

	}

	@Group
	class RegisteredArbitraryResolvers {

		@Example
		void defaultProvidersAreUsedIfNothingIsProvided() {
			PropertyMethodArbitraryResolver provider = getResolver(DefaultParams.class);
			MethodParameter parameter = getParameter(DefaultParams.class, "intParam");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThat(arbitraries).hasSize(1);
			assertThat(arbitraries.iterator().next()).isInstanceOf(DefaultIntegerArbitrary.class);
		}

		@Example
		void doNotConsiderRegisteredProvidersIfForAllHasValue() {
			PropertyMethodArbitraryResolver resolver = getResolver(DefaultParams.class);
			MethodParameter parameter = getParameter(DefaultParams.class, "intParamWithForAllValue");
			assertThat(resolver.forParameter(parameter)).isEmpty();
		}

		@Example
		void useNextRegisteredProviderIfFirstDoesNotProvideAnArbitrary() {
			Arbitrary<String> secondArbitrary = tries -> random -> Shrinkable.unshrinkable("an arbitrary string");
			List<ArbitraryProvider> registeredProviders = Arrays.asList(
				createProvider(String.class, null),
				createProvider(String.class, secondArbitrary)
			);
			PropertyMethodArbitraryResolver resolver = new PropertyMethodArbitraryResolver(
				DefaultParams.class, new DefaultParams(),
				new RegisteredArbitraryResolver(registeredProviders),
				Collections.emptyList()
			);
			MethodParameter parameter = getParameter(DefaultParams.class, "aString");
			assertThat(resolver.forParameter(parameter).iterator().next()).isSameAs(secondArbitrary);
		}

		@Example
		void resolveSeveralFittingArbitraries() {
			Arbitrary<String> doesNotFitFit = tries -> random -> Shrinkable.unshrinkable("an arbitrary string");
			Arbitrary<String> firstFit = tries -> random -> Shrinkable.unshrinkable("an arbitrary string");
			Arbitrary<String> secondFit = tries -> random -> Shrinkable.unshrinkable("an arbitrary string");
			Arbitrary<String> thirdFit = tries -> random -> Shrinkable.unshrinkable("an arbitrary string");
			List<ArbitraryProvider> registeredProviders = Arrays.asList(
				createProvider(String.class, firstFit),
				createProvider(Integer.class, doesNotFitFit),
				createProvider(String.class, secondFit),
				createProvider(String.class, thirdFit)
			);
			PropertyMethodArbitraryResolver resolver = new PropertyMethodArbitraryResolver(
				DefaultParams.class, new DefaultParams(),
				new RegisteredArbitraryResolver(registeredProviders),
				Collections.emptyList()
			);
			MethodParameter parameter = getParameter(DefaultParams.class, "aString");
			assertThat(resolver.forParameter(parameter)).hasSize(3);
			assertThat(resolver.forParameter(parameter)).containsOnly(firstFit, secondFit, thirdFit);
		}

		private ArbitraryProvider createProvider(Class targetClass, Arbitrary<?> arbitrary) {
			return new ArbitraryProvider() {
				@Override
				public boolean canProvideFor(TypeUsage targetType) {
					return targetType.isAssignableFrom(targetClass);
				}

				@Override
				public Arbitrary<?> provideFor(TypeUsage targetType, Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider) {
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
		void unnamedStringGenerator() {
			PropertyMethodArbitraryResolver provider = getResolver(WithUnnamedGenerator.class);
			MethodParameter parameter = getParameter(WithUnnamedGenerator.class, "string");
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
				return Arbitraries.strings().withCharRange('a', 'z');
			}
		}

		@Example
		void findBoxedTypeGenerator() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "longFromBoxedType");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(Long.class);
		}

		@Example
		void findStringGeneratorByName() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "string");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void findStringGeneratorByMethodName() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "stringByMethodName");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void findGeneratorByMethodNameOutsideGroup() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.NestedWithNamedProviders.class
			);
			MethodParameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedStringByMethodName");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void findGeneratorByNameOutsideGroup() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.NestedWithNamedProviders.class
			);
			MethodParameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedString");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(String.class);
		}

		@Example
		void findFirstFitIfNoNameIsGivenInOutsideGroup() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.NestedWithNamedProviders.class
			);
			MethodParameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedThing");
			Object actual = generateFirst(provider, parameter);
			assertThat(actual).isInstanceOf(Thing.class);
		}

		@Example
		void namedStringGeneratorNotFound() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "otherString");
			assertThat(provider.forParameter(parameter)).isEmpty();
		}

		@Example
		void findFirstFitIfNoNameIsGiven() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "listOfThingWithoutName");
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
				return Arbitraries.strings().withCharRange('a', 'z');
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
				return Arbitraries.strings().withCharRange('x', 'y');
			}

			@Property
			boolean longFromBoxedType(@ForAll("longBetween1and10") long aLong) {
				return true;
			}

			@Provide
			Arbitrary<Long> longBetween1and10() {
				return Arbitraries.longs().between(1L, 10L);
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

	private static RandomGenerator<?> getGenerator(PropertyMethodArbitraryResolver provider, MethodParameter parameter) {
		return provider.forParameter(parameter).iterator().next().generator(1);
	}

	@SuppressWarnings("unchecked")
	private <T extends Collection> T generateCollection(PropertyMethodArbitraryResolver provider, MethodParameter parameter) {
		RandomGenerator<?> generator = getGenerator(provider, parameter);
		return (T) TestHelper.generateUntil(generator, o -> {
			T c = (T) o;
			return !c.isEmpty();
		});
	}

	private static Object generateFirst(PropertyMethodArbitraryResolver provider, MethodParameter parameter) {
		return TestHelper.generateFirst(provider.forParameter(parameter).iterator().next());
	}

	private static PropertyMethodArbitraryResolver getResolver(Class<?> container) {
		return new PropertyMethodArbitraryResolver(container, JqwikReflectionSupport.newInstanceWithDefaultConstructor(container));
	}

	private static MethodParameter getParameter(Class container, String methodName) {
		return TestHelper.getParametersFor(container, methodName).get(0);
	}

}
