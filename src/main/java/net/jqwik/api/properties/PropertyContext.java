package net.jqwik.api.properties;

import java.lang.reflect.*;

public interface PropertyContext {

	Method getTargetMethod();

	Class getContainerClass();

	String getLabel();

}
