package net.jqwik.api.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.PerProperty.*;

import static org.assertj.core.api.Assertions.*;

@Group
class PropertyDefaultsTests {

	@Group
	@PropertyDefaults(
		tries = 42,
		afterFailure = AfterFailureMode.RANDOM_SEED,
		shrinking = ShrinkingMode.FULL,
		generation = GenerationMode.RANDOMIZED,
		edgeCases = EdgeCasesMode.NONE
	)
	class OtherAttributes {

		@Property
		@PerProperty(CheckDefaultsAreSet.class)
		void allDefaultsAreSet(@ForAll int anInt) {
		}

		private class CheckDefaultsAreSet implements Lifecycle {
			@Override
			public void before(PropertyLifecycleContext context) {
				assertThat(context.attributes().tries().get()).isEqualTo(42);
				assertThat(context.attributes().afterFailure().get()).isEqualTo(AfterFailureMode.RANDOM_SEED);
				assertThat(context.attributes().shrinking().get()).isEqualTo(ShrinkingMode.FULL);
				assertThat(context.attributes().generation().get()).isEqualTo(GenerationMode.RANDOMIZED);
				assertThat(context.attributes().edgeCases().get()).isEqualTo(EdgeCasesMode.NONE);
			}
		}
	}

	@PropertyDefaults(tries = 10)
	@Group
	class Tries {

		@Property
		@PerProperty(Check10Tries.class)
		void runTenTries(@ForAll int anInt) {
		}

		@Group
		class NestedGroup {
			@Property
			@PerProperty(Check10Tries.class)
			void defaultsWorkForNestedGroups(@ForAll int anInt) {
			}

			@Group
			@PropertyDefaults(tries = 20)
			class DeeperNested {
				@Property
				@PerProperty(Check20Tries.class)
				void closestDefaultIsUsed(@ForAll int anInt) {
				}
			}
		}

		@Property(tries = 20)
		@PerProperty(Check20Tries.class)
		void runTwentyTries(@ForAll int anInt) {
		}

		private class Check10Tries implements Lifecycle {
			@Override
			public void after(PropertyExecutionResult propertyExecutionResult) {
				assertThat(propertyExecutionResult.countTries()).isEqualTo(10);
			}
		}

		private class Check20Tries implements Lifecycle {
			@Override
			public void after(PropertyExecutionResult propertyExecutionResult) {
				assertThat(propertyExecutionResult.countTries()).isEqualTo(20);
			}
		}

	}
}
