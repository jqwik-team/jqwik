package net.jqwik.api.lifecycles;

import java.lang.reflect.Method;

public interface PropertyLifecycleContext {

	Method targetMethod();

	Class containerClass();

	String label();

	Object testInstance();

}
