package net.jqwik.docs.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class TimingExtensionExamples {

	@Property
	@Timing
	void fastProperty(@ForAll int anInt) {
	}

	@Property(tries = 50)
	@Timing
	void slowProperty(@ForAll @IntRange(min = 10, max = 100) int delay) throws InterruptedException {
		Thread.sleep(delay);
	}
}
