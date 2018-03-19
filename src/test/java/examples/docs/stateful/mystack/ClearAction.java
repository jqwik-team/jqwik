package examples.docs.stateful.mystack;

import net.jqwik.api.stateful.*;
import org.assertj.core.api.*;

class ClearAction implements Action<MyStringStack> {

	@Override
	public MyStringStack run(MyStringStack model) {
		model.clear();
		Assertions.assertThat(model.isEmpty()).isTrue();
		return model;
	}

	@Override
	public String toString() {
		return "clear";
	}
}
