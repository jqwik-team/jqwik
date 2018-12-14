package net.jqwik.docs.stateful.mystack;

import java.util.*;

public class MyStringStack {
	private List<String> elements = new ArrayList<>();

	public void push(String element) {
		elements.add(0, element);
	}

	public String pop() {
		return elements.remove(0);
	}

	public void clear() {
		// Wrong implementation to provoke falsification for stacks with more than 2 elements
		// elements.clear();
		if (elements.size() > 2) {
			elements.remove(0);
		} else {
			elements.clear();
		}
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public int size() {
		return elements.size();
	}

	public String top() {
		return elements.get(0);
	}

	@Override
	public String toString() {
		return elements.toString();
	}
}
