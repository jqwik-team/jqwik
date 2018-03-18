package examples.docs.stateful.mystack;

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
		// Commented out to provoke property falsification
		//elements.clear();
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
}
