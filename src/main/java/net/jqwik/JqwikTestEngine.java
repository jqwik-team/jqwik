package net.jqwik;

import net.jqwik.api.lifecycle.*;
import net.jqwik.descriptor.*;
import net.jqwik.discovery.*;
import net.jqwik.execution.*;
import net.jqwik.recording.*;
import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;

import java.lang.reflect.*;
import java.util.*;

public class JqwikTestEngine implements TestEngine {
	public static final String ENGINE_ID = "jqwik";

	private final LifecycleRegistry registry = new LifecycleRegistry();
	private final JqwikConfiguration configuration;

	public JqwikTestEngine() {
		this(new DefaultJqwikConfiguration());
	}

	JqwikTestEngine(JqwikConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
		TestDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueId);
		new JqwikDiscoverer(configuration.testEngineConfiguration().previousRun(), configuration.propertyDefaultValues()) //
			.discover(request, engineDescriptor);
		return engineDescriptor;
	}

	@Override
	public void execute(ExecutionRequest request) {
		TestDescriptor root = request.getRootTestDescriptor();
		registerLifecycleHooks(root);
		try (TestRunRecorder recorder = configuration.testEngineConfiguration().recorder()) {
			new JqwikExecutor(registry, recorder, configuration.testEngineConfiguration().previousFailures()) //
					.execute(root, request.getEngineExecutionListener());
		}
	}

	// TODO: Extract hook registration into class of its own
	private void registerLifecycleHooks(TestDescriptor descriptor) {
		if (descriptor instanceof PropertyMethodDescriptor) {
			registerPropertyMethodHooks((PropertyMethodDescriptor) descriptor);
		}
		if (descriptor instanceof ContainerClassDescriptor) {
			registerContainerHooks((ContainerClassDescriptor) descriptor);
		}
		for (TestDescriptor childDescriptor : descriptor.getChildren()) {
			registerLifecycleHooks(childDescriptor);
		}
	}

	private void registerContainerHooks(ContainerClassDescriptor containerClassDescriptor) {
		Class<?> containerClass = containerClassDescriptor.getContainerClass();
		registerHooks(containerClassDescriptor, containerClass);
	}

	private void registerPropertyMethodHooks(PropertyMethodDescriptor propertyMethodDescriptor) {
		Method targetMethod = propertyMethodDescriptor.getTargetMethod();
		registerHooks(propertyMethodDescriptor, targetMethod);
	}

	private void registerHooks(TestDescriptor descriptor, AnnotatedElement element) {
		List<AddLifecycleHook> addLifecycleHooks = AnnotationSupport.findRepeatableAnnotations(element, AddLifecycleHook.class);
		for (AddLifecycleHook addLifecycleHook : addLifecycleHooks) {
			registry.registerLifecycleHook(descriptor, addLifecycleHook.value());
		}
	}

}
