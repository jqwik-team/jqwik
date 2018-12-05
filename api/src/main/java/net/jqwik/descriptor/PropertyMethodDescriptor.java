package net.jqwik.descriptor;

import org.junit.platform.engine.*;

import java.lang.reflect.*;

public class PropertyMethodDescriptor extends AbstractMethodDescriptor {

	private final PropertyConfiguration configuration;

	public PropertyMethodDescriptor(UniqueId uniqueId, Method propertyMethod, Class containerClass, PropertyConfiguration configuration) {
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
