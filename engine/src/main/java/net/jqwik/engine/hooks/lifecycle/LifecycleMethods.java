package net.jqwik.engine.hooks.lifecycle;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

import static org.junit.platform.commons.support.AnnotationSupport.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

class LifecycleMethods {

	private LifecycleMethods() { }

	private static final ClassValue<List<Method>> findBeforeContainerMethods = new ClassValue<List<Method>>() {
		@Override
		protected List<Method> computeValue(Class<?> testClass) {
			return findMethods(testClass, true, true, BeforeContainer.class, HierarchyTraversalMode.TOP_DOWN);
		}
	};

	static List<Method> findBeforeContainerMethods(Class<?> testClass) {
		return findBeforeContainerMethods.get(testClass);
	}

	private static final ClassValue<List<Method>> findAfterContainerMethods = new ClassValue<List<Method>>() {
		@Override
		protected List<Method> computeValue(Class<?> testClass) {
			return findMethods(testClass, true, true, AfterContainer.class, HierarchyTraversalMode.BOTTOM_UP);
		}
	};

	static List<Method> findAfterContainerMethods(Class<?> testClass) {
		return findAfterContainerMethods.get(testClass);
	}

	private static final ClassValue<List<Method>> findBeforePropertyMethods = new ClassValue<List<Method>>() {
		@Override
		protected List<Method> computeValue(Class<?> testClass) {
			return findMethods(testClass, false, true, BeforeProperty.class, HierarchyTraversalMode.TOP_DOWN);
		}
	};

	static List<Method> findBeforePropertyMethods(Class<?> testClass) {
		return findBeforePropertyMethods.get(testClass);
	}

	private static final ClassValue<List<Method>> findAfterPropertyMethods = new ClassValue<List<Method>>() {
		@Override
		protected List<Method> computeValue(Class<?> testClass) {
			return findMethods(testClass, false, true, AfterProperty.class, HierarchyTraversalMode.BOTTOM_UP);
		}
	};

	static List<Method> findAfterPropertyMethods(Class<?> testClass) {
		return findAfterPropertyMethods.get(testClass);
	}

	private static final ClassValue<List<Method>> findBeforeTryMethods = new ClassValue<List<Method>>() {
		@Override
		protected List<Method> computeValue(Class<?> testClass) {
			return findMethods(testClass, false, true, BeforeTry.class, HierarchyTraversalMode.TOP_DOWN);
		}
	};

	static List<Method> findBeforeTryMethods(Class<?> testClass) {
		return findBeforeTryMethods.get(testClass);
	}

	private static final ClassValue<List<Method>> findAfterTryMethods = new ClassValue<List<Method>>() {
		@Override
		protected List<Method> computeValue(Class<?> testClass) {
			return findMethods(testClass, false, true, AfterTry.class, HierarchyTraversalMode.BOTTOM_UP);
		}
	};

	static List<Method> findAfterTryMethods(Class<?> testClass) {
		return findAfterTryMethods.get(testClass);
	}

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

	private static void assertNoForAllParams(Class<? extends Annotation> annotationType, Method method) {
		for (Parameter parameter : method.getParameters()) {
			AnnotationSupport.findAnnotation(parameter, ForAll.class).ifPresent(ignore -> {
				throw new JqwikException(String.format(
					"@%s method '%s' must not have parameters annotated with @ForAll.",
					annotationType.getSimpleName(), method.toGenericString()
				));
			});
		}
	}

	private static void assertNoAddLifecycleHookAnnotations(Class<? extends Annotation> annotationType, Method method) {
			AnnotationSupport.findAnnotation(method, AddLifecycleHook.class).ifPresent(annotation -> {
				throw new JqwikException(String.format(
					"@%s method '%s' does not support lifecycle hook annotation: [%s].",
					annotationType.getSimpleName(), method.toGenericString(), annotation
				));
			});
	}

	private static void assertNoParams(Class<? extends Annotation> annotationType, Method method) {
		if (method.getParameterCount() > 0) {
			throw new JqwikException(String.format(
				"@%s method '%s' must not have parameters.",
				annotationType.getSimpleName(), method.toGenericString()
			));
		}
	}

	private static List<Method> findMethods(
		Class<?> testClass,
		boolean mustBeStatic,
		boolean canHaveParameters,
		Class<? extends Annotation> annotationType,
		HierarchyTraversalMode traversalMode
	) {
		List<Method> methods = findMethodsAndCheckVoidReturnType(testClass, mustBeStatic, annotationType, traversalMode);
		if (mustBeStatic) {
			methods.forEach(method -> assertStatic(annotationType, method));
		} else {
			methods.forEach(method -> assertNonStatic(annotationType, method));
		}
		methods.forEach(method -> assertNoForAllParams(annotationType, method));
		methods.forEach(method -> assertNoAddLifecycleHookAnnotations(annotationType, method));
		if (!canHaveParameters) {
			methods.forEach(method -> assertNoParams(annotationType, method));
		}
		return methods;
	}

	private static List<Method> findMethodsAndCheckVoidReturnType(
		Class<?> testClass,
		boolean mustBeStatic,
		Class<? extends Annotation> annotationType,
		HierarchyTraversalMode traversalMode
	) {
		List<Method> methods =
			mustBeStatic ?
				findStaticMethods(testClass, annotationType, traversalMode) :
				findNonStaticMethods(testClass, annotationType, traversalMode);

		methods.forEach(method -> assertVoid(annotationType, method));
		return methods;
	}

	private static List<Method> findStaticMethods(
		Class<?> testClass,
		Class<? extends Annotation> annotationType,
		HierarchyTraversalMode traversalMode
	) {
		return findAnnotatedMethods(testClass, annotationType, traversalMode);
	}

	private static List<Method> findNonStaticMethods(
		Class<?> testClass,
		Class<? extends Annotation> annotationType,
		HierarchyTraversalMode traversalMode
	) {
		Predicate<Method> isAnnotated = method -> isAnnotated(method, annotationType);
		return JqwikReflectionSupport.findMethodsPotentiallyOuter(testClass, isAnnotated, traversalMode);
	}

}
