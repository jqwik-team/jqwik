package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;

import org.assertj.core.api.*;
import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.support.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import static net.jqwik.api.GenerationMode.*;
import static net.jqwik.engine.TestHelper.*;
import static net.jqwik.engine.properties.PropertyCheckResult.CheckStatus.*;
import static net.jqwik.engine.properties.PropertyConfigurationBuilder.*;

@SuppressLogging
@Group
class CheckedPropertyTests {

	private static final Consumer<ReportEntry> NULL_PUBLISHER = entry -> {
	};

	@Group
	class CheckedPropertyCreation {
		@Example
		void createCheckedPropertyWithoutParameters() {
			PropertyMethodDescriptor descriptor =
				(PropertyMethodDescriptor) TestDescriptorBuilder
											   .forMethod(CheckingExamples.class, "propertyWithoutParameters", int.class)
											   .build();
			CheckedProperty checkedProperty = createCheckedProperty(descriptor);

			assertThat(checkedProperty.configuration.getStereotype()).isEqualTo(PropertyAttributesDefaults.DEFAULT_STEREOTYPE);
			assertThat(checkedProperty.configuration.getTries()).isEqualTo(DEFAULT_TRIES);
			assertThat(checkedProperty.configuration.getMaxDiscardRatio()).isEqualTo(DEFAULT_MAX_DISCARD_RATIO);
			assertThat(checkedProperty.configuration.getAfterFailureMode()).isEqualTo(DEFAULT_AFTER_FAILURE);
			assertThat(checkedProperty.configuration.getShrinkingMode()).isEqualTo(DEFAULT_SHRINKING);
		}

		@Example
		void createCheckedPropertyWithTriesParameter() {
			PropertyMethodDescriptor descriptor =
				(PropertyMethodDescriptor) TestDescriptorBuilder
											   .forMethod(CheckingExamples.class, "propertyWith42TriesAndMaxDiscardRatio2", int.class)
											   .build();
			CheckedProperty checkedProperty = createCheckedProperty(descriptor);

			assertThat(checkedProperty.configuration.getStereotype()).isEqualTo("OtherStereotype");
			assertThat(checkedProperty.configuration.getTries()).isEqualTo(42);
			assertThat(checkedProperty.configuration.getMaxDiscardRatio()).isEqualTo(2);
			assertThat(checkedProperty.configuration.getAfterFailureMode()).isEqualTo(AfterFailureMode.RANDOM_SEED);
			assertThat(checkedProperty.configuration.getShrinkingMode()).isEqualTo(ShrinkingMode.OFF);
		}

		private CheckedProperty createCheckedProperty(PropertyMethodDescriptor descriptor) {
			CheckedPropertyFactory factory = new CheckedPropertyFactory();
			return factory.fromDescriptor(
				descriptor,
				createPropertyContext(descriptor),
				AroundTryHook.BASE,
				ResolveParameterHook.DO_NOT_RESOLVE,
				InvokePropertyMethodHook.DEFAULT
			);
		}

		private PropertyLifecycleContext createPropertyContext(PropertyMethodDescriptor descriptor) {
			return new DefaultPropertyLifecycleContext(
				descriptor,
				JqwikReflectionSupport.newInstanceWithDefaultConstructor(descriptor.getContainerClass()),
				TestHelper.reporter(),
				ResolveParameterHook.DO_NOT_RESOLVE
			);
		}

	}

	@Group
	class PropertyChecking {
		@Example
		void intParametersSuccess() {
			intOnlyExample("prop0", params -> params.size() == 0, SUCCESSFUL);
			intOnlyExample("prop1", params -> params.size() == 1, SUCCESSFUL);
			intOnlyExample("prop2", params -> params.size() == 2, SUCCESSFUL);
			intOnlyExample("prop8", params -> params.size() == 8, SUCCESSFUL);
		}

		@Example
		void intParametersFailure() {
			intOnlyExample("prop0", params -> false, FAILED);
			intOnlyExample("prop1", params -> false, FAILED);
			intOnlyExample("prop2", params -> false, FAILED);
			intOnlyExample("prop8", params -> false, FAILED);
		}

		@Example
		void exceptionDuringCheck() {
			RuntimeException toThrow = new RuntimeException("test");
			intOnlyExample("prop0", params -> {
				throw toThrow;
			}, FAILED);
			intOnlyExample("prop8", params -> {
				throw toThrow;
			}, FAILED);
		}

		@Example
		void testAbortMakesCheckExhausted() {
			RuntimeException toThrow = new TestAbortedException("test");
			intOnlyExample("prop0", params -> {
				throw toThrow;
			}, EXHAUSTED);
			intOnlyExample("prop8", params -> {
				throw toThrow;
			}, EXHAUSTED);
		}

		@Example
		void assertionErrorMakesCheckFalsified() {
			AssertionError toThrow = new AssertionError("test");
			intOnlyExample("prop0", params -> {
				throw toThrow;
			}, FAILED);
			intOnlyExample("prop8", params -> {
				throw toThrow;
			}, FAILED);
		}

		@Example
		void ifNoArbitraryForParameterCanBeFound_checkIsFalsified() {
			List<MethodParameter> parameters = getParametersForMethod("stringProp");

			CheckedProperty checkedProperty = createCheckedProperty(
				"stringProp",
				params -> false,
				parameters,
				p -> Collections.emptySet(),
				Optional.empty(),
				aConfig().build(),
				lifecycleContextForMethod("stringProp", String.class)
			);

			PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
			assertThat(check.checkStatus()).isEqualTo(FAILED);
			assertThat(check.throwable()).isPresent();
			assertThat(check.throwable().get()).isInstanceOf(CannotFindArbitraryException.class);
		}

		@Example
		void usingFailOnFixedSeedWillFailWithExplicitSeed() {
			CheckedProperty checkedProperty = createCheckedProperty(
				"prop1",
				p -> fail("should not be called"),
				Collections.emptyList(),
				p -> fail("should not be called"),
				Optional.empty(),
				aConfig().withSeed("414243").withWhenFixedSeed(FixedSeedMode.FAIL).build(),
				lifecycleContextForMethod("prop1", int.class)
			);

			PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
			assertThat(check.checkStatus()).isEqualTo(FAILED);
			assertThat(check.throwable()).isPresent();
			assertThat(check.throwable().get()).isInstanceOf(FailOnFixedSeedException.class);
		}

		@Example
		void usingASeedWillAlwaysProvideSameArbitraryValues() {
			List<Integer> allGeneratedInts = new ArrayList<>();
			CheckedFunction addIntToList = params -> allGeneratedInts.add((int) params.get(0));
			CheckedProperty checkedProperty = createCheckedProperty(
				"prop1", addIntToList, getParametersForMethod("prop1"),
				p -> Collections.singleton(Arbitraries.integers().between(-100, 100).asGeneric()),
				Optional.empty(),
				aConfig().withSeed("414243").withTries(20).withEdgeCases(EdgeCasesMode.MIXIN).build(),
				lifecycleContextForMethod("prop1", int.class)
			);

			PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
			assertThat(check.seed()).isEqualTo(Optional.of("414243"));
			assertThat(check.checkStatus()).isEqualTo(SUCCESSFUL);

			ArrayList<Integer> generatedAtFirstAttempt = new ArrayList<>(allGeneratedInts);

			// Generate once more
			allGeneratedInts.clear();
			checkedProperty.check(new Reporting[0]);

			assertThat(allGeneratedInts).isEqualTo(generatedAtFirstAttempt);
		}

		@Example
		@Label("previous seed will be used if onFailure=PREVIOUS_SEED")
		void previousSeedWillBeUsed() {
			List<Integer> allGeneratedInts = new ArrayList<>();
			CheckedFunction addIntToList = params -> allGeneratedInts.add((int) params.get(0));
			CheckedProperty checkedProperty = createCheckedProperty(
				"prop1", addIntToList, getParametersForMethod("prop1"),
				p -> Collections.singleton(Arbitraries.integers().between(-100, 100).asGeneric()),
				Optional.empty(),
				aConfig()
					.withSeed("")
					.withPreviousFailureGeneration(new GenerationInfo("101010"))
					.withAfterFailure(AfterFailureMode.PREVIOUS_SEED).build(),
				lifecycleContextForMethod("prop1", int.class)
			);

			PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
			assertThat(check.seed()).isEqualTo(Optional.of("101010"));
		}

		@Example
		@Label("previous seed will not be used if onFailure=RANDOM_SEED")
		void previousSeedWillNotBeUsed() {
			List<Integer> allGeneratedInts = new ArrayList<>();
			CheckedFunction addIntToList = params -> allGeneratedInts.add((int) params.get(0));
			CheckedProperty checkedProperty = createCheckedProperty(
				"prop1", addIntToList, getParametersForMethod("prop1"),
				p -> Collections.singleton(Arbitraries.integers().between(-100, 100).asGeneric()),
				Optional.empty(),
				aConfig()
					.withSeed("")
					.withPreviousFailureGeneration(new GenerationInfo("101010"))
					.withAfterFailure(AfterFailureMode.RANDOM_SEED).build(),
				lifecycleContextForMethod("prop1", int.class)
			);

			PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
			assertThat(check.seed()).isNotEqualTo(Optional.of("101010"));
		}

		@Example
		@Label("previous seed will not be used if constant seed is set")
		void previousSeedWillNotBeUsedWithConstantSeed() {
			List<Integer> allGeneratedInts = new ArrayList<>();
			CheckedFunction addIntToList = params -> allGeneratedInts.add((int) params.get(0));
			CheckedProperty checkedProperty = createCheckedProperty(
				"prop1", addIntToList, getParametersForMethod("prop1"),
				p -> Collections.singleton(Arbitraries.integers().between(-100, 100).asGeneric()),
				Optional.empty(),
				aConfig()
					.withSeed("4242")
					.withPreviousFailureGeneration(new GenerationInfo("101010"))
					.withAfterFailure(AfterFailureMode.PREVIOUS_SEED).build(),
				lifecycleContextForMethod("prop1", int.class)
			);

			PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
			assertThat(check.seed()).isEqualTo(Optional.of("4242"));
		}

		@Example
		@Label("use previously failed generation info")
		void usePreviouslyFailedGenerationInfo() {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 99);
			GenerationInfo previousGenerationInfo = new GenerationInfo("41", 13);
			// This is what's being generated from integers in the 13th attempt
			List<Integer> expectedParameterValues = Arrays.asList(65, 77);

			CheckedFunction checkSample = params -> {
				Assertions.assertThat(params)
						  .describedAs("sampleProperty initial params should reuse GenerationInfo supplied in the config. " +
										   "If you see failure here, then it looks like the random generation strategy has changed. " +
										   "You might need to adjust expectedParameterValues = ... in usePreviouslyFailedGeneration() property test.")
						  .isEqualTo(expectedParameterValues);
				return true;
			};

			CheckedProperty checkedProperty = createCheckedProperty(
				"sampleProperty", checkSample, getParametersForMethod("sampleProperty"),
				p -> Collections.singleton(integers),
				Optional.empty(),
				aConfig()
					.withPreviousFailureGeneration(previousGenerationInfo)
					.withAfterFailure(AfterFailureMode.SAMPLE_ONLY).build(),
				lifecycleContextForMethod("sampleProperty", int.class, int.class)
			);

			PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
			assertSoftly(
				softly -> {
					softly.assertThat(check.countTries())
						  .describedAs("check.countTries()")
						  .isEqualTo(1);
					softly.assertThat(check.seed())
						  .describedAs("check.seed()")
						  .contains("41");
					softly.assertThat(check.checkStatus())
						  .describedAs("check.checkStatus()")
						  .isEqualTo(SUCCESSFUL);
					softly.assertThat(check.throwable())
						  .describedAs("check.throwable()")
						  .isEmpty();
				}
			);
		}

		@SuppressWarnings("unchecked")
		@Group
		class DataDrivenProperty {
			@Example
			@Label("works with GenerationMode.AUTO")
			void runWithGenerationModeAuto() {
				List<Tuple.Tuple2<Integer, String>> allGeneratedParameters = new ArrayList<>();
				CheckedFunction rememberParameters =
					params -> allGeneratedParameters.add(Tuple.of((int) params.get(0), (String) params.get(1)));
				CheckedProperty checkedProperty = createCheckedProperty(
					"dataDrivenProperty", rememberParameters, getParametersForMethod("dataDrivenProperty"),
					p -> Collections.emptySet(),
					Optional.of(Table.of(Tuple.of(1, "1"), Tuple.of(3, "Fizz"), Tuple.of(5, "Buzz"))),
					aConfig().withGeneration(AUTO).build(),
					lifecycleContextForMethod("dataDrivenProperty", int.class, String.class)
				);

				PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
				assertThat(check.generation()).isEqualTo(GenerationMode.DATA_DRIVEN);
				assertThat(check.countTries()).isEqualTo(3);
				assertThat(check.checkStatus()).isEqualTo(SUCCESSFUL);
				assertThat(allGeneratedParameters).containsExactly(Tuple.of(1, "1"), Tuple.of(3, "Fizz"), Tuple.of(5, "Buzz"));
			}

			@Example
			@Label("works with GenerationMode.DATA_DRIVEN")
			void runWithGenerationModeDataDriven() {
				List<Tuple.Tuple2<Integer, String>> allGeneratedParameters = new ArrayList<>();
				CheckedFunction rememberParameters = params -> allGeneratedParameters
																   .add(Tuple.of((int) params.get(0), (String) params.get(1)));
				CheckedProperty checkedProperty = createCheckedProperty(
					"dataDrivenProperty", rememberParameters, getParametersForMethod("dataDrivenProperty"),
					p -> Collections.emptySet(),
					Optional.of(Table.of(Tuple.of(1, "1"), Tuple.of(3, "Fizz"), Tuple.of(5, "Buzz"))),
					aConfig().withGeneration(DATA_DRIVEN).build(),
					lifecycleContextForMethod("dataDrivenProperty", int.class, String.class)
				);

				PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
				assertThat(check.generation()).isEqualTo(GenerationMode.DATA_DRIVEN);
				assertThat(check.countTries()).isEqualTo(3);
				assertThat(check.checkStatus()).isEqualTo(SUCCESSFUL);
				assertThat(allGeneratedParameters).containsExactly(Tuple.of(1, "1"), Tuple.of(3, "Fizz"), Tuple.of(5, "Buzz"));
			}

			@Example
			@Label("fails if it has GenerationMode.RANDOMIZED")
			void failIfItHasGenerationModeRandomized() {
				CheckedProperty checkedProperty = createCheckedProperty(
					"dataDrivenProperty", params -> true, getParametersForMethod("dataDrivenProperty"),
					p -> Collections.emptySet(),
					Optional.of(Table.of(Tuple.of(1, "1"))),
					aConfig().withGeneration(RANDOMIZED).build(),
					lifecycleContextForMethod("dataDrivenProperty", int.class, String.class)
				);

				assertThatThrownBy(() -> checkedProperty.check(new Reporting[0])).isInstanceOf(JqwikException.class);
			}

			@Example
			@Label("fails if it has GenerationMode.EXHAUSTIVE")
			void failIfItHasGenerationModeExhaustive() {
				CheckedProperty checkedProperty = createCheckedProperty(
					"dataDrivenProperty", params -> true, getParametersForMethod("dataDrivenProperty"),
					p -> Collections.emptySet(),
					Optional.of(Table.of(Tuple.of(1, "1"))),
					aConfig().withGeneration(EXHAUSTIVE).build(),
					lifecycleContextForMethod("dataDrivenProperty", int.class, String.class)
				);

				assertThatThrownBy(() -> checkedProperty.check(new Reporting[0])).isInstanceOf(JqwikException.class);
			}

			@Example
			@Label("fails if optional data is empty")
			void failIfItOptionalDataIsEmpty() {
				CheckedProperty checkedProperty = createCheckedProperty(
					"dataDrivenProperty", params -> true, getParametersForMethod("dataDrivenProperty"),
					p -> Collections.emptySet(),
					Optional.empty(),
					aConfig().withGeneration(DATA_DRIVEN).build(),
					lifecycleContextForMethod("dataDrivenProperty", int.class, String.class)
				);

				assertThatThrownBy(() -> checkedProperty.check(new Reporting[0])).isInstanceOf(JqwikException.class);
			}
		}

		@Group
		class ExhaustiveProperty {

			@Example
			@Label("works with GenerationMode.EXHAUSTIVE")
			void runWithGenerationModeExhaustive() {
				List<Tuple.Tuple1<Integer>> allGeneratedParameters = new ArrayList<>();
				CheckedFunction rememberParameters = params -> allGeneratedParameters.add(Tuple.of((int) params.get(0)));
				CheckedProperty checkedProperty = createCheckedProperty(
					"exhaustiveProperty", rememberParameters, getParametersForMethod("exhaustiveProperty"),
					p -> Collections.singleton(Arbitraries.integers().between(1, 3)),
					Optional.empty(),
					aConfig().withGeneration(EXHAUSTIVE).build(),
					lifecycleContextForMethod("exhaustiveProperty", int.class)
				);

				PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
				assertThat(check.generation()).isEqualTo(GenerationMode.EXHAUSTIVE);
				assertThat(check.countTries()).isEqualTo(3);
				assertThat(check.checkStatus()).isEqualTo(SUCCESSFUL);
				assertThat(allGeneratedParameters).containsExactly(Tuple.of(1), Tuple.of(2), Tuple.of(3));
			}

			@Example
			@Label("with explicit GenerationMode.EXHAUSTIVE number of tries is set to countMax")
			void withExplicitGenerationModeExhaustive() {
				CheckedProperty checkedProperty = createCheckedProperty(
					"exhaustiveProperty", params -> true, getParametersForMethod("exhaustiveProperty"),
					p -> Collections.singleton(Arbitraries.integers().between(1, 99)),
					Optional.empty(),
					aConfig().withTries(50).withGeneration(EXHAUSTIVE).build(),
					lifecycleContextForMethod("exhaustiveProperty", int.class)
				);

				PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
				assertThat(check.generation()).isEqualTo(GenerationMode.EXHAUSTIVE);
				assertThat(check.countTries()).isEqualTo(99);
				assertThat(check.checkStatus()).isEqualTo(SUCCESSFUL);
			}

			@Example
			@Label("works with GenerationMode.AUTO")
			void runWithGenerationModeAuto() {
				List<Tuple.Tuple1<Integer>> allGeneratedParameters = new ArrayList<>();
				CheckedFunction rememberParameters = params -> allGeneratedParameters.add(Tuple.of((int) params.get(0)));
				CheckedProperty checkedProperty = createCheckedProperty(
					"exhaustiveProperty", rememberParameters, getParametersForMethod("exhaustiveProperty"),
					p -> Collections.singleton(Arbitraries.integers().between(1, 3)),
					Optional.empty(),
					aConfig().withGeneration(AUTO).build(),
					lifecycleContextForMethod("exhaustiveProperty", int.class)
				);

				PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
				assertThat(check.generation()).isEqualTo(GenerationMode.EXHAUSTIVE);
				assertThat(check.countTries()).isEqualTo(3);
				assertThat(check.checkStatus()).isEqualTo(SUCCESSFUL);
				assertThat(allGeneratedParameters).containsExactly(Tuple.of(1), Tuple.of(2), Tuple.of(3));
			}

			@Example
			@Label("fails if it no exhaustive generators are provided")
			void failIfNoExhaustiveGeneratorsAreProvided() {
				CheckedProperty checkedProperty = createCheckedProperty(
					"exhaustiveProperty", params -> true, getParametersForMethod("exhaustiveProperty"),
					p -> Collections.singleton(Arbitraries.integers()),
					Optional.empty(),
					aConfig().withGeneration(EXHAUSTIVE).build(),
					lifecycleContextForMethod("exhaustiveProperty", int.class)
				);

				assertThatThrownBy(() -> checkedProperty.check(new Reporting[0])).isInstanceOf(JqwikException.class);
			}

			@Example
			@Label("use randomized generation if countMax is larger than configured tries")
			void useRandomizedGenerationIfCountMaxIsAboveTries() {
				CheckedProperty checkedProperty = createCheckedProperty(
					"exhaustiveProperty", params -> true, getParametersForMethod("exhaustiveProperty"),
					p -> Collections.singleton(Arbitraries.integers().between(1, 21)),
					Optional.empty(),
					aConfig().withTries(20).build(),
					lifecycleContextForMethod("exhaustiveProperty", int.class)
				);

				PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
				assertThat(check.generation()).isEqualTo(GenerationMode.RANDOMIZED);
				assertThat(check.countTries()).isEqualTo(20);
				assertThat(check.checkStatus()).isEqualTo(SUCCESSFUL);
			}

			@Example
			@Label("use randomized generation with explicit GenerationMode.RANDOMIZED")
			void useRandomizedWithExplicitGenerationModeRandomized() {
				CheckedProperty checkedProperty = createCheckedProperty(
					"exhaustiveProperty", params -> true, getParametersForMethod("exhaustiveProperty"),
					p -> Collections.singleton(Arbitraries.integers().between(1, 3)),
					Optional.empty(),
					aConfig().withTries(20).withGeneration(RANDOMIZED).build(),
					lifecycleContextForMethod("exhaustiveProperty", int.class)
				);

				PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
				assertThat(check.generation()).isEqualTo(GenerationMode.RANDOMIZED);
				assertThat(check.countTries()).isEqualTo(20);
				assertThat(check.checkStatus()).isEqualTo(SUCCESSFUL);
			}

		}

	}

	private CheckedProperty createCheckedProperty(
		String propertyName,
		CheckedFunction checkedFunction,
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver,
		Optional<Iterable<? extends Tuple>> optionalData,
		PropertyConfiguration configuration,
		PropertyLifecycleContext propertyLifecycleContext
	) {
		return new CheckedProperty(
			propertyName,
			checkedFunction,
			parameters,
			arbitraryResolver,
			ResolveParameterHook.DO_NOT_RESOLVE,
			propertyLifecycleContext,
			optionalData,
			configuration
		);
	}

	private void intOnlyExample(String methodName, CheckedFunction forAllFunction, PropertyCheckResult.CheckStatus expectedStatus) {
		Class<?>[] parameterTypes =
			methodName.equals("prop0") ? new Class<?>[0]
				: methodName.equals("prop1") ? new Class<?>[]{int.class}
					  : methodName.equals("prop2") ? new Class<?>[]{int.class, int.class}
							: new Class<?>[]{int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class};

		CheckedProperty checkedProperty = createCheckedProperty(
			methodName, forAllFunction, getParametersForMethod(methodName),
			p -> Collections.singleton(Arbitraries.integers().between(-50, 50).asGeneric()),
			Optional.empty(),
			aConfig().build(),
			lifecycleContextForMethod(methodName, parameterTypes)
		);
		PropertyCheckResult check = checkedProperty.check(new Reporting[0]);
		assertThat(check.checkStatus()).isEqualTo(expectedStatus);
	}

	private List<MethodParameter> getParametersForMethod(String methodName) {
		return getParametersFor(CheckingExamples.class, methodName);
	}

	private PropertyLifecycleContext lifecycleContextForMethod(String methodName, Class<?>... parameterTypes) {
		return propertyLifecycleContextFor(CheckingExamples.class, methodName, parameterTypes);
	}

	private static class CheckingExamples {

		@Property
		public boolean propertyWithoutParameters(@ForAll int anyNumber) {
			return true;
		}

		@Property(
			stereotype = "OtherStereotype",
			tries = 42,
			maxDiscardRatio = 2,
			shrinking = ShrinkingMode.OFF,
			afterFailure = AfterFailureMode.RANDOM_SEED
		)
		public boolean propertyWith42TriesAndMaxDiscardRatio2(@ForAll int anyNumber) {
			return true;
		}

		@Property
		public boolean stringProp(@ForAll String aString) {
			return true;
		}

		@Property
		public boolean prop0() {
			return true;
		}

		@Property
		public boolean prop1(@ForAll int n1) {
			return true;
		}

		@Property
		public boolean prop2(@ForAll int n1, @ForAll int n2) {
			return true;
		}

		@Property
		public boolean prop8(
			@ForAll int n1, @ForAll int n2, @ForAll int n3, @ForAll int n4, @ForAll int n5, @ForAll int n6, @ForAll int n7,
			@ForAll int n8
		) {
			return true;
		}

		@Property
		@FromData("fizzBuzzSamples")
		public boolean dataDrivenProperty(@ForAll int index, @ForAll String fizzBuzz) {
			return true;
		}

		@Property
		public boolean exhaustiveProperty(@ForAll @IntRange(min = 1, max = 3) int anInt) {
			return true;
		}

		@Property
		public boolean sampleProperty(@ForAll int n1, @ForAll int n2) {
			return true;
		}

	}
}
