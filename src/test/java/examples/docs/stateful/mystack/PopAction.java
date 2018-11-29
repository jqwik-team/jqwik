package examples.docs.stateful.mystack;

import java.io.*;

import net.jqwik.api.stateful.*;
import org.assertj.core.api.*;

class PopAction implements Action<MyStringStack>, Serializable {

	@Override
	public boolean precondition(MyStringStack model) {
		return !model.isEmpty();
	}

	@Override
	public MyStringStack run(MyStringStack model) {
		int sizeBefore = model.size();
		String topBefore = model.top();

		String popped = model.pop();
		Assertions.assertThat(popped).isEqualTo(topBefore);
		Assertions.assertThat(model.size()).isEqualTo(sizeBefore - 1);
		return model;
	}

	@Override
	public String toString() {
		return "pop";
	}
}
