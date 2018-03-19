package examples.docs.stateful.mystack;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

class MyStringStackProperties {

	@Property(reporting = Reporting.GENERATED)
	void checkMyStack(@ForAll("sequences") ActionSequence<MyStringStack> stackMachine) {
		stackMachine.run(new MyStringStack());
	}

	@Provide
	Arbitrary<ActionSequence<MyStringStack>> sequences() {
		return Arbitraries.sequences(Arbitraries.oneOf(push(), pop(), clear()));
	}

	private Arbitrary<Action<MyStringStack>> push() {
		return Arbitraries.strings().alpha().ofLength(5).map(PushAction::new);
	}

	private Arbitrary<Action<MyStringStack>> clear() {
		return Arbitraries.constant(new ClearAction());
	}

	private Arbitrary<Action<MyStringStack>> pop() {
		return Arbitraries.constant(new PopAction());
	}

}
