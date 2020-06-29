package net.jqwik.docs.stateful.mystack;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.stateful.*;
import org.assertj.core.api.*;

class MyStringStackProperties {

	@Property(tries = 20, afterFailure = AfterFailureMode.SAMPLE_FIRST)
	void checkMyStack(@ForAll("sequences") @Size(max = 10) ActionSequence<MyStringStack> actions) {
		actions.run(new MyStringStack());
	}

	@Provide
	Arbitrary<ActionSequence<MyStringStack>> sequences() {
		return Arbitraries.sequences(Arbitraries.oneOf(push(), pop(), clear()));
	}

	private Arbitrary<PushAction> push() {
		return Arbitraries.strings().alpha().ofLength(5).map(PushAction::new);
	}

	private Arbitrary<Action<MyStringStack>> clear() {
		return Arbitraries.just(new ClearAction());
	}

	private Arbitrary<Action<MyStringStack>> pop() {
		return Arbitraries.just(new PopAction());
	}


	@Property
	void checkMyStackWithInvariant(@ForAll("sequences") ActionSequence<MyStringStack> actions) {
		actions
			.withInvariant("greater", stack -> Assertions.assertThat(stack.size()).isGreaterThanOrEqualTo(0))
			.withInvariant("less", stack -> Assertions.assertThat(stack.size()).isLessThan(5))
			.run(new MyStringStack());
	}
}
