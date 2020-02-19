package net.jqwik.docs.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

class FullLifecycleExamples {

	@BeforeContainer
	static void beforeContainer() {
		System.out.println("before container");
	}

	@AfterContainer
	static void afterContainer() {
		System.out.println("after container");
	}

	@BeforeProperty
	void beforeProperty() {
		System.out.println("before property");
	}

	@AfterProperty
	void afterProperty() {
		System.out.println("after property");
	}

	@BeforeTry
	void beforeTry() {
		System.out.println("before try");
	}

	@AfterTry
	void afterTry() {
		System.out.println("after try");
	}

	@Property(tries = 3)
	void property(@ForAll @IntRange(min = -5, max = 5) int anInt) {
		System.out.println("property: " + anInt);
	}
}
