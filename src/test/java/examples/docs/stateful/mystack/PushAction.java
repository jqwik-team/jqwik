package examples.docs.stateful.mystack;

import net.jqwik.api.stateful.*;
import org.assertj.core.api.*;

class PushAction implements Action<MyStringStack> {

	private final String element;

	PushAction(String element) {
		this.element = element;
	}

	@Override
	public MyStringStack run(MyStringStack model) {
		int sizeBefore = model.size();
		model.push(element);
		Assertions.assertThat(model.isEmpty()).isFalse();
		Assertions.assertThat(model.size()).isEqualTo(sizeBefore + 1);
		return model;
	}

	@Override
	public String toString() {
		return String.format("push(%s)", element);
	}
}
