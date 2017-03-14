package net.jqwik.discovery;

import net.jqwik.api.Example;
import net.jqwik.descriptor.ContainerClassDescriptor;
import net.jqwik.descriptor.ExampleMethodDescriptor;
import net.jqwik.descriptor.OverloadedExampleMethodDescriptor;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.discovery.PackageSelector;
import org.junit.platform.engine.discovery.UniqueIdSelector;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.platform.engine.support.filter.ClasspathScanningSupport.buildClassNamePredicate;

public class JqwikDiscoverer {

	public static final String CONTAINER_SEGMENT_TYPE = "class";
	public static final String EXAMPLE_SEGMENT_TYPE = "example";
	public static final String OVERLOADED_SEGMENT_TYPE = "overloaded";
	public static final String OVERLOADED_ERROR_SEGMENT_TYPE = "error";

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
			appendTestsInPackage(selector.getPackageName(), engineDescriptor, classNamePredicate);
		});

		request.getSelectorsByType(ClassSelector.class).forEach(selector -> {
			appendTestsInClass(selector.getJavaClass(), engineDescriptor);
		});

		request.getSelectorsByType(MethodSelector.class).forEach(selector -> {
			appendTestFromMethod(selector.getJavaMethod(), selector.getJavaClass(), engineDescriptor);
		});

		request.getSelectorsByType(UniqueIdSelector.class).forEach(selector -> {
			appendTestsFromUniqueId(selector.getUniqueId(), engineDescriptor);
		});
		// request.getSelectorsByType(ClasspathRootSelector.class).forEach(selector -> {
		// });

	}

	private void appendTestsFromUniqueId(UniqueId uniqueId, TestDescriptor engineDescriptor) {
		if (!uniqueId.getEngineId().isPresent()) {
			LOG.warning(() -> "Cannot discover tests from unique ID without engine ID");
			return;
		}
		String engineId = uniqueId.getEngineId().get();
		if (!engineId.equals(engineDescriptor.getUniqueId().getEngineId().get())) {
			LOG.warning(() -> String.format("Cannot discover tests for engine '%s'", engineId));
			return;
		}
		List<UniqueId.Segment> segmentsWithoutEngine = getUniqueIdSegmentsWithoutEngine(uniqueId);
		resolveUniqueIdSegments(segmentsWithoutEngine, engineDescriptor);
	}

	private List<UniqueId.Segment> getUniqueIdSegmentsWithoutEngine(UniqueId uniqueId) {
		List<UniqueId.Segment> segmentsWithoutEngine = uniqueId.getSegments();
		segmentsWithoutEngine.remove(0);
		return segmentsWithoutEngine;
	}

	private void resolveUniqueIdSegments(List<UniqueId.Segment> segments, TestDescriptor parent) {
		if (segments.isEmpty())
			return;
		UniqueId.Segment next = segments.remove(0);
		switch (next.getType()) {
		case CONTAINER_SEGMENT_TYPE:
			boolean withChildren = segments.isEmpty();
			String className = next.getValue();
			Optional<Class<?>> optionalContainerClass = ReflectionUtils.loadClass(className);
			if (optionalContainerClass.isPresent()) {
				ContainerClassDescriptor descriptor = createClassDescriptor(optionalContainerClass.get(), parent, withChildren);
				parent.addChild(descriptor);
				resolveUniqueIdSegments(segments, descriptor);
			} else {
				LOG.warning(() -> String.format("Cannot resolve class '%s' from unique ID.", className));
				return;
			}
			break;
		case EXAMPLE_SEGMENT_TYPE:
			String methodName = next.getValue();
			Class<?> containerClass = ((ContainerClassDescriptor) parent).getContainerClass();
			Predicate<Method> isNamedExample = m -> IS_EXAMPLE_METHOD.test(m) && m.getName().equals(methodName);
			List<Method> exampleMethods = ReflectionSupport.findMethods(containerClass, isNamedExample, HierarchyTraversalMode.TOP_DOWN);
			if (exampleMethods.size() == 1) {
				Method exampleMethod = exampleMethods.get(0);
				ExampleMethodDescriptor descriptor = createExampleMethodDescriptor(parent.getUniqueId(), exampleMethod, containerClass);
				parent.addChild(descriptor);
				resolveUniqueIdSegments(segments, descriptor);
			} else if (exampleMethods.size() == 0) {
				LOG.warning(() -> String.format("Cannot resolve example '%s' from unique ID.", methodName));
				return;
			} else {
				LOG.warning(() -> String.format("Method name '%s' from unique ID is ambiguous.", methodName));
				return;
			}
			break;
		default:
			LOG.warning(() -> String.format("Cannot resolve unique ID segement '%s'.", next));
		}
	}

	private ExampleMethodDescriptor createExampleMethodDescriptor(UniqueId parentUniqueId, Method exampleMethod, Class<?> containerClass) {
		UniqueId uniqueId = parentUniqueId.append(EXAMPLE_SEGMENT_TYPE, exampleMethod.getName());
		return new ExampleMethodDescriptor(uniqueId, exampleMethod, containerClass);
	}

	private void appendTestFromMethod(Method javaMethod, Class<?> containerClass, TestDescriptor engineDescriptor) {
		if (IS_EXAMPLE_METHOD.test(javaMethod)) {
			ContainerClassDescriptor classDescriptor = createClassDescriptor(containerClass, engineDescriptor, false);
			classDescriptor.addChild(createExampleMethodDescriptor(classDescriptor.getUniqueId(), javaMethod, containerClass));
			engineDescriptor.addChild(classDescriptor);
		}
	}

	private void appendTestsInClass(Class<?> javaClass, TestDescriptor engineDescriptor) {
		if (IS_JQWIK_CONTAINER_CLASS.test(javaClass))
			engineDescriptor.addChild(createClassDescriptor(javaClass, engineDescriptor, true));
	}

	private void appendTestsInPackage(String packageName, TestDescriptor engineDescriptor, Predicate<String> classNamePredicate) {
		ReflectionSupport.findAllClassesInPackage(packageName, IS_JQWIK_CONTAINER_CLASS, classNamePredicate).stream()
				.map(aClass -> createClassDescriptor(aClass, engineDescriptor, true)).forEach(engineDescriptor::addChild);
	}

	private ContainerClassDescriptor createClassDescriptor(Class<?> javaClass, TestDescriptor engineDescriptor, boolean withChildren) {
		UniqueId uniqueId = engineDescriptor.getUniqueId().append(CONTAINER_SEGMENT_TYPE, javaClass.getName());
		ContainerClassDescriptor classTestDescriptor = new ContainerClassDescriptor(uniqueId, javaClass);
		if (withChildren) {
			appendExamplesInContainerClass(javaClass, classTestDescriptor);
		}
		return classTestDescriptor;
	}

	private void appendExamplesInContainerClass(Class<?> containerClass, TestDescriptor classTestDescriptor) {
		Map<String, List<ExampleMethodDescriptor>> exampleDescriptorsByMethodName = ReflectionSupport
				.findMethods(containerClass, IS_EXAMPLE_METHOD, HierarchyTraversalMode.TOP_DOWN).stream()
				.map(method -> createExampleMethodDescriptor(classTestDescriptor.getUniqueId(), method, containerClass))
				.collect(Collectors.groupingBy(exampleDescriptor -> exampleDescriptor.getExampleMethod().getName()));

		exampleDescriptorsByMethodName.entrySet()
				.stream()
				.flatMap(entry -> exampleDescriptors(entry, containerClass))
				.forEach(classTestDescriptor::addChild);
	}

	private Stream<? extends TestDescriptor> exampleDescriptors(Map.Entry<String, List<ExampleMethodDescriptor>> entry,
																Class<?> containerClass) {
		String methodName = entry.getKey();
		List<ExampleMethodDescriptor> examples = entry.getValue();
		if (examples.size() == 1)
			return examples.stream();
		LOG.warning(
				() -> String.format("There is more than one @Example for '%s::%s'. Ignoring all.", containerClass.getName(), methodName));
		return IntStream.range(0, examples.size()).mapToObj(i -> new OverloadedExampleMethodDescriptor(examples.get(i), i));
	}

}
