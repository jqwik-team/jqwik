package net.jqwik.discovery;

import static org.junit.platform.engine.support.filter.ClasspathScanningSupport.buildClassNamePredicate;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.discovery.PackageSelector;

import net.jqwik.api.Example;

public class JqwikDiscoverer {

	private static final Logger LOG = Logger.getLogger(JqwikDiscoverer.class.getName());

	private static final Predicate<Class<?>> IS_JQWIK_CONTAINER_CLASS = classCandidate -> {
		if (ReflectionUtils.isAbstract(classCandidate))
			return false;
		if (ReflectionUtils.isPrivate(classCandidate))
			return false;
		return hasExamples(classCandidate);
	};

	private static final Predicate<Method> IS_EXAMPLE_METHOD = methodCandidate -> {
		if (ReflectionUtils.isAbstract(methodCandidate))
			return false;
		if (ReflectionUtils.isPrivate(methodCandidate))
			return false;
		return AnnotationSupport.isAnnotated(methodCandidate, Example.class);
	};

	private static boolean hasExamples(Class<?> classCandidate) {
		return !ReflectionSupport.findMethods(classCandidate, IS_EXAMPLE_METHOD, HierarchyTraversalMode.TOP_DOWN).isEmpty();
	}

	public void discover(EngineDiscoveryRequest request, TestDescriptor engineDescriptor) {
		// TODO: Use in classpath scanning
		Predicate<String> classNamePredicate = buildClassNamePredicate(request);

		request.getSelectorsByType(PackageSelector.class).forEach(selector -> {
			appendTestsInPackage(selector.getPackageName(), engineDescriptor);
		});

		request.getSelectorsByType(ClassSelector.class).forEach(selector -> {
			appendTestsInClass(selector.getJavaClass(), engineDescriptor);
		});

		request.getSelectorsByType(MethodSelector.class).forEach(selector -> {
			appendTestFromMethod(selector.getJavaMethod(), selector.getJavaClass(), engineDescriptor);
		});

		// request.getSelectorsByType(UniqueIdSelector.class).forEach(selector -> {
		// });
		// request.getSelectorsByType(ClasspathRootSelector.class).forEach(selector -> {
		// });

	}

	private void appendTestFromMethod(Method javaMethod, Class<?> javaClass, TestDescriptor engineDescriptor) {
		if (IS_EXAMPLE_METHOD.test(javaMethod)) {
			ContainerClassDescriptor classDescriptor = createClassDescriptor(javaClass, engineDescriptor, false);
			classDescriptor.addChild(new ExampleMethodDescriptor(javaMethod, javaClass, classDescriptor));
			engineDescriptor.addChild(classDescriptor);
		}
	}

	private void appendTestsInClass(Class<?> javaClass, TestDescriptor engineDescriptor) {
		if (IS_JQWIK_CONTAINER_CLASS.test(javaClass))
			engineDescriptor.addChild(createClassDescriptor(javaClass, engineDescriptor, true));
	}

	private void appendTestsInPackage(String packageName, TestDescriptor engineDescriptor) {
		ReflectionSupport.findAllClassesInPackage(packageName, IS_JQWIK_CONTAINER_CLASS, name -> true)
				.stream()
				.map(aClass -> createClassDescriptor(aClass, engineDescriptor, true))
				.forEach(engineDescriptor::addChild);
	}

	private ContainerClassDescriptor createClassDescriptor(Class<?> javaClass, TestDescriptor engineDescriptor, boolean withChildren) {
		ContainerClassDescriptor classTestDescriptor = new ContainerClassDescriptor(javaClass, engineDescriptor);
		if (withChildren) {
			appendExamplesInContainerClass(javaClass, classTestDescriptor);
		}
		return classTestDescriptor;
	}

	private void appendExamplesInContainerClass(Class<?> containerClass, TestDescriptor classTestDescriptor) {
		Map<String, List<ExampleMethodDescriptor>> exampleDescriptorsByMethodName =
				ReflectionSupport.findMethods(containerClass, IS_EXAMPLE_METHOD,HierarchyTraversalMode.TOP_DOWN)
				.stream()
				.map(method -> new ExampleMethodDescriptor(method, containerClass, classTestDescriptor))
				.collect(Collectors.groupingBy(exampleDescriptor -> exampleDescriptor.getExampleMethod().getName()));

		exampleDescriptorsByMethodName.entrySet()
				.stream()
				.map(entry -> descriptorOrError(entry, containerClass, classTestDescriptor))
				.forEach(classTestDescriptor::addChild);
	}

	private TestDescriptor descriptorOrError(Map.Entry<String, List<ExampleMethodDescriptor>> entry, Class<?> containerClass, TestDescriptor classTestDescriptor) {
		String methodName = entry.getKey();
		List<ExampleMethodDescriptor> examples = entry.getValue();
		if (examples.size() > 1) {
			LOG.warning(() -> String.format("There is more than one @Example for '%s::%s'. Ignoring all.", containerClass.getName(), methodName ));
			return new OverloadedExamplesError(examples, methodName, containerClass, classTestDescriptor);
		}
		return examples.get(0);
	}

}
