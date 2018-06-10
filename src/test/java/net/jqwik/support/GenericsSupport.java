package net.jqwik.support;

import java.util.*;

public class GenericsSupport {

	private static Map<Class, GenericsContext> contextsCache = new HashMap<>();

	public static GenericsContext contextFor(Class<?> contextClass) {
		return contextsCache.computeIfAbsent(contextClass, GenericsSupport::createContext);
	}

	private static GenericsContext createContext(Class<?> contextClass) {
		GenericsContext context = new GenericsContext(contextClass);
		addGenericSuperclass(contextClass, context);
		addGenericInterfaces(contextClass, context);
		return context;
	}

	private static void addGenericSuperclass(Class<?> contextClass, GenericsContext context) {
		context.addGenericSupertype(contextClass.getGenericSuperclass());
	}

	private static void addGenericInterfaces(Class<?> contextClass, GenericsContext context) {
		Arrays.stream(contextClass.getGenericInterfaces()).forEach(context::addGenericSupertype);
	}
}
