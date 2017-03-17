package net.jqwik.support;

import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class JqwikReflectionSupport {

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
