package net.jqwik.api.lifecycle;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.PerProperty.*;

@Group
class PropertyDefaultsTests {

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
				Assertions.assertThat(propertyExecutionResult.countTries()).isEqualTo(10);
			}
		}

		private class Check20Tries implements Lifecycle {
			@Override
			public void after(PropertyExecutionResult propertyExecutionResult) {
				Assertions.assertThat(propertyExecutionResult.countTries()).isEqualTo(20);
			}
		}

	}
}
