package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import org.assertj.core.api.*;

import java.util.*;

class ActionSequenceInvariantProperties {

	@Example
	boolean succeedingInvariant(@ForAll Random random) {
		Arbitrary<ActionSequence<MyModel>> arbitrary = Arbitraries.sequences(changeValue());
		NShrinkable<ActionSequence<MyModel>> sequence = arbitrary.generator(10).next(random);

		ActionSequence<MyModel> sequenceWithInvariant = sequence.value().withInvariant(model -> {
			Assertions.assertThat(true).isTrue();
		});
		MyModel result = sequenceWithInvariant.run(new MyModel());
		return result.value.length() > 0;
	}

	private Arbitrary<Action<MyModel>> changeValue() {
		return Arbitraries.strings().alpha().ofMinLength(1).map(aString -> model -> model.setValue(aString));
	}

	@Example
	void failingInvariantFailSequenceRun(@ForAll Random random) {
		Arbitrary<ActionSequence<MyModel>> arbitrary = Arbitraries.sequences(Arbitraries.oneOf(changeValue(), nullify()));
		NShrinkable<ActionSequence<MyModel>> sequence = arbitrary.generator(10).next(random);

		ActionSequence<MyModel> sequenceWithInvariant = sequence.value().withInvariant(model -> {
			Assertions.assertThat(model.value).isNotNull();
		});

		Assertions.assertThatThrownBy(() -> sequenceWithInvariant.run(new MyModel()))
				  .isInstanceOf(InvariantFailedError.class);
	}

	private Arbitrary<Action<MyModel>> nullify() {
		return Arbitraries.constant(model -> model.setValue(null));
	}

	static class MyModel {

		public String value = "";

		MyModel setValue(String aString) {
			this.value = aString;
			return this;
		}

	}

}
