package examples.bugs;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.stateful.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

/**
 * https://github.com/jqwik-team/jqwik/issues/104
 */
public class JqwikActionsShrinkToNothing {

	public class MyStringStack {
		private final Stack<String> stack = new Stack<>();

		public MyStringStack(Collection<String> initialContents) {
			stack.addAll(initialContents);
		}

		public void push(String element) {
			stack.push(element);
			stack.push(element); // Intentional bug
		}

		public String pop() {
			return stack.pop();
		}

		public int size() {
			return stack.size();
		}

		@Override
		public String toString() {
			return String.format("MyStringStack%s", stack);
		}
	}

	public class PopAction implements Action<MyStringStack> {

		@Override
		public boolean precondition(final MyStringStack state) {
			// Precondition that fails when state is maximally shrunk.
			return state.size() > 0;
		}

		@Override
		public MyStringStack run(final MyStringStack state) {
			state.pop();
			return state;
		}

		@Override
		public String toString() {
			return "pop()";
		}
	}

	@Property
	@Report(Reporting.FALSIFIED)
	@ExpectFailure(checkResult = ShrinkToOneActionEmptyStack.class)
	void checkMyStack(
		@ForAll("sequences") ActionSequence<MyStringStack> actions,
		@ForAll("stacks") MyStringStack stack
	) {
		actions.run(stack);
	}

	private class ShrinkToOneActionEmptyStack implements Consumer<PropertyExecutionResult> {
		@Override
		public void accept(final PropertyExecutionResult propertyExecutionResult) {
			List<Object> shrunkExample = propertyExecutionResult.falsifiedParameters().get();
			ActionSequence shrunkSequence = (ActionSequence) shrunkExample.get(0);
			assertThat(shrunkSequence.runActions()).hasSize(1);
			MyStringStack shrunkStack = (MyStringStack) shrunkExample.get(1);
			assertThat(shrunkStack.size()).isEqualTo(0);
		}
	}

	@Property
	@Report(Reporting.FALSIFIED)
	@ExpectFailure(checkResult = ShrinkToEmptyStackOneAction.class)
	void checkMyStack_ReversedParameters(
		@ForAll("stacks") MyStringStack stack,
		@ForAll("sequences") ActionSequence<MyStringStack> actions
	) {
		actions.run(stack);
	}

	private class ShrinkToEmptyStackOneAction implements Consumer<PropertyExecutionResult> {
		@Override
		public void accept(final PropertyExecutionResult propertyExecutionResult) {
			List<Object> shrunkExample = propertyExecutionResult.falsifiedParameters().get();
			MyStringStack shrunkStack = (MyStringStack) shrunkExample.get(0);
			assertThat(shrunkStack.size()).isEqualTo(0);
			ActionSequence<?> shrunkSequence = (ActionSequence<?>) shrunkExample.get(1);
			assertThat(shrunkSequence.runActions()).hasSize(1);
		}
	}

	@Provide
	Arbitrary<ActionSequence<MyStringStack>> sequences() {
		return Arbitraries.sequences(
			Arbitraries.oneOf(
				push(),
				Arbitraries.just(new PopAction())
			));
	}

	@Provide
	Arbitrary<MyStringStack> stacks() {
		// Need to be able to shrink the initial state, too.
		return Arbitraries.strings().alpha().ofLength(5).list().map(MyStringStack::new);
	}

	@Provide
	Arbitrary<List<String>> lists() {
		return Arbitraries.strings().alpha().ofLength(5).list();
	}

	private Arbitrary<Action<MyStringStack>> push() {
		return Arbitraries.strings().alpha().ofLength(5).map(stringToPush -> new Action<MyStringStack>() {
			@Override
			public MyStringStack run(final MyStringStack stack) {
				final int beforeSize = stack.size();
				stack.push(stringToPush);
				// Bug should be found during post-condition check.
				assertThat(stack.size()).isEqualTo(beforeSize + 1);
				return stack;
			}

			@Override
			public String toString() {
				return String.format("push(%s)", stringToPush);
			}
		});
	}

}