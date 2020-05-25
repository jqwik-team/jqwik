package net.jqwik.docs.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

class SkipExecutionHookExample {

	@Property
	@AddLifecycleHook(OnMacOnly.class)
	void myProperty(@ForAll int anInt) {
	}

}

class OnMacOnly implements SkipExecutionHook {
	@Override
	public SkipResult shouldBeSkipped(final LifecycleContext context) {
		if (System.getProperty("os.name").equals("Mac OS X")) {
			return SkipResult.doNotSkip();
		}
		return SkipResult.skip("Only on Mac");
	}
}
