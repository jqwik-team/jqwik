package examples.bugs;

import java.math.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

/**
 * https://github.com/jlink/jqwik/issues/104
 * <p>
 * checkMyStack() should reveal the bug in line 26
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
	void checkMyStack(
		@ForAll("sequences") ActionSequence<MyStringStack> actions,
		@ForAll("stacks") MyStringStack stack
	) {
		actions.run(stack);
	}

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
	void checkMyStack_ReversedParameters(
		@ForAll("stacks") MyStringStack stack,
		@ForAll("sequences") ActionSequence<MyStringStack> actions
	) {
		actions.run(stack);
	}

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
	void checkMyStackInitialEmpty(
		@ForAll("stacks") MyStringStack stack,
		@ForAll("sequences") ActionSequence<MyStringStack> actions
	) {
		actions.run(new MyStringStack(Collections.emptyList()));
	}

	@Provide
	Arbitrary<ActionSequence<MyStringStack>> sequences() {
		return Arbitraries.sequences(Arbitraries.oneOf(push(), Arbitraries.constant(new PopAction())));
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
				Assertions.assertThat(stack.size()).isEqualTo(beforeSize + 1);
				return stack;
			}

			@Override
			public String toString() {
				return String.format("push(%s)", stringToPush);
			}
		});
	}
}