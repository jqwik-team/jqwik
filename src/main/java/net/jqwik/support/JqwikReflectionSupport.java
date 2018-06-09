package net.jqwik.support;

import net.jqwik.discovery.predicates.*;
import org.junit.platform.commons.support.*;
import ru.vyarus.java.generics.resolver.*;
import ru.vyarus.java.generics.resolver.context.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

public class JqwikReflectionSupport {

	private final static IsTopLevelClass isTopLevelClass = new IsTopLevelClass();

	public static Stream<Object> streamInnerInstances(Object inner) {
		return addInstances(inner, new ArrayList<>()).stream();
	}

	private static List<Object> addInstances(Object inner, List<Object> instances) {
		instances.add(inner);
		Optional<Object> outer = getOuterInstance(inner);
		outer.ifPresent(o -> addInstances(o, instances));
		return instances;
	}

	private static Optional<Object> getOuterInstance(Object inner) {
		// This is risky since it depends on the name of the field which is nowhere guaranteed
		// but has been stable so far in all JDKs

		// @formatter:off
		return Arrays.stream(inner.getClass().getDeclaredFields()).filter(field -> field.getName().startsWith("this$")).findFirst()
				.map(field -> {
					try {
						return makeAccessible(field).get(inner);
					} catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
						return Optional.empty();
					}
				});
		// @formatter:on
	}

	private static <T extends AccessibleObject> T makeAccessible(T object) {
		if (!object.isAccessible()) {
			object.setAccessible(true);
		}
		return object;
	}

	/**
	 * Create instance of a class that can potentially be a non static inner class
	 *
	 * @param <T>
	 *            The type of the instance to create
	 * @param clazz
	 *            The class to instantiate
	 *
	 * @return the instance
	 */
	public static <T> T newInstanceWithDefaultConstructor(Class<T> clazz) {
		if (isTopLevelClass.test(clazz) || JqwikReflectionSupport.isStatic(clazz))
			return ReflectionSupport.newInstance(clazz);
		else {
			Object parentInstance = newInstanceWithDefaultConstructor(clazz.getDeclaringClass());
			return ReflectionSupport.newInstance(clazz, parentInstance);
		}
	}

	/**
	 * Find all {@linkplain Method methods} as in ReflectionSupport.findMethods(..) but also use outer classes to look for
	 * methods.
	 *
	 * @param clazz
	 *            The class in which you start the search
	 * @param predicate
	 *            The condition to check for all candidate methods
	 * @param traversalMode
	 *            Traverse hierarchy up or down. Determines the order in resulting list.
	 *
	 * @return List of found methods
	 */
	public static List<Method> findMethodsPotentiallyOuter(Class<?> clazz, Predicate<Method> predicate,
														   HierarchyTraversalMode traversalMode
	) {

		List<Method> foundMethods = new ArrayList<>();
		foundMethods.addAll(ReflectionSupport.findMethods(clazz, predicate, traversalMode));
		Class<?> searchClass = clazz;
		while (searchClass.getDeclaringClass() != null) {
			searchClass = searchClass.getDeclaringClass();
			foundMethods.addAll(ReflectionSupport.findMethods(searchClass, predicate, traversalMode));
		}
		return foundMethods;
	}

	/**
	 * Invoke the supplied {@linkplain Method method} as in ReflectionSupport.invokeMethod(..) but potentially use the outer
	 * instance if the method belongs to the outer instance of an object.
	 *
	 * @param method
	 *            The method to invoke
	 * @param target
	 *            The object to invoke the method on
	 * @param args
	 *            The arguments of the method invocation
	 *
	 * @return Result of method invocation if there is one, otherwise null
	 */
	public static Object invokeMethodPotentiallyOuter(Method method, Object target, Object... args) {
		if (method.getDeclaringClass().isAssignableFrom(target.getClass())) {
			return ReflectionSupport.invokeMethod(method, target, args);
		} else {
			if (target.getClass().getDeclaringClass() != null) {
				Optional<Object> newTarget = getOuterInstance(target);
				if (newTarget.isPresent()) {
					return invokeMethodPotentiallyOuter(method, newTarget.get(), args);
				}
			}
			throw new IllegalArgumentException(String.format("Method [%s] cannot be invoked on target [%s].", method, target));
		}
	}

	public static Set<Path> getAllClasspathRootDirectories() {
		// TODO: This is quite a hack, since sometimes the classpath is quite different.
		// Especially under Java 9's module system this will probably no longer work.
		String classpath = System.getProperty("java.class.path");
		return Arrays.stream(classpath.split(File.pathSeparator)) //
					 .map(Paths::get).filter(Files::isDirectory) //
					 .collect(toSet());
	}

	public static MethodParameter[] getMethodParameters(Method method, Class<?> containerClass) {

		List<MethodParameter> list = new ArrayList<>();
		Parameter[] parameters = method.getParameters();

		List<Type> resolvedGenericParameters = resolveGenericParameters(method, containerClass);

		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			Type resolvedParameter = resolvedGenericParameters.get(i);
			MethodParameter methodParameter = new MethodParameter(parameter, resolvedParameter);
			list.add(methodParameter);
		}
		return list.toArray(new MethodParameter[parameters.length]);
	}

	private static List<Type> resolveGenericParameters(Method method, Class<?> containerClass) {

		// TODO: GenericsContext cannot resolve parameter who are generic types themselves, e.g. List<T>
		//       I probably have to build my own generics resolver
		GenericsContext context = GenericsResolver.resolve(containerClass).type(method.getDeclaringClass());
		MethodGenericsContext methodContext = context.method(method);
		List<Class<?>> resolvedParameters = methodContext.resolveParameters();
		Parameter[] parameters = method.getParameters();

		List<Type> genericParameters = new ArrayList<>();
		for (int i = 0; i < resolvedParameters.size(); i++) {
			Class<?> resolvedParameter = resolvedParameters.get(i);
			if (resolvedParameter == parameters[i].getType()) {
				genericParameters.add(null);
			} else {
				genericParameters.add(resolvedParameter);
			}
		}

		return genericParameters;

	}

	public static boolean isPublic(Class<?> clazz) {
		return Modifier.isPublic(clazz.getModifiers());
	}

	public static boolean isPublic(Member member) {
		return Modifier.isPublic(member.getModifiers());
	}

	public static boolean isPrivate(Class<?> clazz) {
		return Modifier.isPrivate(clazz.getModifiers());
	}

	public static boolean isPrivate(Member member) {
		return Modifier.isPrivate(member.getModifiers());
	}

	public static boolean isAbstract(Class<?> clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	public static boolean isAbstract(Member member) {
		return Modifier.isAbstract(member.getModifiers());
	}

	public static boolean isStatic(Class<?> clazz) {
		return Modifier.isStatic(clazz.getModifiers());
	}

	public static boolean isStatic(Member member) {
		return Modifier.isStatic(member.getModifiers());
	}

}
