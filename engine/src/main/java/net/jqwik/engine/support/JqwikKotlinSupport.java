package net.jqwik.engine.support;

import java.lang.annotation.*;
import java.lang.reflect.*;

public class JqwikKotlinSupport {

	public static String javaOrKotlinName(Method targetMethod) {
		String name = targetMethod.getName();
		if (isKotlinClass(targetMethod.getDeclaringClass())) {
			if (isKotlinInternal(targetMethod)) {
				name = nameWithoutInternalPart(name);
			}
			if (isKotlinSpecial(targetMethod)) {
				name = nameWithoutSpecialPart(name);
			}
		}
		return name;
	}

	private static boolean isKotlinInternal(Method method) {
		if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
			return false;
		}
		// Kotlin appends "$kotlin" to internal method names in Java bytecode
		return method.getName().endsWith("$kotlin");
	}

	private static boolean isKotlinSpecial(Method method) {
		// Kotlin appends a 7-char-extension separated by a hyphen to method names in special cases
		// e.g. when method has UInt parameter, the original method is appended with '-WZ4Q5Ns'
		// TODO: Find out what's really happening here
		String name = isKotlinInternal(method) ? nameWithoutInternalPart(method.getName()): method.getName();
		int lastIndexOfHyphen = name.lastIndexOf('-');
		return lastIndexOfHyphen >= 0 && lastIndexOfHyphen == (name.length() - 8);
	}

	// Learned mechanism to detect Kotlin class in https://stackoverflow.com/a/39806722
	private static boolean isKotlinClass(Class<?> aClass) {
		for (Annotation annotation : aClass.getDeclaredAnnotations()) {
			if (annotation.annotationType().getTypeName().equals("kotlin.Metadata")) {
				return true;
			}
		}
		return false;
	}

	private static String nameWithoutInternalPart(String name) {
		int lastDollarPosition = name.lastIndexOf('$');
		return name.substring(0, lastDollarPosition);
	}

	private static String nameWithoutSpecialPart(String name) {
		int lastDollarPosition = name.lastIndexOf('-');
		return name.substring(0, lastDollarPosition);
	}

	// Kotlin constructor parameters can have default values.
	// Those will generate overloaded constructors for this class.
	// In version 1.5.31 this means that there is a parameter present of type
	// kotlin.jvm.internal.DefaultConstructorMarker
	public static boolean isOverloadedConstructor(Constructor<?> constructor) {
		for (Class<?> parameterType : constructor.getParameterTypes()) {
			if (parameterType.getName().contains("DefaultConstructorMarker")) {
				return true;
			}
		}
		return false;
	}
}
