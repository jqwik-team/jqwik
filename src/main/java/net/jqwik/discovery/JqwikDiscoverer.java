package net.jqwik.discovery;

import static org.junit.platform.commons.support.ReflectionSupport.*;
import static org.junit.platform.engine.support.filter.ClasspathScanningSupport.buildClassNamePredicate;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.discovery.*;

import net.jqwik.discovery.predicates.IsScannableContainerClass;

public class JqwikDiscoverer {

	private static final IsScannableContainerClass isScannableTestClass = new IsScannableContainerClass();

	public void discover(EngineDiscoveryRequest request, TestDescriptor engineDescriptor) {
		HierarchicalJavaResolver javaElementsResolver = createHierarchicalResolver(engineDescriptor);
		Predicate<String> classNamePredicate = buildClassNamePredicate(request);

		request.getSelectorsByType(ClasspathRootSelector.class).forEach(selector -> {
			findAllClassesInClasspathRoot(selector.getClasspathRoot(), isScannableTestClass,
				classNamePredicate).forEach(javaElementsResolver::resolveClass);
		});
		request.getSelectorsByType(PackageSelector.class).forEach(selector -> {
			findAllClassesInPackage(selector.getPackageName(), isScannableTestClass, classNamePredicate).forEach(
				javaElementsResolver::resolveClass);
		});
		request.getSelectorsByType(ClassSelector.class).forEach(selector -> {
			javaElementsResolver.resolveClass(selector.getJavaClass());
		});
		request.getSelectorsByType(MethodSelector.class).forEach(selector -> {
			javaElementsResolver.resolveMethod(selector.getJavaClass(), selector.getJavaMethod());
		});
		request.getSelectorsByType(UniqueIdSelector.class).forEach(selector -> {
			javaElementsResolver.resolveUniqueId(selector.getUniqueId());
		});
	}

	private HierarchicalJavaResolver createHierarchicalResolver(TestDescriptor engineDescriptor) {
		Set<ElementResolver> resolvers = new HashSet<>();
		resolvers.add(new ContainerClassResolver());
		resolvers.add(new ExampleMethodResolver());
		resolvers.add(new PropertyMethodResolver());
		return new HierarchicalJavaResolver(engineDescriptor, resolvers);
	}

}
