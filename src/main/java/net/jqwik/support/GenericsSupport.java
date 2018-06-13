package net.jqwik.support;

import java.lang.reflect.*;
import java.util.*;

public class GenericsSupport {

	private static Map<Class, GenericsClassContext> contextsCache = new HashMap<>();

	/**
	 * Return a context object which can resolve generic types for a given {@code contextClass}.
	 *
	 * Must be synchronized because of caching.
	 *
	 * @param contextClass
	 * @return a potentially cached context object
	 */
	public synchronized static GenericsClassContext contextFor(Class<?> contextClass) {
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
		AnnotatedType[] annotatedInterfaces = contextClass.getAnnotatedInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> supertype = interfaces[i];
			Type genericSupertype = genericInterfaces[i];
			AnnotatedType annotatedSupertype = annotatedInterfaces[i];
			addResolutionForSupertype(supertype, genericSupertype, annotatedSupertype, context);
		}
	}

	private static void addResolutionForSuperclass(Class<?> contextClass, GenericsClassContext context) {
		addResolutionForSupertype(contextClass.getSuperclass(), contextClass.getGenericSuperclass(), contextClass.getAnnotatedSuperclass(), context);
	}

	private static void addResolutionForSupertype(Class<?> supertype, Type genericSupertype, AnnotatedType annotatedSupertype, GenericsClassContext context) {
		if (genericSupertype instanceof ParameterizedType) {
			ParameterizedType genericParameterizedType = (ParameterizedType) genericSupertype;
			Type[] supertypeTypeArguments = genericParameterizedType.getActualTypeArguments();
			TypeVariable[] superclassTypeVariables = supertype.getTypeParameters();
			AnnotatedType[] annotatedTypeVariables = ((AnnotatedParameterizedType) annotatedSupertype).getAnnotatedActualTypeArguments();
			for (int i = 0; i < superclassTypeVariables.length; i++) {
				TypeVariable variable = superclassTypeVariables[i];
				Type resolvedType = supertypeTypeArguments[i];
				AnnotatedType annotatedType = annotatedTypeVariables[i];
				context.addResolution(variable, resolvedType, annotatedType);
			}
		}
	}
}
