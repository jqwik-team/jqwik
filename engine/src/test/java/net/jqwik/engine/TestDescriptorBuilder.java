package net.jqwik.engine;

import static net.jqwik.engine.JqwikUniqueIdBuilder.*;

import java.lang.reflect.Method;
import java.util.*;

import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import net.jqwik.api.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.discovery.JqwikUniqueIDs;
import net.jqwik.engine.support.JqwikStringSupport;

/**
 * For testing purposes
 */
public class TestDescriptorBuilder {

	public static final int TRIES = 1000;
	public static final int MAX_DISCARD_RATIO = 5;
	public static final AfterFailureMode AFTER_FAILURE = AfterFailureMode.PREVIOUS_SEED;

	public static TestDescriptorBuilder forMethod(Class<?> containerClass, String methodName, Class<?>... parameterTypes) {
		Optional<Method> optionalMethod = ReflectionSupport.findMethod(containerClass, methodName, parameterTypes);
		if (!optionalMethod.isPresent())
			throw new JqwikException(String.format("Class [%s] has no method with name [%s] and parameters [%s]", containerClass,
					methodName, JqwikStringSupport.parameterTypesToString(parameterTypes)));
		return new TestDescriptorBuilder(optionalMethod.get());
	}

	public static TestDescriptorBuilder forEngine(JqwikTestEngine engine) {
		return new TestDescriptorBuilder(engine);
	}

	public static TestDescriptorBuilder forClass(Class<?> clazz, String... methodNames) {
		TestDescriptorBuilder testDescriptorBuilder = new TestDescriptorBuilder(clazz);
		for (String methodName : methodNames) {
			testDescriptorBuilder.with(forMethod(clazz, methodName));
		}
		return testDescriptorBuilder;
	}

	private Object element;

	private List<TestDescriptorBuilder> children = new ArrayList<>();

	private TestDescriptorBuilder(Object element) {
		this.element = element;
	}

	public TestDescriptorBuilder with(TestDescriptorBuilder... children) {
		Collections.addAll(this.children, children);
		return this;
	}

	public TestDescriptorBuilder with(Object... elements) {
		for (Object element : elements) {
			children.add(new TestDescriptorBuilder(element));
		}
		return this;
	}

	public TestDescriptor build() {
		return build(new AbstractTestDescriptor(UniqueId.root("root", "test"), "test root") {
			@Override
			public Type getType() {
				return Type.CONTAINER;
			}
		});
	}

	public TestDescriptor build(TestDescriptor parent) {
		TestDescriptor descriptor = createDescriptor(parent);
		for (TestDescriptorBuilder child : children) {
			descriptor.addChild(child.build(descriptor));
		}
		return descriptor;
	}

	private TestDescriptor createDescriptor(TestDescriptor parent) {
		if (element instanceof JqwikTestEngine)
			return new JqwikEngineDescriptor(engineId(), null, null);
		if (element instanceof Class) {
			Class<?> containerClass = (Class<?>) this.element;
			return new ContainerClassDescriptor(uniqueIdForClassContainer(containerClass), containerClass, false);
		}
		if (element instanceof Method) {
			Method targetMethod = (Method) this.element;
			Optional<Property> optionalProperty = AnnotationSupport.findAnnotation(targetMethod, Property.class);
			if (optionalProperty.isPresent()) {
				Property property = optionalProperty.get();
				UniqueId uniqueId = JqwikUniqueIDs.appendProperty(parent.getUniqueId(), targetMethod);
				PropertyConfiguration propertyConfig = PropertyConfiguration.from(property, PropertyDefaultValues.with(TRIES, MAX_DISCARD_RATIO, AFTER_FAILURE), null, null);

				return new PropertyMethodDescriptor(uniqueId, targetMethod, targetMethod.getDeclaringClass(), propertyConfig);
			}
		}
		throw new JqwikException("Cannot build descriptor for " + element.toString());
	}
}
