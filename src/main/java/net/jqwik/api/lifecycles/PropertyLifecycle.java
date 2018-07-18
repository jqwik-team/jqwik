package net.jqwik.api.lifecycles;

public interface PropertyLifecycle {
//	default aroundProperty(PropertyContext propertyDescriptor, Object cont)
	void doFinally(PropertyLifecycleContext propertyDescriptor) throws Throwable;
}
