package net.jqwik.docs.state.mystack;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.state.*;

import static org.assertj.core.api.Assertions.*;

class MyStringStackExamples {

	@Property
	void checkMyStack(@ForAll("myStackActions") ActionChain<MyStringStack> chain) {
		chain.run();
	}

	@Provide
	Arbitrary<ActionChain<MyStringStack>> myStackActions() {
		return ActionChain.startWith(MyStringStack::new)
						  .addAction(push())
						  .addAction(pop())
						  .addAction(clear());
	}

	private Action<MyStringStack> push() {
		return new PushAction();
	}

	private Action<MyStringStack> clear() {
		return Action.just(
			"clear",
			stack -> {
				stack.clear();
				assertThat(stack.isEmpty()).describedAs("is stack empty").isTrue();
				return stack;
			}
		);
	}

	private Action<MyStringStack> pop() {
		return Action.<MyStringStack>when(stack -> !stack.isEmpty())
					 .describeAs("pop")
					 .justMutate(stack -> {
						 int sizeBefore = stack.size();
						 String topBefore = stack.top();

						 String popped = stack.pop();
						 assertThat(popped).isEqualTo(topBefore);
						 assertThat(stack.size()).isEqualTo(sizeBefore - 1);
					 });
	}

	@Property
	void checkMyStackWithInvariant(@ForAll("myStackActions") ActionChain<MyStringStack> chain) {
		chain
			.withInvariant("greater", stack -> assertThat(stack.size()).isGreaterThanOrEqualTo(0))
			.withInvariant("less", stack -> assertThat(stack.size()).isLessThan(5)) // Does not hold!
			.run();
	}

	static class PushAction implements Action.Independent<MyStringStack> {

		@Override
		public Arbitrary<Transformer<MyStringStack>> transformer() {
			StringArbitrary pushElements = Arbitraries.strings().alpha().ofLength(5);
			return pushElements.map(element -> Transformer.mutate(
				String.format("push(%s)", element),
				stack -> {
					int sizeBefore = stack.size();
					stack.push(element);
					assertThat(stack.isEmpty()).isFalse();
					assertThat(stack.size()).isEqualTo(sizeBefore + 1);
				}
			));
		}
	}
}
