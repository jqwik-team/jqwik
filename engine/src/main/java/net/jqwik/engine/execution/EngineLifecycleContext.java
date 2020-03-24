package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

public class EngineLifecycleContext extends AbstractLifecycleContext implements ContainerLifecycleContext {

	private TestDescriptor engineDescriptor;

	public EngineLifecycleContext(TestDescriptor engineDescriptor, Reporter reporter, ResolveParameterHook resolveParameterHook) {
		super(reporter);
		this.engineDescriptor = engineDescriptor;
	}

	@Override
	public Optional<Class<?>> optionalContainerClass() {
		return Optional.empty();
	}

	@Override
	public String label() {
		return engineDescriptor.getDisplayName();
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

}
