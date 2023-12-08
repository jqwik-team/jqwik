package net.jqwik.engine.properties.stateful;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.stateful.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.testing.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

class ShrinkableActionSequenceTests {

	@Example
	void createNotRunSequence() {
		List<Shrinkable<Action<String>>> actions = asList(
			shrinkableAddCC(),
			shrinkableAddX()
		);
		ActionGenerator<String> actionGenerator = new ShrinkablesActionGenerator<>(actions);
		Shrinkable<ActionSequence<String>> shrinkable = new ShrinkableActionSequence<>(
			actionGenerator, actions.size(), ShrinkingDistance.of(2)
		);

		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(2));

		ActionSequence<String> sequence = shrinkable.value();
		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.NOT_RUN);
		assertThat(sequence.toString()).contains("2 actions intended");
	}

	@Example
	void createAndRunSequence() {
		List<Shrinkable<Action<String>>> actions = asList(
			shrinkableAddCC(),
			shrinkableAddX()
		);
		Shrinkable<ActionSequence<String>> shrinkable = createAndRunShrinkableSequence(actions);
		assertThat(shrinkable.value().finalModel()).isEqualTo("ccx");
		assertThat(shrinkable.value().runState()).isEqualTo(ActionSequence.RunState.SUCCEEDED);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(2, 2, 4));
	}

	@SuppressWarnings("unchecked")
	@Example
	void shrinkToSequenceWithFirstActionOnly() {

		List<Shrinkable<Action<String>>> actions = asList(
			shrinkableAddCC(),
			shrinkableAddX(),
			shrinkableAddCC(),
			shrinkableAddX()
		);
		Shrinkable<ActionSequence<String>> shrinkable = createAndRunShrinkableSequence(actions);
		TestingFalsifier<ActionSequence<String>> falsifier = seq -> {
			seq.run("");
			return false;
		};
		ActionSequence<String> shrunkValue = shrink(shrinkable, falsifier, null);
		assertThat(shrunkValue.runActions()).hasSize(1);
		assertThat(shrunkValue.run("")).isEqualTo("x");
	}

	@Example
	void alsoShrinkActions() {
		List<Shrinkable<Action<String>>> actions = asList(
			shrinkableAddCC(),
			shrinkableAddX(),
			shrinkableAddCC(),
			shrinkableAddCC()
		);
		Shrinkable<ActionSequence<String>> shrinkable = createAndRunShrinkableSequence(actions);

		TestingFalsifier<ActionSequence<String>> falsifier = seq -> {
			String result = seq.run("");
			return result.length() < 2;
		};

		ActionSequence<String> shrunkValue = shrink(shrinkable, falsifier, null);
		assertThat(shrunkValue.runActions()).hasSize(1);
		assertThat(shrunkValue.runActions().get(0).run("")).isEqualTo("aa");
	}

	@Example
	void alsoShrinkSequenceThenActionsTheSequenceAgain() {
		List<Shrinkable<Action<String>>> actions = asList(
			shrinkableAddCC(),
			shrinkableAddX(),
			shrinkableAddCC(),
			shrinkableAddCC()
		);
		Shrinkable<ActionSequence<String>> shrinkable = createAndRunShrinkableSequence(actions);

		TestingFalsifier<ActionSequence<String>> falsifier = seq -> {
			String result = seq.run("");
			if (result.contains("c"))
				return result.length() < 4;
			else
				return result.length() < 2;
		};

		ActionSequence<String> shrunkValue = shrink(shrinkable, falsifier, null);
		assertThat(shrunkValue.runActions()).hasSize(1);
		assertThat(shrunkValue.runActions().get(0).run("")).isEqualTo("aa");
	}

	@Example
	void actionsWithFailingPreconditionsAreShrunkAway() {
		List<Shrinkable<Action<String>>> actions = asList(
			shrinkableFailingPrecondition(),
			shrinkableAddCC(),
			shrinkableAddX(),
			shrinkableFailingPrecondition(),
			shrinkableAddCC(),
			shrinkableFailingPrecondition(),
			shrinkableFailingPrecondition(),
			shrinkableAddCC()
		);
		Shrinkable<ActionSequence<String>> shrinkable = createAndRunShrinkableSequence(actions);

		TestingFalsifier<ActionSequence<String>> falsifier = seq -> {
			String result = seq.run("");
			return result.length() < 2;
		};

		ActionSequence<String> shrunkValue = shrink(shrinkable, falsifier, null);
		assertThat(shrunkValue.runActions()).hasSize(1);
		assertThat(shrunkValue.runActions().get(0).run("")).isEqualTo("aa");
	}

	@Property(tries = 100)
	void alwaysShrinkToSingleAction(@ForAll("stringActions") @Size(max = 50) List<Shrinkable<Action<String>>> actions) {
		actions.add(shrinkableAddX()); // to ensure that at least one action is valid
		Shrinkable<ActionSequence<String>> shrinkable = createAndRunShrinkableSequence(actions);

		TestingFalsifier<ActionSequence<String>> falsifier = seq -> {
			String result = seq.run("");
			return !result.contains("x");
		};
		ActionSequence<String> shrunkValue = shrink(shrinkable, falsifier, null);
		assertThat(shrunkValue.run("")).isEqualTo("x");
	}

	@Provide
	Arbitrary<List<Shrinkable<Action<String>>>> stringActions() {
		return Arbitraries.of(shrinkableAddCC(), shrinkableAddX(), shrinkableFailingPrecondition()).list();
	}

	private Shrinkable<Action<String>> shrinkableFailingPrecondition() {
		return Shrinkable
				   .unshrinkable("poops")
				   .map(ignore -> new Action<String>() {
					   @Override
					   public boolean precondition(String state) {
						   return false;
					   }

					   @Override
					   public String run(String state) {
						   return state;
					   }
				   });
	}

	private Shrinkable<Action<String>> shrinkableAddX() {
		return Shrinkable
				   .unshrinkable("x")
				   .map(aString -> model -> model + aString);
	}

	private Shrinkable<Action<String>> shrinkableAddCC() {
		return ShrinkableStringTests
				   .createShrinkableString("cc", 2, false)
				   .map(aString -> model -> model + aString);
	}

	private Shrinkable<ActionSequence<String>> createAndRunShrinkableSequence(List<Shrinkable<Action<String>>> actions) {
		ActionGenerator<String> actionGenerator = new ShrinkablesActionGenerator<>(actions);
		Shrinkable<ActionSequence<String>> shrinkable = new ShrinkableActionSequence<>(
			actionGenerator, actions.size(), ShrinkingDistance.of(actions.size())
		);
		shrinkable.value().run("");
		return shrinkable;
	}

}
