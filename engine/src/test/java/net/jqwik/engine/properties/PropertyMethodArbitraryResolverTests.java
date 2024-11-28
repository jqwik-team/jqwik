package net.jqwik.engine.properties;

import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.support.*;
import net.jqwik.testing.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

@Group
class PropertyMethodArbitraryResolverTests {

	private static class Thing {
		@Override
		public String toString() {
			return "a Thing";
		}
	}

	private class ThingArbitrary extends ArbitraryDecorator<Thing> {

		@Override
		protected Arbitrary<Thing> arbitrary() {
			return  Arbitraries.just(new Thing());
		}
	}



	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Provide
	@interface MyProvide {

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
			Arbitrary<String> secondArbitrary = Arbitraries.fromGenerator(random -> Shrinkable.unshrinkable("an arbitrary string"));
			List<ArbitraryProvider> registeredProviders = Arrays.asList(
				createProvider(String.class),
				createProvider(String.class, secondArbitrary)
			);
			PropertyMethodArbitraryResolver resolver = new PropertyMethodArbitraryResolver(
				asList(new DefaultParams()),
				new RegisteredArbitraryResolver(registeredProviders),
				new RegisteredArbitraryConfigurer(Collections.emptyList())
			);
			MethodParameter parameter = getParameter(DefaultParams.class, "aString");
			assertThat(resolver.forParameter(parameter).iterator().next()).isSameAs(secondArbitrary);
		}

		@Example
		void resolveSeveralFittingArbitraries() {
			Arbitrary<String> doesNotFitFit = Arbitraries.fromGenerator(random -> Shrinkable.unshrinkable("an arbitrary string"));
			Arbitrary<String> firstFit = Arbitraries.fromGenerator(random -> Shrinkable.unshrinkable("an arbitrary string"));
			Arbitrary<String> secondFit = Arbitraries.fromGenerator(random -> Shrinkable.unshrinkable("an arbitrary string"));
			Arbitrary<String> thirdFit = Arbitraries.fromGenerator(random -> Shrinkable.unshrinkable("an arbitrary string"));
			List<ArbitraryProvider> registeredProviders = Arrays.asList(
				createProvider(String.class, firstFit),
				createProvider(Integer.class, doesNotFitFit),
				createProvider(String.class, secondFit),
				createProvider(String.class, thirdFit)
			);
			PropertyMethodArbitraryResolver resolver = new PropertyMethodArbitraryResolver(
				asList(new DefaultParams()),
				new RegisteredArbitraryResolver(registeredProviders),
				new RegisteredArbitraryConfigurer(Collections.emptyList())
			);
			MethodParameter parameter = getParameter(DefaultParams.class, "aString");
			assertThat(resolver.forParameter(parameter)).containsOnly(firstFit, secondFit, thirdFit);
		}

		@Example
		void allFittingArbitrariesAreConfigured() {
			final List<Arbitrary<?>> configured = new ArrayList<>();
			RegisteredArbitraryConfigurer configurer = new RegisteredArbitraryConfigurer(Collections.emptyList()) {
				@Override
				public Arbitrary<?> configure(Arbitrary<?> arbitrary, TypeUsage targetType) {
					configured.add(arbitrary);
					return arbitrary;
				}
			};

			Arbitrary<String> firstFit = Arbitraries.fromGenerator(random -> Shrinkable.unshrinkable("an arbitrary string"));
			Arbitrary<String> secondFit = Arbitraries.fromGenerator(random -> Shrinkable.unshrinkable("an arbitrary string"));
			List<ArbitraryProvider> registeredProviders = Arrays.asList(
				createProvider(String.class, firstFit),
				createProvider(String.class, secondFit)
			);
			PropertyMethodArbitraryResolver resolver = new PropertyMethodArbitraryResolver(
				asList(new DefaultParams()),
				new RegisteredArbitraryResolver(registeredProviders),
				configurer
			);
			MethodParameter parameter = getParameter(DefaultParams.class, "stringOfLength5");
			assertThat(resolver.forParameter(parameter)).containsOnly(firstFit, secondFit);
			assertThat(configured).containsOnly(firstFit, secondFit);
		}

		@Example
		void whenConfigurerReturnsNullArbitraryIsFilteredOut() {

			RegisteredArbitraryConfigurer configurer = new RegisteredArbitraryConfigurer(Collections.emptyList()) {
				@Override
				public Arbitrary<?> configure(Arbitrary<?> arbitrary, TypeUsage targetType) {
					Object value = arbitrary.sample();
					if (value.equals("filter out")) {
						return null;
					}
					return arbitrary;
				}
			};

			Arbitrary<String> firstFit = Arbitraries.fromGenerator(random -> Shrinkable.unshrinkable("an arbitrary string"));
			Arbitrary<String> secondFit = Arbitraries.fromGenerator(random -> Shrinkable.unshrinkable("an arbitrary string"));
			Arbitrary<String> filterOut = Arbitraries.fromGenerator(random -> Shrinkable.unshrinkable("filter out"));

			List<ArbitraryProvider> registeredProviders = Arrays.asList(
				createProvider(String.class, firstFit),
				createProvider(String.class, secondFit),
				createProvider(String.class, filterOut)
			);

			PropertyMethodArbitraryResolver resolver = new PropertyMethodArbitraryResolver(
				asList(new DefaultParams()),
				new RegisteredArbitraryResolver(registeredProviders),
				configurer
			);
			MethodParameter parameter = getParameter(DefaultParams.class, "stringOfLength5");
			assertThat(resolver.forParameter(parameter)).containsOnly(firstFit, secondFit);
		}

		private ArbitraryProvider createProvider(Class<?> targetClass, Arbitrary<?>... arbitraries) {
			return new ArbitraryProvider() {
				@Override
				public boolean canProvideFor(TypeUsage targetType) {
					return targetType.isAssignableFrom(targetClass);
				}

				@Override
				public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
					return new LinkedHashSet<>(asList(arbitraries));
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
		void findBoxedTypeGenerator() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "longFromBoxedType");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThat(arbitraries.iterator().next()).isInstanceOf(LongArbitrary.class);
		}

		@Example
		void providerMethodCanHaveWildcardArbitrary() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thingFromWildcard");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThingArbitrary(arbitraries.iterator().next());
		}

		@Example
		void providerMethodCanReturnArbitrarySubtype() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thingFromArbitrarySubtype");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThingArbitrary(arbitraries.iterator().next());
		}

		@Example
		void providerMethodCanHaveTypeUsageParameter() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thingWithTypeUsage");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThingArbitrary(arbitraries.iterator().next());
		}

		@SuppressWarnings("unchecked")
		@Example
		void providerMethodCanHaveForAllParameters(@ForAll Random random) {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "tuple2WithThingAndString");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			RandomGenerator<Tuple2<Thing, String>> generator =
				(RandomGenerator<Tuple2<Thing, String>>) arbitraries.iterator().next().generator(100);
			TestingSupport.assertAllGenerated(generator, random, aTuple -> {
				assertThat(aTuple).isInstanceOf(Tuple2.class);
				assertThat(aTuple.get1()).isInstanceOf(Thing.class);
				assertThat(aTuple.get2()).isInstanceOf(String.class);
				assertThat(aTuple.get2()).hasSize(2);
			});
		}

		@Example
		void findGeneratorByName() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thing");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThingArbitrary(arbitraries.iterator().next());
		}

		@Example
		void failWhenProviderMethodDoesNotReturnArbitrary() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "providerDoesNotReturnArbitrary");
			assertThat(provider.forParameter(parameter)).isEmpty();
		}

		@Example
		void findGeneratorBySupplier() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thingBySupplier");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThingArbitrary(arbitraries.iterator().next());
		}

		@Example
		void failWithNullSupplier() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thingByNullSupplier");
			assertThatThrownBy(() -> provider.forParameter(parameter)).isInstanceOf(JqwikException.class);
		}

		@Example
		void findGeneratorByNameInFromAnnotation() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thingFrom");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThingArbitrary(arbitraries.iterator().next());
		}

		@Example
		void findGeneratorBySupplierInFromAnnotation() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thingFromSupplier");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThingArbitrary(arbitraries.iterator().next());
		}

		@Example
		void findGeneratorByNameInFromAnnotationOfTypeParameter(@ForAll Random random) {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "listOfThingFrom");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			Arbitrary<?> listOfThingsArbitrary = arbitraries.iterator().next();
			List<?> listOfThings = (List<?>) listOfThingsArbitrary.generator(10, true).next(random).value();
			assertThat(listOfThings).allMatch(aThing -> aThing instanceof Thing);
		}

		@Example
		void findGeneratorByMethodName() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thingByMethodName");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThat(arbitraries.iterator().next()).isInstanceOf(Arbitrary.class);
		}

		@Example
		void providedArbitraryIsConfigured() {
			final List<Arbitrary<?>> configured = new ArrayList<>();
			RegisteredArbitraryConfigurer configurer = new RegisteredArbitraryConfigurer(Collections.emptyList()) {
				@Override
				public Arbitrary<?> configure(Arbitrary<?> arbitrary, TypeUsage targetType) {
					configured.add(arbitrary);
					return arbitrary;
				}
			};

			PropertyMethodArbitraryResolver provider = new PropertyMethodArbitraryResolver(
				asList(new WithNamedProviders()),
				new RegisteredArbitraryResolver(Collections.emptyList()),
				configurer
			);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thingWithNullByMethodName");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			Arbitrary<?> arbitrary = arbitraries.iterator().next();
			assertThat(configured).containsOnly(arbitrary);
		}

		@Example
		void findGeneratorByMethodNameOutsideGroup() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.NestedWithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedThingByMethodName");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThingArbitrary(arbitraries.iterator().next());
		}

		@Example
		void findGeneratorByNameOutsideGroup(@ForAll Random random) {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.NestedWithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.NestedWithNamedProviders.class, "nestedThing");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThingArbitrary(arbitraries.iterator().next());
		}

		@Example
		void findGeneratorByNameWithProvideAnnotationInSuperclass() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thingFromProvideAnnotatedInSuperclass");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThingArbitrary(arbitraries.iterator().next());
		}

		@Example
		void findGeneratorByNameWithProvideInMetaAnnotation() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thingFromProvideInMetaAnnotation");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			assertThingArbitrary(arbitraries.iterator().next());
		}

		@Example
		void moreThanOneGeneratorSpecifiedFails() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "thingFromEverything");
			assertThatThrownBy(() -> provider.forParameter(parameter)).isInstanceOf(JqwikException.class);
		}

		@Example
		void namedGeneratorNotFound() {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "otherThing");
			assertThat(provider.forParameter(parameter)).isEmpty();
		}

		@Example
		void provideAnnotationCanHaveIgnoreExceptionsAttribute(@ForAll Random random) {
			PropertyMethodArbitraryResolver provider = getResolver(WithNamedProviders.class);
			MethodParameter parameter = getParameter(WithNamedProviders.class, "integersFromProvideMethodWithIgnoreExceptions");
			Set<Arbitrary<?>> arbitraries = provider.forParameter(parameter);
			RandomGenerator<Integer> generator =
				(RandomGenerator<Integer>) arbitraries.iterator().next().generator(100);
			TestingSupport.assertAllGenerated(generator, random, anInt -> {
				assertThat(anInt % 3).isEqualTo(0);
			});
		}

		private void assertThingArbitrary(Arbitrary<?> arbitrary) {
			Thing aThing = (Thing) arbitrary.generator(10, true).next(SourceOfRandomness.current()).value();
			assertThat(aThing).isInstanceOf(Thing.class);
		}

		private abstract class AbstractNamedProviders {
			@Provide
			abstract Arbitrary<Thing> thingFromSuper();
		}

		private class WithNamedProviders extends AbstractNamedProviders {
			@Property
			boolean thingWithTypeUsage(@ForAll("thingProviderWithTypeUsage") Thing aThing) {
				return true;
			}

			@Provide
			Arbitrary<Thing> thingProviderWithTypeUsage(TypeUsage parameter) {
				assertThat(parameter.isOfType(Thing.class));
				return Arbitraries.just(new Thing());
			}

			@Property
			boolean thing(@ForAll("aThingByValue") Thing aThing) {
				return true;
			}

			@Provide("aThingByValue")
			Arbitrary<Thing> aThingy() {
				return Arbitraries.just(new Thing());
			}

			@Property
			boolean thingFromWildcard(@ForAll("aThingFromWildcard") Thing aThing) {
				return true;
			}

			@Provide
			Arbitrary<?> aThingFromWildcard() {
				return Arbitraries.just(new Thing());
			}

			@Property
			boolean thingFromArbitrarySubtype(@ForAll("aThingFromArbitrarySubtype") Thing aThing) {
				return true;
			}

			@Provide
			ThingArbitrary aThingFromArbitrarySubtype() {
				return new ThingArbitrary();
			}

			@Property
			boolean thingBySupplier(@ForAll(supplier = ThingSupplier.class) Thing aThing) {
				return true;
			}

			class ThingSupplier implements ArbitrarySupplier<Thing> {
				@Override
				public Arbitrary<Thing> get() {
					return Arbitraries.just(new Thing());
				}
			}

			@Property
			boolean thingByNullSupplier(@ForAll(supplier = ThingNullSupplier.class) Thing aThing) {
				return true;
			}

			class ThingNullSupplier implements ArbitrarySupplier<Thing> {
				@Override
				public Arbitrary<Thing> get() {
					return null;
				}
			}

			@Property
			boolean otherThing(@ForAll("unknown ref") Thing aThing) {
				return true;
			}

			@Property
			boolean thingByMethodName(@ForAll("byMethodName") Thing aString) {
				return true;
			}

			@Property
			boolean thingWithNullByMethodName(@ForAll("byMethodName") @WithNull Thing aThing) {
				return true;
			}

			@Provide
			Arbitrary<Thing> byMethodName() {
				return Arbitraries.just(new Thing());
			}

			@Property
			boolean thingFromEverything(
				@ForAll(value = "aThingy", supplier = ThingSupplier.class)
				@From(value = "aThingy", supplier = ThingSupplier.class)
				Thing aThing
			) {
				return true;
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
			boolean thingFromProvideAnnotatedInSuperclass(@ForAll("thingFromSuper") Thing aString) {
				return true;
			}

			@Override
			Arbitrary<Thing> thingFromSuper() {
				return Arbitraries.just(new Thing());
			}

			@Property
			boolean thingFromProvideInMetaAnnotation(@ForAll("thingWithMetaAnnotation") Thing aThing) {
				return true;
			}

			@MyProvide
			Arbitrary<Thing> thingWithMetaAnnotation() {
				return Arbitraries.just(new Thing());
			}

			@Property
			boolean thingFrom(@ForAll @From("aThing") Thing t) {
				return true;
			}

			@Property
			boolean thingFromSupplier(@ForAll @From(supplier = ThingSupplier.class) Thing t) {
				return true;
			}

			@Property
			boolean listOfThingFrom(@ForAll @Size(1) List<@From("aThing") Thing> l) {
				return true;
			}

			@Provide
			Arbitrary<Thing> aThing() {
				return Arbitraries.just(new Thing());
			}

			@Property
			boolean providerDoesNotReturnArbitrary(@ForAll("notAnArbitrary") Thing aThing) {
				return true;
			}

			@Provide
			Thing notAnArbitrary() {
				return new Thing();
			}

			@Group
			class NestedWithNamedProviders {
				@Property
				boolean nestedThingByMethodName(@ForAll("byMethodName") Thing aThing) {
					return true;
				}

				@Property
				boolean nestedThing(@ForAll("aThingByValue") Thing aThing) {
					return true;
				}

			}

			@Property
			boolean tuple2WithThingAndString(@ForAll("tuple2WithThingAndStringProvider") Tuple2<Thing, String> aTuple) {
				return true;
			}

			@Provide
			Arbitrary<Tuple2<Thing, String>> tuple2WithThingAndStringProvider(
				@ForAll("aThing") Thing aThing,
				@ForAll @StringLength(2) String aString,
				TypeUsage targetType
			) {
				assertThat(targetType.isOfType(Tuple2.class)).isTrue();
				return Arbitraries.just(Tuple.of(aThing, aString));
			}

			@Property
			boolean integersFromProvideMethodWithIgnoreExceptions(@ForAll("integerDivisibleBy3") int anIntDivisibleBy3) {
				return true;
			}

			@Provide(ignoreExceptions = IllegalArgumentException.class)
			Arbitrary<Integer> integerDivisibleBy3() {
				return Arbitraries.integers().greaterOrEqual(0)
								  .map(i -> {
									  if (i % 3 != 0) {
										  throw new IllegalArgumentException("not divisible by 3");
									  }
									  return i;
								  });
			}

		}
	}

	private static PropertyMethodArbitraryResolver getResolver(Class<?> container) {
		return new PropertyMethodArbitraryResolver(
			JqwikReflectionSupport.newInstancesWithDefaultConstructor(container),
			DomainContext.global()
		);
	}

	private static MethodParameter getParameter(Class<?> container, String methodName) {
		return TestHelper.getParametersFor(container, methodName).get(0);
	}

}
