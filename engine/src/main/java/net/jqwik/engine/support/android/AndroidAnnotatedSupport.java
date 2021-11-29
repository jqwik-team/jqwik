package net.jqwik.engine.support.android;

import java.lang.reflect.*;
import java.util.*;

public class AndroidAnnotatedSupport {

	public static AnnotatedType[] getAnnotatedInterfaces(Class<?> clazz) {
		return clazz.getAnnotatedInterfaces();
		// return Arrays.stream(clazz.getInterfaces()).map(AndroidAnnotatedType::new).toArray(AnnotatedType[]::new);
	}

	public static AnnotatedType getAnnotatedSuperclass(Class<?> clazz) {
		return clazz.getAnnotatedSuperclass();
		// return new AndroidAnnotatedType(clazz.getSuperclass());
	}

	public static AnnotatedType getAnnotatedReturnType(Method method)  {
		return method.getAnnotatedReturnType();
		// return new AndroidAnnotatedType(method.getReturnType());
	}

}
