package net.jqwik.api;

import java.lang.reflect.Method;

public interface TestDescriptor {

	Method getTargetMethod();

	Class gerContainerClass();

	String getLabel();

}
