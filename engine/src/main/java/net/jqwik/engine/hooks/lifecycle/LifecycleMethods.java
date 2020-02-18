package net.jqwik.engine.hooks.lifecycle;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

import static org.junit.platform.commons.support.AnnotationSupport.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

final class LifecycleMethods {

	private LifecycleMethods() {
		/* no-op */
	}

	static List<Method> findBeforeContainerMethods(Class<?> testClass) {
		return findMethodsAndAssertStatic(testClass, true, BeforeContainer.class, HierarchyTraversalMode.TOP_DOWN);
	}

	static List<Method> findAfterContainerMethods(Class<?> testClass) {
		return findMethodsAndAssertStatic(testClass, true, AfterContainer.class, HierarchyTraversalMode.BOTTOM_UP);
	}

//	static List<Method> findBeforeEachMethods(Class<?> testClass) {
//		return findMethodsAndAssertNonStatic(testClass, BeforeEach.class, HierarchyTraversalMode.TOP_DOWN);
//	}
//
//	static List<Method> findAfterEachMethods(Class<?> testClass) {
//		return findMethodsAndAssertNonStatic(testClass, AfterEach.class, HierarchyTraversalMode.BOTTOM_UP);
//	}

	private static void assertStatic(Class<? extends Annotation> annotationType, Method method) {
		if (JqwikReflectionSupport.isNotStatic(method)) {
			throw new JqwikException(String.format(
				"@%s method '%s' must be static.",
				annotationType.getSimpleName(), method.toGenericString()
			));
		}
	}

	private static void assertNonStatic(Class<? extends Annotation> annotationType, Method method) {
		if (JqwikReflectionSupport.isStatic(method)) {
			throw new JqwikException(String.format(
				"@%s method '%s' must not be static.",
				annotationType.getSimpleName(), method.toGenericString()
			));
		}
	}

	private static void assertVoid(Class<? extends Annotation> annotationType, Method method) {
		if (!returnsVoid(method)) {
			throw new JqwikException(String.format(
				"@%s method '%s' must not return a value.",
				annotationType.getSimpleName(), method.toGenericString()
			));
		}
	}

	private static List<Method> findMethodsAndAssertStatic(
		Class<?> testClass, boolean requireStatic,
		Class<? extends Annotation> annotationType, HierarchyTraversalMode traversalMode
	) {
		List<Method> methods = findMethodsAndCheckVoidReturnType(testClass, annotationType, traversalMode);
		if (requireStatic) {
			methods.forEach(method -> assertStatic(annotationType, method));
		}
		return methods;
	}

	private static List<Method> findMethodsAndAssertNonStatic(
		Class<?> testClass,
		Class<? extends Annotation> annotationType, HierarchyTraversalMode traversalMode
	) {
		List<Method> methods = findMethodsAndCheckVoidReturnType(testClass, annotationType, traversalMode);
		methods.forEach(method -> assertNonStatic(annotationType, method));
		return methods;
	}

	private static List<Method> findMethodsAndCheckVoidReturnType(
		Class<?> testClass,
		Class<? extends Annotation> annotationType, HierarchyTraversalMode traversalMode
	) {
		List<Method> methods = findAnnotatedMethods(testClass, annotationType, traversalMode);
		methods.forEach(method -> assertVoid(annotationType, method));
		return methods;
	}

}
