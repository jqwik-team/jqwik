package examples.docs.stateful.mystack;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

class MyStringStackProperties {

	@Property(tries = 10, reporting = Reporting.GENERATED)
	void checkMyStackMachine(@ForAll("stackMachine") StateMachineRunner<MyStringStack> stackMachine) {
		stackMachine.run();
	}

	@Provide
	Arbitrary<StateMachineRunner<MyStringStack>> stackMachine() {
		return Arbitraries.stateMachineRunner(MyStackMachine.class);
	}

}
