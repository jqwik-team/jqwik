package net.jqwik.execution.lifecycle;

import java.util.*;

import net.jqwik.api.lifecycle.*;

public class RegisteredLifecycleHooks {

	public static Iterable<LifecycleHook> getRegisteredHooks() {
		return ServiceLoader.load(LifecycleHook.class);
	}

}
