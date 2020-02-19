package net.jqwik.docs;

import net.jqwik.api.*;

class SimpleLifecycleTests implements AutoCloseable {

	SimpleLifecycleTests() {
		System.out.println("Before each");
	}

	@Example
	void anExample() {
		System.out.println("anExample");
	}

	@Property(tries = 5)
	void aProperty(@ForAll String aString) {
		System.out.println("aProperty: " + aString);
	}

	@Override
	public void close() throws Exception {
		System.out.println("After each");
	}
}

