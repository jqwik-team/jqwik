package net.jqwik;

import net.jqwik.api.Example;
import net.jqwik.api.Property;
import net.jqwik.descriptor.ContainerClassDescriptor;
import net.jqwik.descriptor.ExampleMethodDescriptor;
import net.jqwik.descriptor.JqwikEngineDescriptor;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import net.jqwik.discovery.JqwikUniqueIDs;
import net.jqwik.support.JqwikReflectionSupport;
import net.jqwik.support.JqwikStringSupport;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.jqwik.JqwikUniqueIdBuilder.engineId;
import static net.jqwik.JqwikUniqueIdBuilder.uniqueIdForClassContainer;

/**
 * For testing purposes
 */
public class TestDescriptorBuilder {

	public static TestDescriptorBuilder forMethod(Class<?> containerClass, String methodName, Class<?>... parameterTypes)
			throws NoSuchMethodException {
		Optional<Method> optionalMethod = JqwikReflectionSupport.findMethod(containerClass, methodName, parameterTypes);
		if (!optionalMethod.isPresent())
			throw new JqwikException(String.format("Class [%s] has no method with name [%s] and parameters [%s]", containerClass,
					methodName, JqwikStringSupport.nullSafeToString(parameterTypes)));
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
			public boolean isContainer() {
				return true;
			}

			@Override
			public boolean isTest() {
				return false;
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
			return new ContainerClassDescriptor(uniqueIdForClassContainer(containerClass), containerClass);
		}
		if (element instanceof Method) {
			Method targetMethod = (Method) this.element;
			if (AnnotationSupport.isAnnotated(targetMethod, Example.class)) {
				UniqueId uniqueId = JqwikUniqueIDs.appendExample(parent.getUniqueId(), targetMethod);
				return new ExampleMethodDescriptor(uniqueId, targetMethod, targetMethod.getDeclaringClass());
			}
			if (AnnotationSupport.isAnnotated(targetMethod, Property.class)) {
				UniqueId uniqueId = JqwikUniqueIDs.appendProperty(parent.getUniqueId(), targetMethod);
				return new PropertyMethodDescriptor(uniqueId, targetMethod, targetMethod.getDeclaringClass());
			}
		}
		throw new JqwikException("Cannot build descriptor for " + element.toString());
	}
}
