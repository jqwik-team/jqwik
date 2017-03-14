package net.jqwik;

import net.jqwik.descriptor.ContainerClassDescriptor;
import net.jqwik.descriptor.ExampleMethodDescriptor;
import net.jqwik.descriptor.JqwikEngineDescriptor;
import net.jqwik.discovery.JqwikUniqueIDs;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static net.jqwik.JqwikUniqueIdBuilder.engineId;
import static net.jqwik.JqwikUniqueIdBuilder.uniqueIdForClassContainer;

/**
 * For testing purposes
 */
public class TestDescriptorBuilder {

	public static TestDescriptorBuilder forMethod(Class<?> containerClass, String methodName) throws NoSuchMethodException {
		Method method = containerClass.getDeclaredMethod(methodName);
		return new TestDescriptorBuilder(method);
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
			Method exampleMethod = (Method) this.element;
			UniqueId uniqueId = JqwikUniqueIDs.appendExample(parent.getUniqueId(), exampleMethod);
			return new ExampleMethodDescriptor(uniqueId, exampleMethod, exampleMethod.getDeclaringClass());
		}
		throw new JqwikException("Cannot build descriptor for " + element.toString());
	}
}
