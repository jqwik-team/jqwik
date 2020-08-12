package net.jqwik.engine.descriptor;

import java.lang.reflect.*;

import org.junit.platform.engine.*;

public class PropertyMethodDescriptor extends AbstractMethodDescriptor {

	private final PropertyConfiguration configuration;

	public PropertyMethodDescriptor(
		UniqueId uniqueId,
		Method propertyMethod,
		Class<?> containerClass,
		PropertyConfiguration configuration
	) {
		super(uniqueId, propertyMethod, containerClass);
		this.configuration = configuration;
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}

	public PropertyConfiguration getConfiguration() {
		return configuration;
	}

}
