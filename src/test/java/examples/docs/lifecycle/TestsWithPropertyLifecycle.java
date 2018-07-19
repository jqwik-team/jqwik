package examples.docs.lifecycle;

import net.jqwik.api.*;

class TestsWithPropertyLifecycle implements AutoCloseable {

	TestsWithPropertyLifecycle() {
		System.out.println("Before each property");
	}

	@Example void anExample() {
		System.out.println("anExample");
	}

	@Property(tries = 5)
	void aProperty(@ForAll String aString) {
		System.out.println("anProperty: " + aString);
	}

	@Override
	public void close() {
		System.out.println("Finally after each property");
	}
}
