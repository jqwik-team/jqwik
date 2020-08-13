package net.jqwik.engine;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.discovery.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.JqwikUniqueIdBuilder.*;

/**
 * For testing purposes
 */
public class TestDescriptorBuilder {

	private TestDescriptorBuilder parentBuilder = null;

	public static TestDescriptorBuilder forMethod(Class<?> containerClass, String methodName, Class<?>... parameterTypes) {
		Optional<Method> optionalMethod = ReflectionSupport.findMethod(containerClass, methodName, parameterTypes);
		if (!optionalMethod.isPresent())
			throw new JqwikException(String.format("Class [%s] has no method with name [%s] and parameters [%s]", containerClass,
												   methodName, JqwikStringSupport.parameterTypesToString(parameterTypes)
			));
		TestDescriptorBuilder methodDescriptorBuilder = new TestDescriptorBuilder(optionalMethod.get());
		methodDescriptorBuilder.parentBuilder = forClass(containerClass);
		return methodDescriptorBuilder;
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

	private final Object element;

	private final List<TestDescriptorBuilder> children = new ArrayList<>();

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
		TestDescriptor parent =
			parentBuilder == null ?
				new AbstractTestDescriptor(UniqueId.root("root", "test"), "test root") {
					@Override
					public Type getType() {
						return Type.CONTAINER;
					}
				}
				: parentBuilder.build();
		return build(parent);
	}

	public TestDescriptor build(TestDescriptor parent) {
		TestDescriptor descriptor = createDescriptor(parent);
		if (!(descriptor instanceof JqwikTestEngine)) {
			parent.addChild(descriptor);
		}
		for (TestDescriptorBuilder child : children) {
			descriptor.addChild(child.build(descriptor));
		}
		return descriptor;
	}

	private TestDescriptor createDescriptor(TestDescriptor parent) {
		if (element instanceof JqwikTestEngine)
			return new JqwikEngineDescriptor(engineId());
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
				PropertyAttributes attributes = DefaultPropertyAttributes.from(property);
				PropertyConfiguration propertyConfig =
					PropertyConfiguration.from(
						attributes,
						TestHelper.propertyAttributesDefaults(),
						null,
						null
					);

				return new PropertyMethodDescriptor(uniqueId, targetMethod, targetMethod.getDeclaringClass(), propertyConfig);
			}
		}
		throw new JqwikException("Cannot build descriptor for " + element.toString());
	}
}
