package net.jqwik.engine.discovery;

import java.lang.annotation.*;
import java.lang.reflect.*;

public class JqwikKotlinSupport {

	public static boolean isInternalKotlinMethod(Method method) {
		Class<?> aClass = method.getDeclaringClass();
		return isKotlinClass(aClass) && isKotlinInternal(method);
	}

	public static boolean isInnerKotlinMethod(Method method) {
		Class<?> aClass = method.getDeclaringClass();
		return isKotlinClass(aClass) && isKotlinSpecial(method);
	}

	private static boolean isKotlinInternal(Method method) {
		// Kotlin appends a module extension to internal method names
		// The exact text of this extension is not known (to me)
		return method.getName().lastIndexOf('$') > 0;
	}

	private static boolean isKotlinSpecial(Method method) {
		// Kotlin appends an extension separated by a hyphen to method names in special cases
		// e.g. when method has UInt parameter.
		// TODO: Find out what's really happening here
		return method.getName().lastIndexOf('-') > 0;
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

}
