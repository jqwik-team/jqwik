package net.jqwik.support;

import java.lang.reflect.*;
import java.util.*;

public class GenericsSupport {

	private static Map<Class, GenericsClassContext> contextsCache = new HashMap<>();

	public static GenericsClassContext contextFor(Class<?> contextClass) {
		if (contextClass == null) {
			return GenericsClassContext.NULL;
		}
		return contextsCache.computeIfAbsent(contextClass, GenericsSupport::createContext);
	}

	private static GenericsClassContext createContext(Class<?> contextClass) {
		GenericsClassContext context = new GenericsClassContext(contextClass);
		addResolutionForSuperclass(contextClass, context);
		addResolutionForInterfaces(contextClass, context);
		return context;
	}

	private static void addResolutionForInterfaces(Class<?> contextClass, GenericsClassContext context) {
		Class<?>[] interfaces = contextClass.getInterfaces();
		Type[] genericInterfaces = contextClass.getGenericInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> supertype = interfaces[i];
			Type genericSupertype = genericInterfaces[i];
			addResolutionForSupertype(supertype, genericSupertype, context);
		}
	}

	private static void addResolutionForSuperclass(Class<?> contextClass, GenericsClassContext context) {
		addResolutionForSupertype(contextClass.getSuperclass(), contextClass.getGenericSuperclass(), context);
	}

	private static void addResolutionForSupertype(Class<?> supertype, Type genericSupertype, GenericsClassContext context) {
		if (genericSupertype instanceof ParameterizedType) {
			ParameterizedType genericParameterizedType = (ParameterizedType) genericSupertype;
			Type[] supertypeTypeArguments = genericParameterizedType.getActualTypeArguments();
			TypeVariable[] superclassTypeVariables = supertype.getTypeParameters();
			for (int i = 0; i < superclassTypeVariables.length; i++) {
				TypeVariable variable = superclassTypeVariables[i];
				Type resolvedType = supertypeTypeArguments[i];
				context.addResolution(variable, resolvedType);
			}
		}
	}
}
