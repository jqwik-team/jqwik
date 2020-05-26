package net.jqwik.docs.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

@AddLifecycleHook(ExternalServerResource.class)
class AroundContainerHookExamples {
	@Example
	void example1() {
		System.out.println("Running example 1");
	}
	@Example
	void example2() {
		System.out.println("Running example 2");
	}
}

class ExternalServerResource implements AroundContainerHook {
	@Override
	public void beforeContainer(final ContainerLifecycleContext context) {
		System.out.println("Starting server...");
	}

	@Override
	public void afterContainer(final ContainerLifecycleContext context) {
		System.out.println("Stopping server...");
	}
}