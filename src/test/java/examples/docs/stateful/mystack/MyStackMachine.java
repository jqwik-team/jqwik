package examples.docs.stateful.mystack;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import org.assertj.core.api.*;

import java.util.*;

class MyStackMachine implements StateMachine<MyStringStack> {

	@Override
	public List<Arbitrary<Action<MyStringStack>>> actions() {
		return Arrays.asList(push(), clear(), pop());
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

	@Override
	public MyStringStack createModel() {
		return new MyStringStack();
	}

	private class PushAction implements Action<MyStringStack> {

		private final String element;

		private PushAction(String element) {
			this.element = element;
		}

		@Override
		public void run(MyStringStack model) {
			int sizeBefore = model.size();
			model.push(element);
			Assertions.assertThat(model.isEmpty()).isFalse();
			Assertions.assertThat(model.size()).isEqualTo(sizeBefore + 1);
		}

		@Override
		public String toString() {
			return String.format("push(%s)", element);
		}
	}

	private class ClearAction implements Action<MyStringStack> {

		@Override
		public void run(MyStringStack model) {
			model.clear();
			Assertions.assertThat(model.isEmpty()).isTrue();
		}

		@Override
		public String toString() {
			return "clear";
		}
	}

	private class PopAction implements Action<MyStringStack> {

		@Override
		public boolean precondition(MyStringStack model) {
			return !model.isEmpty();
		}

		@Override
		public void run(MyStringStack model) {
			int sizeBefore = model.size();
			String topBefore = model.top();

			String popped = model.pop();
			Assertions.assertThat(popped).isEqualTo(topBefore);
			Assertions.assertThat(model.size()).isEqualTo(sizeBefore - 1);
		}

		@Override
		public String toString() {
			return "pop";
		}
	}
}
