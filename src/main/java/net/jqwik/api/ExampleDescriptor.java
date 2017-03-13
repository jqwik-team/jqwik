package net.jqwik.api;

import java.lang.reflect.Method;

public interface ExampleDescriptor {

	Method getExampleMethod();

	Class gerContainerClass();

	String getLabel();

}
