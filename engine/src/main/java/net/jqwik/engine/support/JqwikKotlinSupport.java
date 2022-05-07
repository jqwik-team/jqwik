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
			if (hasMangledName(targetMethod)) {
				name = nameWithoutInlinedParameterHashes(name);
			}
		}
		return name;
	}

	private static boolean isKotlinInternal(Method method) {
		if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
			return false;
		}
		// Kotlin appends "$<module-name>" to internal method names in Java bytecode
		// However, it's not necessarily the module name available from Class.getModule().getName() in java
		int lastDollarIndex = method.getName().lastIndexOf('$');
		return lastDollarIndex > 0 && lastDollarIndex < (method.getName().length() - 1);
	}

	private static boolean hasMangledName(Method method) {
		String name = isKotlinInternal(method) ? nameWithoutInternalPart(method.getName()): method.getName();
		return hasInlinedParameters(name);
	}

	private static boolean hasInlinedParameters(String name) {
		// Kotlin appends a 7-char-hash separated by a hyphen to method names
		// whenever the underlying Kotlin method uses inlined parameter type, e.g. UInt
		// See https://ncorti.com/blog/name-mangling-in-kotlin
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

	private static String nameWithoutInlinedParameterHashes(String name) {
		int lastDollarPosition = name.lastIndexOf('-');
		return name.substring(0, lastDollarPosition);
	}
}
