package net.jqwik.api;

import java.lang.reflect.*;

public interface TestContext {

	Method getTargetMethod();

	Class getContainerClass();

	String getLabel();

}
