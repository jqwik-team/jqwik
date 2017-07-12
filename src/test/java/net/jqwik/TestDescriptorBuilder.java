package net.jqwik;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.discovery.*;
import net.jqwik.support.*;
import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import java.lang.reflect.*;
import java.util.*;

import static net.jqwik.JqwikUniqueIdBuilder.*;

/**
 * For testing purposes
 */
public class TestDescriptorBuilder {

	public static TestDescriptorBuilder forMethod(Class<?> containerClass, String methodName, Class<?>... parameterTypes)
			throws NoSuchMethodException {
		Optional<Method> optionalMethod = JqwikReflectionSupport.findMethod(containerClass, methodName, parameterTypes);
		if (!optionalMethod.isPresent())
			throw new JqwikException(String.format("Class [%s] has no method with name [%s] and parameters [%s]", containerClass,
					methodName, JqwikStringSupport.parameterTypesToString(parameterTypes)));
		return new TestDescriptorBuilder(optionalMethod.get());
	}

	public static TestDescriptorBuilder forEngine(JqwikTestEngine engine) {
		return new TestDescriptorBuilder(engine);
	}

	public static TestDescriptorBuilder forClass(Class<?> clazz, String... methodNames) throws NoSuchMethodException {
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
		for (TestDescriptorBuilder child : children) {
			this.children.add(child);
		}
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
			return new JqwikEngineDescriptor(engineId());
		if (element instanceof Class) {
			Class<?> containerClass = (Class<?>) this.element;
			return new ContainerClassDescriptor(uniqueIdForClassContainer(containerClass), containerClass, false);
		}
		if (element instanceof Method) {
			Method targetMethod = (Method) this.element;
			Optional<Property> property = AnnotationSupport.findAnnotation(targetMethod, Property.class);
			if (property.isPresent()) {
				UniqueId uniqueId = JqwikUniqueIDs.appendProperty(parent.getUniqueId(), targetMethod);
				return new PropertyMethodDescriptor(uniqueId, targetMethod, targetMethod.getDeclaringClass(), property.get().seed(),
						property.get().tries(), property.get().maxDiscardRatio());
			}
		}
		throw new JqwikException("Cannot build descriptor for " + element.toString());
	}
}
