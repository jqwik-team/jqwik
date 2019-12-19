package net.jqwik.docs.stateful.mystack;

import java.io.*;

import net.jqwik.api.stateful.*;
import org.assertj.core.api.*;

class PopAction implements Action<MyStringStack>, Serializable {

	@Override
	public boolean precondition(MyStringStack state) {
		return !state.isEmpty();
	}

	@Override
	public MyStringStack run(MyStringStack state) {
		int sizeBefore = state.size();
		String topBefore = state.top();

		String popped = state.pop();
		Assertions.assertThat(popped).isEqualTo(topBefore);
		Assertions.assertThat(state.size()).isEqualTo(sizeBefore - 1);
		return state;
	}

	@Override
	public String toString() {
		return "pop";
	}
}
