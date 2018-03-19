package examples.docs.stateful.mystack;

import net.jqwik.api.stateful.*;
import org.assertj.core.api.*;

class PopAction implements Action<MyStringStack> {

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
