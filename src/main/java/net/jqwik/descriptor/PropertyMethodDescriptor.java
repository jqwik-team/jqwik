package net.jqwik.descriptor;

import java.lang.reflect.Method;

import org.junit.platform.engine.UniqueId;

import net.jqwik.execution.PropertyContext;

public class PropertyMethodDescriptor extends AbstractMethodDescriptor implements PropertyContext {

	private final PropertyConfiguration configuration;

	public PropertyMethodDescriptor(UniqueId uniqueId, Method propertyMethod, Class containerClass, PropertyConfiguration configuration) {
		super(uniqueId, propertyMethod, containerClass);
		this.configuration = configuration;
	}

	@Override
	public Type getType() {
		// TODO: Should be Type.CONTAINER_AND_TEST but then
		// IntelliJ does not display failures correctly in Test-Runner
		return Type.TEST;
	}

	public PropertyConfiguration getConfiguration() {
		return configuration;
	}

}
