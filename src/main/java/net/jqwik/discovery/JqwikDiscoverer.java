package net.jqwik.discovery;

import net.jqwik.*;
import net.jqwik.discovery.predicates.*;
import net.jqwik.recording.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.*;

import java.util.*;
import java.util.function.*;

import static org.junit.platform.commons.support.ReflectionSupport.*;
import static org.junit.platform.engine.support.filter.ClasspathScanningSupport.*;

public class JqwikDiscoverer {

	private static final IsScannableContainerClass isScannableTestClass = new IsScannableContainerClass();

	private final TestRunData testRunData;
	private final PropertyDefaultValues propertyDefaultValues;

	public JqwikDiscoverer(TestRunData testRunData, PropertyDefaultValues propertyDefaultValues) {
		this.testRunData = testRunData;
		this.propertyDefaultValues = propertyDefaultValues;
	}

	public void discover(EngineDiscoveryRequest request, TestDescriptor engineDescriptor) {
		HierarchicalJavaResolver javaElementsResolver = createHierarchicalResolver(engineDescriptor);
		Predicate<String> classNamePredicate = buildClassNamePredicate(request);

		request.getSelectorsByType(ModuleSelector.class).forEach(selector -> {
			findAllClassesInModule(selector.getModuleName(), isScannableTestClass, classNamePredicate)
				.forEach(javaElementsResolver::resolveClass);
		});
		request.getSelectorsByType(ClasspathRootSelector.class).forEach(selector -> {
			findAllClassesInClasspathRoot(selector.getClasspathRoot(), isScannableTestClass, classNamePredicate)
					.forEach(javaElementsResolver::resolveClass);
		});
		request.getSelectorsByType(PackageSelector.class).forEach(selector -> {
			findAllClassesInPackage(selector.getPackageName(), isScannableTestClass, classNamePredicate)
					.forEach(javaElementsResolver::resolveClass);
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
		resolvers.add(new TopLevelContainerResolver());
		resolvers.add(new GroupContainerResolver());
		resolvers.add(new PropertyMethodResolver(testRunData, propertyDefaultValues));
		return new HierarchicalJavaResolver(engineDescriptor, resolvers);
	}

}
