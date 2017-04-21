package net.jqwik.descriptor;

import net.jqwik.api.*;
import org.junit.platform.engine.*;

import java.lang.reflect.*;

public class PropertyMethodDescriptor extends AbstractMethodDescriptor implements PropertyContext {

	public PropertyMethodDescriptor(UniqueId uniqueId, Method exampleMethod, Class containerClass) {
		super(uniqueId, exampleMethod, containerClass);
	}

	@Override
	//TODO: Change to CONTAINER_AND_TEST as soon as https://github.com/junit-team/junit5/issues/756 has been fixed
	public Type getType() {
		return Type.TEST;
	}

	@Override
	//TODO: Remove as soon as https://github.com/junit-team/junit5/issues/756 has been fixed
	public boolean hasTests() {
		return true;
	}
}
