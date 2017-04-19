package net.jqwik.api;

import java.lang.reflect.*;

public interface PropertyContext {

	Method getTargetMethod();

	Class getContainerClass();

	String getLabel();

}
