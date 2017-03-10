package net.jqwik;

import net.jqwik.discovery.JqwikClassTestDescriptor;
import net.jqwik.discovery.JqwikExampleTestDescriptor;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
		return build(null);
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
			return new JqwikEngineDescriptor(UniqueId.forEngine(((JqwikTestEngine) element).getId()));
		if (element instanceof Class)
			return new JqwikClassTestDescriptor((Class) element, parent);
		if (element instanceof Method)
			return new JqwikExampleTestDescriptor((Method) element, ((JqwikClassTestDescriptor) parent).getContainerClass(), parent);
		throw new JqwikException("Cannot build descriptor for " + element.toString());
	}
}
