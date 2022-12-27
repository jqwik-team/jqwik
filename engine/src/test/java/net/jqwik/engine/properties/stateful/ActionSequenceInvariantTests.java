package net.jqwik.engine.properties.stateful;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

class ActionSequenceInvariantTests {

	@Example
	boolean succeedingInvariant(@ForAll JqwikRandom random) {
		Arbitrary<ActionSequence<MyModel>> arbitrary = Arbitraries.sequences(changeValue());
		Shrinkable<ActionSequence<MyModel>> sequence = arbitrary.generator(10, true).next(random);

		ActionSequence<MyModel> sequenceWithInvariant = sequence.value().withInvariant(model -> Assertions.assertThat(true).isTrue());
		MyModel result = sequenceWithInvariant.run(new MyModel());
		return result.value.length() > 0;
	}

	private Arbitrary<Action<MyModel>> changeValue() {
		return Arbitraries.strings().alpha().ofMinLength(1).map(aString -> model -> model.setValue(aString));
	}

	@Example
	void failingInvariantFailSequenceRun(@ForAll JqwikRandom random) {
		Arbitrary<ActionSequence<MyModel>> arbitrary = Arbitraries.sequences(Arbitraries.oneOf(changeValue(), nullify())).ofSize(20);
		Shrinkable<ActionSequence<MyModel>> sequence = arbitrary.generator(1000, true).next(random);

		ActionSequence<MyModel> sequenceWithInvariant = sequence.value().withInvariant(model -> Assertions.assertThat(model.value).isNotNull());

		Assertions.assertThatThrownBy(() -> sequenceWithInvariant.run(new MyModel()))
				  .isInstanceOf(InvariantFailedError.class);
	}

	private Arbitrary<Action<MyModel>> nullify() {
		return Arbitraries.just(model -> model.setValue(null));
	}

	static class MyModel {

		public String value = "";

		MyModel setValue(String aString) {
			this.value = aString;
			return this;
		}

	}

}
