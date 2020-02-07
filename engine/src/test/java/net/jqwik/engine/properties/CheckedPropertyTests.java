package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.support.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.GenerationMode.*;
import static net.jqwik.engine.TestHelper.*;
import static net.jqwik.engine.properties.PropertyCheckResult.CheckStatus.*;
import static net.jqwik.engine.properties.PropertyConfigurationBuilder.*;

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

			assertThat(checkedProperty.configuration.getStereotype()).isEqualTo(Property.DEFAULT_STEREOTYPE);
			assertThat(checkedProperty.configuration.getTries()).isEqualTo(TestDescriptorBuilder.TRIES);
			assertThat(checkedProperty.configuration.getMaxDiscardRatio()).isEqualTo(TestDescriptorBuilder.MAX_DISCARD_RATIO);
			assertThat(checkedProperty.configuration.getAfterFailureMode()).isEqualTo(TestDescriptorBuilder.AFTER_FAILURE);
			assertThat(checkedProperty.configuration.getShrinkingMode()).isEqualTo(ShrinkingMode.BOUNDED);
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
			return factory.fromDescriptor(descriptor, createPropertyContext(descriptor), AroundTryHook.BASE);
		}

		private PropertyLifecycleContext createPropertyContext(PropertyMethodDescriptor descriptor) {
			return new PropertyLifecycleContextForMethod(descriptor, new Object(), ((key, value) -> {}));
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
				aConfig().build()
			);

			PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
			assertThat(check.checkStatus()).isEqualTo(FAILED);
			assertThat(check.throwable()).isPresent();
			assertThat(check.throwable().get()).isInstanceOf(CannotFindArbitraryException.class);
		}

		@Example
		void usingASeedWillAlwaysProvideSameArbitraryValues() {
			List<Integer> allGeneratedInts = new ArrayList<>();
			CheckedFunction addIntToList = params -> allGeneratedInts.add((int) params.get(0));
			CheckedProperty checkedProperty = createCheckedProperty(
				"prop1", addIntToList, getParametersForMethod("prop1"),
				p -> Collections.singleton(new GenericArbitrary(Arbitraries.integers().between(-100, 100))),
				Optional.empty(),
				aConfig().withSeed("414243").withTries(20).build()
			);

			PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
			assertThat(check.randomSeed()).isEqualTo("414243");

			assertThat(check.checkStatus()).isEqualTo(SUCCESSFUL);
			assertThat(allGeneratedInts)
				.containsExactly(5, 22, 10, 4, 43, 70, -66, -75, -11, -65, 93, -61, -5, 37, -2, -9, -86, 10, -10, -4);
		}

		@Example
		@Label("previous seed will be used if onFailure=PREVIOUS_SEED")
		void previousSeedWillBeUsed() {
			List<Integer> allGeneratedInts = new ArrayList<>();
			CheckedFunction addIntToList = params -> allGeneratedInts.add((int) params.get(0));
			CheckedProperty checkedProperty = createCheckedProperty(
				"prop1", addIntToList, getParametersForMethod("prop1"),
				p -> Collections.singleton(new GenericArbitrary(Arbitraries.integers().between(-100, 100))),
				Optional.empty(),
				aConfig()
					.withSeed("")
					.withPreviousSeed("101010")
					.withAfterFailure(AfterFailureMode.PREVIOUS_SEED).build()
			);

			PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
			assertThat(check.randomSeed()).isEqualTo("101010");
		}

		@Example
		@Label("previous seed will not be used if onFailure=RANDOM_SEED")
		void previousSeedWillNotBeUsed() {
			List<Integer> allGeneratedInts = new ArrayList<>();
			CheckedFunction addIntToList = params -> allGeneratedInts.add((int) params.get(0));
			CheckedProperty checkedProperty = createCheckedProperty(
				"prop1", addIntToList, getParametersForMethod("prop1"),
				p -> Collections.singleton(new GenericArbitrary(Arbitraries.integers().between(-100, 100))),
				Optional.empty(),
				aConfig()
					.withSeed("")
					.withPreviousSeed("101010")
					.withAfterFailure(AfterFailureMode.RANDOM_SEED).build()
			);

			PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
			assertThat(check.randomSeed()).isNotEqualTo("101010");
		}

		@Example
		@Label("previous seed will not be used if constant seed is set")
		void previousSeedWillNotBeUsedWithConstantSeed() {
			List<Integer> allGeneratedInts = new ArrayList<>();
			CheckedFunction addIntToList = params -> allGeneratedInts.add((int) params.get(0));
			CheckedProperty checkedProperty = createCheckedProperty(
				"prop1", addIntToList, getParametersForMethod("prop1"),
				p -> Collections.singleton(new GenericArbitrary(Arbitraries.integers().between(-100, 100))),
				Optional.empty(),
				aConfig()
					.withSeed("4242")
					.withPreviousSeed("101010")
					.withAfterFailure(AfterFailureMode.PREVIOUS_SEED).build()
			);

			PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
			assertThat(check.randomSeed()).isEqualTo("4242");
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
					aConfig().withGeneration(AUTO).build()
				);

				PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
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
					aConfig().withGeneration(DATA_DRIVEN).build()
				);

				PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
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
					aConfig().withGeneration(RANDOMIZED).build()
				);

				assertThatThrownBy(() -> checkedProperty.check(NULL_PUBLISHER, new Reporting[0])).isInstanceOf(JqwikException.class);
			}

			@Example
			@Label("fails if it has GenerationMode.EXHAUSTIVE")
			void failIfItHasGenerationModeExhaustive() {
				CheckedProperty checkedProperty = createCheckedProperty(
					"dataDrivenProperty", params -> true, getParametersForMethod("dataDrivenProperty"),
					p -> Collections.emptySet(),
					Optional.of(Table.of(Tuple.of(1, "1"))),
					aConfig().withGeneration(EXHAUSTIVE).build()
				);

				assertThatThrownBy(() -> checkedProperty.check(NULL_PUBLISHER, new Reporting[0])).isInstanceOf(JqwikException.class);
			}

			@Example
			@Label("fails if optional data is empty")
			void failIfItOptionalDataIsEmpty() {
				CheckedProperty checkedProperty = createCheckedProperty(
					"dataDrivenProperty", params -> true, getParametersForMethod("dataDrivenProperty"),
					p -> Collections.emptySet(),
					Optional.empty(),
					aConfig().withGeneration(DATA_DRIVEN).build()
				);

				assertThatThrownBy(() -> checkedProperty.check(NULL_PUBLISHER, new Reporting[0])).isInstanceOf(JqwikException.class);
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
					aConfig().withGeneration(EXHAUSTIVE).build()
				);

				PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
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
					aConfig().withTries(50).withGeneration(EXHAUSTIVE).build()
				);

				PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
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
					aConfig().withGeneration(AUTO).build()
				);

				PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
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
					aConfig().withGeneration(EXHAUSTIVE).build()
				);

				assertThatThrownBy(() -> checkedProperty.check(NULL_PUBLISHER, new Reporting[0])).isInstanceOf(JqwikException.class);
			}

			@Example
			@Label("use randomized generation if countMax is larger than configured tries")
			void useRandomizedGenerationIfCountMaxIsAboveTries() {
				CheckedProperty checkedProperty = createCheckedProperty(
					"exhaustiveProperty", params -> true, getParametersForMethod("exhaustiveProperty"),
					p -> Collections.singleton(Arbitraries.integers().between(1, 21)),
					Optional.empty(),
					aConfig().withTries(20).build()
				);

				PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
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
					aConfig().withTries(20).withGeneration(RANDOMIZED).build()
				);

				PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
				assertThat(check.generation()).isEqualTo(GenerationMode.RANDOMIZED);
				assertThat(check.countTries()).isEqualTo(20);
				assertThat(check.checkStatus()).isEqualTo(SUCCESSFUL);
			}

		}

		@Group
		class WithSample {

			@Example
			@Label("run sample only when sample is provided")
			void runWithSampleOnlyWhenSampleIsProvided() {
				List<Object> sample = Arrays.asList(1, 2);
				CheckedFunction checkSample = params -> params.equals(sample);
				CheckedProperty checkedProperty = createCheckedProperty(
					"sampleProperty", checkSample, getParametersForMethod("sampleProperty"),
					p -> Collections.emptySet(),
					Optional.empty(),
					aConfig().withFalsifiedSample(sample).withAfterFailure(AfterFailureMode.SAMPLE_ONLY).build()
				);

				PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
				assertThat(check.countTries()).isEqualTo(1);
				assertThat(check.checkStatus()).isEqualTo(SUCCESSFUL);
				assertThat(check.falsifiedSample()).isEmpty();
			}

			@Example
			@Label("run sample and then new random seed")
			void runSampleAndRandomSeed() {
				List<Object> sample = Arrays.asList(1, 2);
				CheckedFunction checkSample = params -> true;
				CheckedProperty checkedProperty = createCheckedProperty(
					"sampleProperty", checkSample, getParametersForMethod("sampleProperty"),
					p -> Collections.singleton(new GenericArbitrary(Arbitraries.integers().between(-100, 100))),
					Optional.empty(),
					aConfig().withTries(10).withFalsifiedSample(sample).withAfterFailure(AfterFailureMode.SAMPLE_FIRST).build()
				);

				PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
				assertThat(check.countTries()).isEqualTo(10);
				assertThat(check.checkStatus()).isEqualTo(SUCCESSFUL);
				assertThat(check.falsifiedSample()).isEmpty();
			}
		}
	}

	private CheckedProperty createCheckedProperty(
		String propertyName,
		CheckedFunction checkedFunction,
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver,
		Optional<Iterable<? extends Tuple>> optionalData,
		PropertyConfiguration configuration
	) {
		return new CheckedProperty(
			propertyName,
			checkedFunction,
			parameters,
			arbitraryResolver,
			optionalData,
			configuration
		);
	}

	private void intOnlyExample(String methodName, CheckedFunction forAllFunction, PropertyCheckResult.CheckStatus expectedStatus) {
		CheckedProperty checkedProperty = createCheckedProperty(
			methodName, forAllFunction, getParametersForMethod(methodName),
			p -> Collections.singleton(new GenericArbitrary(Arbitraries.integers().between(-50, 50))),
			Optional.empty(),
			aConfig().build()
		);
		PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER, new Reporting[0]);
		assertThat(check.checkStatus()).isEqualTo(expectedStatus);
	}

	private List<MethodParameter> getParametersForMethod(String methodName) {
		return getParametersFor(CheckingExamples.class, methodName);
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

		public boolean stringProp(@ForAll String aString) {
			return true;
		}

		public boolean prop0() {
			return true;
		}

		public boolean prop1(@ForAll int n1) {
			return true;
		}

		public boolean prop2(@ForAll int n1, @ForAll int n2) {
			return true;
		}

		public boolean prop8(
			@ForAll int n1, @ForAll int n2, @ForAll int n3, @ForAll int n4, @ForAll int n5, @ForAll int n6, @ForAll int n7,
			@ForAll int n8
		) {
			return true;
		}

		@FromData("fizzBuzzSamples")
		public boolean dataDrivenProperty(@ForAll int index, @ForAll String fizzBuzz) {
			return true;
		}

		public boolean exhaustiveProperty(@ForAll @IntRange(min = 1, max = 3) int anInt) {
			return true;
		}

		public boolean sampleProperty(@ForAll int n1, @ForAll int n2) {
			return true;
		}

	}
}
