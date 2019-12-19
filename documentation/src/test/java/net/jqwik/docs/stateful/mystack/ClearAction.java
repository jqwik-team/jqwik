package net.jqwik.docs.stateful.mystack;

import java.io.*;

import net.jqwik.api.stateful.*;
import org.assertj.core.api.*;

class ClearAction implements Action<MyStringStack>, Serializable {

	@Override
	public MyStringStack run(MyStringStack stack) {
		stack.clear();
		Assertions.assertThat(stack.isEmpty()).isTrue();
		return stack;
	}

	@Override
	public String toString() {
		return "clear";
	}
}
