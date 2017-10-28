package net.jqwik.execution;

import java.lang.reflect.Method;

public interface PropertyContext {

	Method getTargetMethod();

	Class getContainerClass();

	String getLabel();

}
