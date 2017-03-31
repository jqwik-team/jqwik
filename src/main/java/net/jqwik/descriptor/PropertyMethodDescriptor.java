package net.jqwik.descriptor;

import java.lang.reflect.*;

import org.junit.platform.engine.*;

import net.jqwik.api.properties.*;

public class PropertyMethodDescriptor extends AbstractMethodDescriptor implements PropertyContext {

	public PropertyMethodDescriptor(UniqueId uniqueId, Method exampleMethod, Class containerClass) {
		super(uniqueId, exampleMethod, containerClass);
	}

	@Override
	public Type getType() {
		return Type.CONTAINER_AND_TEST;
	}

	@Override
	//TODO: Remove as soon as https://github.com/junit-team/junit5/issues/756 has been fixed
	public boolean hasTests() {
		return true;
	}
}
