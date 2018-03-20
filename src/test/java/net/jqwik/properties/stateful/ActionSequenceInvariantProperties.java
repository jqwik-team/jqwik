package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import org.assertj.core.api.*;

import java.util.*;

class ActionSequenceInvariantProperties {

	@Example
	boolean succeedingInvariant(@ForAll Random random) {
		Arbitrary<ActionSequence<ModelWithInvariant>> arbitrary = Arbitraries.sequences(changeValue());
		Shrinkable<ActionSequence<ModelWithInvariant>> sequence = arbitrary.generator(10).next(random);

		ModelWithInvariant result = sequence.value().run(new ModelWithInvariant());
		return result.value.length() > 0;
	}

	private Arbitrary<Action<ModelWithInvariant>> changeValue() {
		return Arbitraries.strings().alpha().ofMinLength(1).map(aString -> model -> model.setValue(aString));
	}

	@Example
	void failingInvariantFailSequenceRun(@ForAll Random random) {
		Arbitrary<ActionSequence<ModelWithInvariant>> arbitrary = Arbitraries.sequences(Arbitraries.oneOf(changeValue(), nullify()));
		Shrinkable<ActionSequence<ModelWithInvariant>> sequence = arbitrary.generator(10).next(random);

		Assertions.assertThatThrownBy(() -> sequence.value().run(new ModelWithInvariant()))
				  .isInstanceOf(InvariantFailedError.class);
	}

	private Arbitrary<Action<ModelWithInvariant>> nullify() {
		return Arbitraries.constant(model -> model.setValue(null));
	}

	static class ModelWithInvariant implements Invariant {

		private String value = "";

		ModelWithInvariant setValue(String aString) {
			this.value = aString;
			return this;
		}

		@Override
		public boolean invariant() {
			return value != null;
		}
	}

}
