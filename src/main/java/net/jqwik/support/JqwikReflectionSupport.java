package net.jqwik.support;

import net.jqwik.discovery.predicates.IsTopLevelClass;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class JqwikReflectionSupport {

	private final static IsTopLevelClass isTopLevelClass = new IsTopLevelClass();

	public static Optional<Class<?>> loadClass(String name) {
		return ReflectionUtils.loadClass(name);
	}

	public static Optional<Method> findMethod(Class<?> clazz, String methodName, String parameterTypeNames) {
		return ReflectionUtils.findMethod(clazz, methodName, parameterTypeNames);
	}

	public static Optional<Method> findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
		return ReflectionUtils.findMethod(clazz, methodName, parameterTypes);
	}

	public static <T> T newInstance(Class<T> clazz, Object... args) {
		return ReflectionUtils.newInstance(clazz, args);
	}

	public static Object invokeMethod(Method method, Object target, Object... args) {
		return ReflectionUtils.invokeMethod(method, target, args);
	}

	public static List<Class<?>> findNestedClasses(Class<?> clazz, Predicate<Class<?>> predicate) {
		return ReflectionUtils.findNestedClasses(clazz, predicate);
	}

	public static Stream<Object> streamAllInstances(Object inner) {
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
		return Arrays.stream(inner.getClass().getDeclaredFields())
				.filter(field -> field.getName().startsWith("this$"))
				.findFirst()
				.map(field -> {
					try {
						return makeAccessible(field).get(inner);
					}
					catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
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
	 */
	public static <T> T newInstanceWithDefaultConstructor(Class<T> clazz) {
		if (isTopLevelClass.test(clazz) || JqwikReflectionSupport.isStatic(clazz))
			return JqwikReflectionSupport.newInstance(clazz);
		else  {
			Object parentInstance = newInstanceWithDefaultConstructor(clazz.getDeclaringClass());
			return JqwikReflectionSupport.newInstance(clazz, parentInstance);
		}
	}



	public static Set<Path> getAllClasspathRootDirectories() {
		return ReflectionUtils.getAllClasspathRootDirectories();
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
