package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;

public class EngineLifecycleContext implements ContainerLifecycleContext {

	private TestDescriptor engineDescriptor;
	private Reporter reporter;

	public EngineLifecycleContext(TestDescriptor engineDescriptor, Reporter reporter) {
		this.engineDescriptor = engineDescriptor;
		this.reporter = reporter;
	}

	@Override
	public Optional<Class<?>> containerClass() {
		return Optional.empty();
	}

	@Override
	public String label() {
		return engineDescriptor.getDisplayName();
	}

	@Override
	public Optional<AnnotatedElement> annotatedElement() {
		return Optional.empty();
	}

	@Override
	public Reporter reporter() {
		return reporter;
	}
}
