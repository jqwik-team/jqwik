package net.jqwik.engine.discovery;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.*;

import net.jqwik.engine.*;
import net.jqwik.engine.discovery.predicates.*;
import net.jqwik.engine.recording.*;

import static org.junit.platform.commons.support.ReflectionSupport.*;
import static org.junit.platform.engine.Filter.*;

public class JqwikDiscoverer {

	private static final IsScannableContainerClass isScannableTestClass = new IsScannableContainerClass();

	private final TestRunData testRunData;
	private final PropertyAttributesDefaults propertyDefaultValues;

	public JqwikDiscoverer(TestRunData testRunData, PropertyAttributesDefaults propertyDefaultValues) {
		this.testRunData = testRunData;
		this.propertyDefaultValues = propertyDefaultValues;
	}

	public void discover(EngineDiscoveryRequest request, TestDescriptor engineDescriptor) {
		HierarchicalJavaResolver javaElementsResolver = createHierarchicalResolver(engineDescriptor);
		EngineDiscoveryListener discoveryListener = request.getDiscoveryListener();
		Predicate<String> classNamePredicate = buildClassNamePredicate(request);

		request.getSelectorsByType(ModuleSelector.class).forEach(selector -> {
			findAllClassesInModule(selector.getModuleName(), isScannableTestClass, classNamePredicate)
				.forEach(testClass -> {
					discoveryListener.selectorProcessed(
						engineDescriptor.getUniqueId(),
						selector,
						javaElementsResolver.resolveClass(testClass)
					);
				});
		});
		request.getSelectorsByType(ClasspathRootSelector.class).forEach(selector -> {
			findAllClassesInClasspathRoot(selector.getClasspathRoot(), isScannableTestClass, classNamePredicate)
				.forEach(testClass -> {
					discoveryListener.selectorProcessed(
						engineDescriptor.getUniqueId(),
						selector,
						javaElementsResolver.resolveClass(testClass)
					);
				});
		});
		request.getSelectorsByType(PackageSelector.class).forEach(selector -> {
			findAllClassesInPackage(selector.getPackageName(), isScannableTestClass, classNamePredicate)
				.forEach(testClass -> {
					discoveryListener.selectorProcessed(
						engineDescriptor.getUniqueId(),
						selector,
						javaElementsResolver.resolveClass(testClass)
					);
				});
		});
		request.getSelectorsByType(ClassSelector.class).forEach(selector -> {
			discoveryListener.selectorProcessed(
				engineDescriptor.getUniqueId(),
				selector,
				javaElementsResolver.resolveClass(selector.getJavaClass())
			);
		});
		request.getSelectorsByType(MethodSelector.class).forEach(selector -> {
			Method javaMethod = getJavaMethodIfNecessaryFromInnerKotlinClass(selector);
			discoveryListener.selectorProcessed(
				engineDescriptor.getUniqueId(),
				selector,
				javaElementsResolver.resolveMethod(selector.getJavaClass(), javaMethod)
			);
		});
		request.getSelectorsByType(UniqueIdSelector.class).forEach(selector -> {
			discoveryListener.selectorProcessed(
				engineDescriptor.getUniqueId(),
				selector,
				javaElementsResolver.resolveUniqueId(selector.getUniqueId())
			);
		});
	}

	private Method getJavaMethodIfNecessaryFromInnerKotlinClass(MethodSelector selector) {
		// This is for Kotlin methods within inner classes, the names of which have a unique postfix
		try {
			return selector.getJavaMethod();
		} catch (Exception methodNotFound) {
			String methodName = selector.getMethodName();
			for (Method method : selector.getJavaClass().getMethods()) {
				// TODO: Also match parameters
				if (matchesInnerKotlinMethod(method, methodName)) {
					return method;
				}
			}
			throw methodNotFound;
		}
	}

	private boolean matchesInnerKotlinMethod(Method method, String methodName) {
		if (!JqwikKotlinSupport.isInnerKotlinMethod(method)) {
			return false;
		}
		String plainMethodName = JqwikKotlinSupport.nameWithoutSpecialPart(method);
		return plainMethodName.equals(methodName);
	}

	private HierarchicalJavaResolver createHierarchicalResolver(TestDescriptor engineDescriptor) {
		Set<ElementResolver> resolvers = new HashSet<>();
		resolvers.add(new TopLevelContainerResolver());
		resolvers.add(new GroupContainerResolver());
		resolvers.add(new PropertyMethodResolver(testRunData, propertyDefaultValues));
		return new HierarchicalJavaResolver(engineDescriptor, resolvers);
	}

	private static Predicate<String> buildClassNamePredicate(EngineDiscoveryRequest request) {
		List<DiscoveryFilter<String>> filters = new ArrayList<>();
		filters.addAll(request.getFiltersByType(ClassNameFilter.class));
		filters.addAll(request.getFiltersByType(PackageNameFilter.class));
		return composeFilters(filters).toPredicate();
	}

}
