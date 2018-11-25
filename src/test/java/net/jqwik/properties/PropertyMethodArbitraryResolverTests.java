package net.jqwik.properties;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;
import net.jqwik.properties.arbitraries.*;
import net.jqwik.support.*;

import java.lang.annotation.*;
import java.util.*;

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
				createProvider(String.class),
				createProvider(String.class, secondArbitrary)
			);
			PropertyMethodArbitraryResolver resolver = new PropertyMethodArbitraryResolver(
				DefaultParams.class, new DefaultParams(),
				new RegisteredArbitraryResolver(registeredProviders),
				new RegisteredArbitraryConfigurer(Collections.emptyList())
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
				new RegisteredArbitraryConfigurer(Collections.emptyList())
			);
			MethodParameter parameter = getParameter(DefaultParams.class, "aString");
			assertThat(resolver.forParameter(parameter)).containsOnly(firstFit, secondFit, thirdFit);
		}

		@Example
		void allFittingArbitrariesAreConfigured() {
			final List<Arbitrary> configured = new ArrayList<>();
			RegisteredArbitraryConfigurer configurer = new RegisteredArbitraryConfigurer(Collections.emptyList()) {
				@Override
				public Arbitrary<?> configure(Arbitrary<?> arbitrary, TypeUsage targetType) {
					configured.add(arbitrary);
					return arbitrary;
				}
			};

			Arbitrary<String> firstFit = tries -> random -> Shrinkable.unshrinkable("an arbitrary string");
			Arbitrary<String> secondFit = tries -> random -> Shrinkable.unshrinkable("an arbitrary string");
			List<ArbitraryProvider> registeredProviders = Arrays.asList(
				createProvider(String.class, firstFit),
				createProvider(String.class, secondFit)
			);
			PropertyMethodArbitraryResolver resolver = new PropertyMethodArbitraryResolver(
				DefaultParams.class, new DefaultParams(),
				new RegisteredArbitraryResolver(registeredProviders),
				configurer
			);
			MethodParameter parameter = getParameter(DefaultParams.class, "stringOfLength5");
			assertThat(resolver.forParameter(parameter)).containsOnly(firstFit, secondFit);
			assertThat(configured).containsOnly(firstFit, secondFit);
		}

		private ArbitraryProvider createProvider(Class targetClass, Arbitrary<?>... arbitraries) {
			return new ArbitraryProvider() {
				@Override
				public boolean canProvideFor(TypeUsage targetType) {
					return targetType.isAssignableFrom(targetClass);
				}

				@Override
				public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
					return new HashSet<>(Arrays.asList(arbitraries));
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

			@Property
			boolean stringOfLength5(@ForAll @StringLength(5) String aString) {
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
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThat(arbitraries.iterator().next()).isInstanceOf(StringArbitrary.class);
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
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThat(arbitraries.iterator().next()).isInstanceOf(LongArbitrary.class);
		}

		@Example
		void findStringGeneratorByName() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "string");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThat(arbitraries.iterator().next()).isInstanceOf(StringArbitrary.class);
		}

		@Example
		void findStringGeneratorByMethodName() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "stringByMethodName");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThat(arbitraries.iterator().next()).isInstanceOf(StringArbitrary.class);
		}

		@Example
		void providedArbitraryIsConfigured() {
			final List<Arbitrary> configured = new ArrayList<>();
			RegisteredArbitraryConfigurer configurer = new RegisteredArbitraryConfigurer(Collections.emptyList()) {
				@Override
				public Arbitrary<?> configure(Arbitrary<?> arbitrary, TypeUsage targetType) {
					configured.add(arbitrary);
					return arbitrary;
				}
			};

			PropertyMethodArbitraryResolver provider = new PropertyMethodArbitraryResolver(
				WithNamedProviders.class, new WithNamedProviders(),
				new RegisteredArbitraryResolver(Collections.emptyList()),
				configurer
			);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "stringOfLength5ByMethodName");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			Arbitrary<?> arbitrary = arbitraries.iterator().next();
			assertThat(configured).containsOnly(arbitrary);
		}

		@Example
		void findGeneratorByMethodNameOutsideGroup() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.NestedWithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedStringByMethodName");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThat(arbitraries.iterator().next()).isInstanceOf(StringArbitrary.class);
		}

		@Example
		void findGeneratorByNameOutsideGroup() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.NestedWithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedString");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThat(arbitraries.iterator().next()).isInstanceOf(StringArbitrary.class);
		}

		@Example
		void findFirstFitIfNoNameIsGiven() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thingWithoutName");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			Arbitrary<?> firstArbitrary = arbitraries.iterator().next();
			assertThat(firstArbitrary).isInstanceOf(Arbitrary.class);

			assertThat(TestHelper.generateFirst(firstArbitrary)).isInstanceOf(Thing.class);
		}

		@Example
		void findFirstFitOfGenericTypeIfNoNameIsGiven() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "listOfThingWithoutName");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			Arbitrary<?> firstArbitrary = arbitraries.iterator().next();
			assertThat(firstArbitrary).isInstanceOf(Arbitrary.class);

			assertThat(TestHelper.generateFirst(firstArbitrary)).isInstanceOf(List.class);
		}

		@Example
		void findFirstFitIfNoNameIsGivenInOutsideGroup() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.NestedWithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedThing");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			Arbitrary<?> firstArbitrary = arbitraries.iterator().next();
			assertThat(firstArbitrary).isInstanceOf(Arbitrary.class);

			assertThat(TestHelper.generateFirst(firstArbitrary)).isInstanceOf(Thing.class);
		}

		@Example
		void namedStringGeneratorNotFound() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "otherString");
			assertThat(provider.forParameter(parameter)).isEmpty();
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

			@Property
			boolean stringOfLength5ByMethodName(@ForAll("byMethodName") @StringLength(5) String aString) {
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
			boolean thingWithoutName(@ForAll Thing aThing) {
				return true;
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

	private static PropertyMethodArbitraryResolver getResolver(Class<?> container) {
		return new PropertyMethodArbitraryResolver(container, JqwikReflectionSupport.newInstanceWithDefaultConstructor(container));
	}

	private static MethodParameter getParameter(Class container, String methodName) {
		return TestHelper.getParametersFor(container, methodName).get(0);
	}

}
