package net.jqwik.docs.state.mystack;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
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
						  .addAction(new PushAction())
						  .addAction(pop())
						  .addAction(new ClearAction());
	}

	@Provide
	@Property
	void checkMyStackInfinite(@ForAll("infiniteStackActions") ActionChain<MyStringStack> chain) {
		chain.run();
	}

	@Provide
	Arbitrary<ActionChain<MyStringStack>> infiniteStackActions() {
		return ActionChain.startWith(MyStringStack::new)
						  .addAction(new PushAction())
						  .addAction(pop())
						  .addAction(new ClearAction())
						  .addAction(Action.just(Transformer.endOfChain()))
						  .infinite();
	}

	static class PushAction implements Action.Independent<MyStringStack> {

		@Override
		public Arbitrary<Transformer<MyStringStack>> transformer() {
			Arbitrary<String> pushElements = Arbitraries.strings().alpha().ofLength(5);
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

	static class ClearAction extends Action.JustMutate<MyStringStack> {
		@Override
		public void mutate(MyStringStack stack) {
			stack.clear();
			assertThat(stack.isEmpty()).describedAs("stack is empty").isTrue();
		}

		@Override
		public String description() {
			return "clear";
		}
	}

	@Property
	void checkMyStackWithInvariant(@ForAll("myStackActions") ActionChain<MyStringStack> chain) {
		chain
			.withInvariant("greater", stack -> assertThat(stack.size()).isGreaterThanOrEqualTo(0))
			.withInvariant("less", stack -> assertThat(stack.size()).isLessThan(5)) // Does not hold!
			.run();
	}
}
