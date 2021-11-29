package net.jqwik.engine.support.android;

import java.lang.reflect.*;
import java.util.*;

public class AndroidAnnotatedSupport {

	public static AnnotatedType[] getAnnotatedInterfaces(Class<?> clazz) {
		try {
			return clazz.getAnnotatedInterfaces();
		} catch (NoSuchMethodError noSuchMethodError) {
			return Arrays.stream(clazz.getInterfaces()).map(AndroidAnnotatedType::new).toArray(AnnotatedType[]::new);
		}
	}

	public static AnnotatedType getAnnotatedSuperclass(Class<?> clazz) {
		try {
			return clazz.getAnnotatedSuperclass();
		} catch (NoSuchMethodError noSuchMethodError) {
			return new AndroidAnnotatedType(clazz.getSuperclass());
		}
	}

	public static AnnotatedType getAnnotatedReturnType(Method method) {
		try {
			return method.getAnnotatedReturnType();
		} catch (NoSuchMethodError noSuchMethodError) {
			return new AndroidAnnotatedType(method.getReturnType());
		}
	}

}
