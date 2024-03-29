package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

public class EngineLifecycleContext extends AbstractLifecycleContext implements ContainerLifecycleContext {

	public EngineLifecycleContext(TestDescriptor engineDescriptor, Reporter reporter, ResolveParameterHook resolveParameterHook) {
		super(reporter, engineDescriptor);
	}

	@Override
	public Optional<Class<?>> optionalContainerClass() {
		return Optional.empty();
	}

	@Override
	public Optional<AnnotatedElement> optionalElement() {
		return Optional.empty();
	}

	@Override
	public <T> T newInstance(Class<T> clazz) {
		return JqwikReflectionSupport.newInstanceWithDefaultConstructor(clazz);
	}

	@Override
	public Optional<ResolveParameterHook.ParameterSupplier> resolveParameter(Executable executable, int index) {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return toString(ContainerLifecycleContext.class);
	}

}
