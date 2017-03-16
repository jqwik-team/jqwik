package net.jqwik.api;

import java.lang.reflect.Method;

public interface TestContext {

	Method getTargetMethod();

	Class getContainerClass();

	String getLabel();

}
