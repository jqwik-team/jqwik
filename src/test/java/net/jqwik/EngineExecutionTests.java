
package net.jqwik;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.When;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.GeneratorConfiguration;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.generator.ValuesOf;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.junit.gen5.api.Assertions;
import org.junit.gen5.api.Test;
import org.junit.gen5.engine.EngineDiscoveryRequest;
import org.junit.gen5.engine.TestDescriptor;
import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.discovery.ClassSelector;
import org.junit.gen5.engine.discovery.MethodSelector;
import org.junit.gen5.launcher.main.TestDiscoveryRequestBuilder;
import org.opentest4j.AssertionFailedError;
import net.jqwik.api.Assumptions;

class EngineExecutionTests extends AbstractEngineTests{

	@Test
	void executeSingleProperty() {
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
			MethodSelector.forMethod(NoParamsProperties.class, "succeedingProperty")).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		RecordingExecutionListener engineListener = executeEngine(engineDescriptor);

		Assertions.assertEquals(1, engineListener.countPropertiesStarted(), "Started");
		Assertions.assertEquals(1, engineListener.countPropertiesSuccessful(), "Successful");
	}

	@Test
	void propertiesWithoutParams() {
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
			ClassSelector.forClass(NoParamsProperties.class)).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		Assertions.assertEquals(8, engineDescriptor.allDescendants().size());

		RecordingExecutionListener engineListener = executeEngine(engineDescriptor);

		Assertions.assertEquals(6, engineListener.countPropertiesStarted(), "Started");
		Assertions.assertEquals(3, engineListener.countPropertiesSuccessful(), "Successful");
		Assertions.assertEquals(2, engineListener.countPropertiesFailed(), "Failed");
		Assertions.assertEquals(1, engineListener.countPropertiesAborted(), "Aborted");
		Assertions.assertEquals(1, engineListener.countPropertiesSkipped(), "Skipped");
	}

	@Test
	void propertiesWithUnmodifiedParams() {
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
			ClassSelector.forClass(UnmodifiedParamsProperties.class)).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));
		Assertions.assertEquals(6, engineDescriptor.allDescendants().size());

		RecordingExecutionListener engineListener = executeEngine(engineDescriptor);

		Assertions.assertEquals(5, engineListener.countPropertiesStarted(), "Started");
		Assertions.assertEquals(2, engineListener.countPropertiesSuccessful(), "Successful");
		Assertions.assertEquals(3, engineListener.countPropertiesFailed(), "Failed");
	}

	@Test
	void checkForPropertyVerificationFailure() throws NoSuchMethodException {
		Method failingMethod = UnmodifiedParamsProperties.class.getDeclaredMethod("shrinking",
			new Class[] { int.class });
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
			MethodSelector.forMethod(UnmodifiedParamsProperties.class, failingMethod)).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		RecordingExecutionListener engineListener = executeEngine(engineDescriptor);

		RecordingExecutionListener.ExecutionEvent failingEvent = engineListener.filterEvents(
			event -> event.type == RecordingExecutionListener.ExecutionEventType.Failed).findFirst().get();

		Assertions.assertEquals("shrinking", failingEvent.descriptor.getName());
		PropertyVerificationFailure verificationFailure = (PropertyVerificationFailure) failingEvent.exception;
		Assertions.assertEquals(0, verificationFailure.getArgs()[0], "Shrinked value");
	}

	@Test
	void diverseParameterTypes() {
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
			ClassSelector.forClass(DiverseProperties.class)).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		RecordingExecutionListener engineListener = executeEngine(engineDescriptor);

		Assertions.assertEquals(5, engineListener.countPropertiesStarted(), "Started");
		Assertions.assertEquals(4, engineListener.countPropertiesSuccessful(), "Successful");
		Assertions.assertEquals(1, engineListener.countPropertiesFailed(), "Failed");
	}

	private static class NoParamsProperties {
		@Property
		String skipBecauseItDoesNotReturnBoolean() {
			return "a string";
		}

		@Property
		boolean succeedingProperty() {
			return true;
		}

		@Property
		boolean failingProperty() {
			return false;
		}

		@Property
		void succeedingVoid() {
		}

		@Property
		void failingVoid() {
			throw new AssertionFailedError("failing property");
		}

		@Property
		static void succeedingStatic() {

		}

		@Property
		void shouldBeSkipped() {
			Assumptions.assume(false);
		}
	}

	private static class UnmodifiedParamsProperties {
		@Property
		boolean succeeding(int aNumber) {
			return true;
		}

		@Property
		boolean failingWithTwo(int first, int second) {
			return first > second;
		}

		@Property
		boolean succeedingWithTwo(int first, int second) {
			Assumptions.assume(first > second);
			return first > second;
		}

		@Property
		boolean shrinking(int aNumber) {
			return aNumber > 0;
		}

		@Property
		void failingWithVoid(int first, int second) {
			throw new AssertionFailedError();
		}
	}

	private static class DiverseProperties {
		@Property(trials = 10)
		void positiveIntegers(@From(IntegralGenerator.class) @Positive int i) {
		}

		@Property(trials = 10)
		void restrictedListOfIntegers(@Size(max = 5) List<@InRange(min = "0", max = "9") Integer> digits) {
		}

		@Property(trials = 10)
		static boolean allBinaryValues(@ValuesOf boolean aBoolean) {
			return true;
		}

		@Property(shrink = false)
		boolean doNotShrink(BigDecimal aNumber) {
			return aNumber.compareTo(BigDecimal.ONE) == 1;
		}

		@Property(trials = 1, shrink = false)
		boolean withFixedSeed(@When(seed = 42L) long aNumber) {
			return aNumber == 4631121966219677515L;
		}
	}

	@Target({ PARAMETER, FIELD, ANNOTATION_TYPE, TYPE_USE })
	@Retention(RUNTIME)
	@GeneratorConfiguration
	@interface Positive {
		// ...
	}

	public static class IntegralGenerator extends Generator<Integer> {
		private Positive positive;

		public IntegralGenerator() {
			super(Integer.class);
		}

		@Override
		public Integer generate(SourceOfRandomness random, GenerationStatus status) {

			int value = random.nextInt();
			return positive != null ? Math.abs(value) : value;
		}

		public void configure(Positive positive) {
			this.positive = positive;
		}
	}
}
