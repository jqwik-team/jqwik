package net.jqwik.discovery;

import static org.junit.platform.commons.support.ReflectionSupport.*;
import static org.junit.platform.engine.support.filter.ClasspathScanningSupport.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.recording.*;
import org.junit.platform.commons.support.*;
import org.junit.platform.commons.util.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.*;

import net.jqwik.discovery.predicates.*;

public class JqwikDiscoverer {

	private static final IsScannableContainerClass isScannableTestClass = new IsScannableContainerClass();

	private final TestRunData testRunData;

	public JqwikDiscoverer(TestRunData testRunData) {
		this.testRunData = testRunData;
	}

	public void discover(EngineDiscoveryRequest request, TestDescriptor engineDescriptor) {
		HierarchicalJavaResolver javaElementsResolver = createHierarchicalResolver(engineDescriptor);
		Predicate<String> classNamePredicate = buildClassNamePredicate(request);

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
			Method testMethod;
			try {
				testMethod = selector.getJavaMethod();
			} catch (PreconditionViolationException methodNotFound) {
				// Hack: Work around bug in IDEA's Junit platform integration
				// Currently doesn't work due to an error in DefaultLauncher
				// TODO: Remove as soon as IDEA's fix has been released
				Predicate<Method> hasCorrectName = method -> method.getName().equals(selector.getMethodName());
				List<Method> methodWithFittingName = ReflectionSupport.findMethods(selector.getJavaClass(), hasCorrectName,
						HierarchyTraversalMode.BOTTOM_UP);
				if (methodWithFittingName.isEmpty())
					return;
				testMethod = methodWithFittingName.get(0);
			}
			javaElementsResolver.resolveMethod(selector.getJavaClass(), testMethod);
		});
		request.getSelectorsByType(UniqueIdSelector.class).forEach(selector -> {
			javaElementsResolver.resolveUniqueId(selector.getUniqueId());
		});
	}

	private HierarchicalJavaResolver createHierarchicalResolver(TestDescriptor engineDescriptor) {
		Set<ElementResolver> resolvers = new HashSet<>();
		resolvers.add(new TopLevelContainerResolver());
		resolvers.add(new GroupContainerResolver());
		resolvers.add(new PropertyMethodResolver(testRunData));
		return new HierarchicalJavaResolver(engineDescriptor, resolvers);
	}

}
