package net.jqwik.engine.discovery;

import java.lang.annotation.*;
import java.lang.reflect.*;

public class JqwikKotlinSupport {

	public static boolean isInternalKotlinMethod(Method method) {
		Class<?> aClass = method.getDeclaringClass();
		return isKotlinClass(aClass) && isKotlinInternal(method);
	}

	public static boolean isSpeciallyNamedKotlinMethod(Method method) {
		Class<?> aClass = method.getDeclaringClass();
		return isKotlinClass(aClass) && isKotlinSpecial(method);
	}

	private static boolean isKotlinInternal(Method method) {
		// Kotlin appends a module extension to internal method names
		// The exact text of this extension is not known (to me)
		return method.getName().lastIndexOf('$') > 0;
	}

	private static boolean isKotlinSpecial(Method method) {
		// Kotlin appends an 7-char-extension separated by a hyphen to method names in special cases
		// e.g. when method has UInt parameter, the original method is appended with '-WZ4Q5Ns'
		// TODO: Find out what's really happening here
		String name = method.getName();
		int lastIndexOfHyphen = name.lastIndexOf('-');
		return lastIndexOfHyphen == (name.length() - 8);
	}

	// Learned mechanism to detect Kotlin class in https://stackoverflow.com/a/39806722
	public static boolean isKotlinClass(Class<?> aClass) {
		for (Annotation annotation : aClass.getDeclaredAnnotations()) {
			if (annotation.annotationType().getTypeName().equals("kotlin.Metadata")) {
				return true;
			}
		}
		return false;
	}

	public static String nameWithoutInternalPart(Method targetMethod) {
		int lastDollarPosition = targetMethod.getName().lastIndexOf('$');
		return targetMethod.getName().substring(0, lastDollarPosition);
	}

	public static String nameWithoutSpecialPart(Method targetMethod) {
		int lastDollarPosition = targetMethod.getName().lastIndexOf('-');
		return targetMethod.getName().substring(0, lastDollarPosition);
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
