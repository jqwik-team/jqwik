package net.jqwik.docs.stateful.mystack;

import java.io.*;

import net.jqwik.api.stateful.*;
import org.assertj.core.api.*;

class PushAction implements Action<MyStringStack>, Serializable {

	private final String element;

	PushAction(String element) {
		this.element = element;
	}

	@Override
	public MyStringStack run(MyStringStack state) {
		int sizeBefore = state.size();
		state.push(element);
		Assertions.assertThat(state.isEmpty()).isFalse();
		Assertions.assertThat(state.size()).isEqualTo(sizeBefore + 1);
		return state;
	}

	@Override
	public String toString() {
		return String.format("push(%s)", element);
	}
}
