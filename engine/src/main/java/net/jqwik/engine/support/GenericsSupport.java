package net.jqwik.engine.support;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.providers.*;

public class GenericsSupport {

	private static final Map<TypeUsage, GenericsClassContext> contextsCache = new LinkedHashMap<>();

	/**
	 * Return a context object which can resolve generic types for a given {@code contextClass}.
	 * <p>
	 * Must be synchronized because of caching.
	 *
	 * @param contextClass The class to wrap in a context
	 * @return a potentially cached context object
	 */
	public synchronized static GenericsClassContext contextFor(Class<?> contextClass) {
		if (contextClass == null) {
			return GenericsClassContext.NULL;
		}
		return contextFor(TypeUsage.of(contextClass));
	}

	public synchronized static GenericsClassContext contextFor(TypeUsage typeUsage) {
		return contextsCache.computeIfAbsent(typeUsage, GenericsSupport::createContext);
	}

	private static GenericsClassContext createContext(TypeUsage typeUsage) {
		GenericsClassContext context = new GenericsClassContext(typeUsage);
		addOwnResolutions(typeUsage, context);
		addResolutionsForSuperclass(typeUsage, context);
		addResolutionsForInterfaces(typeUsage, context);
		return context;
	}

	private static void addResolutionsForInterfaces(TypeUsage contextType, GenericsClassContext context) {
		Class<?>[] interfaces = contextType.getRawType().getInterfaces();
		Type[] genericInterfaces = contextType.getRawType().getGenericInterfaces();
		AnnotatedType[] annotatedInterfaces = contextType.getRawType().getAnnotatedInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> supertype = interfaces[i];
			Type genericSupertype = genericInterfaces[i];
			AnnotatedType annotatedSupertype = annotatedInterfaces[i];
			addResolutionsForSupertype(supertype, genericSupertype, annotatedSupertype, context);
		}
	}

	private static void addResolutionsForSuperclass(TypeUsage typeUsage, GenericsClassContext context) {
		addResolutionsForSupertype(
			typeUsage.getRawType().getSuperclass(),
			typeUsage.getRawType().getGenericSuperclass(),
			typeUsage.getRawType().getAnnotatedSuperclass(),
			context
		);
	}

	private static void addOwnResolutions(TypeUsage typeUsage, GenericsClassContext context) {
		if (typeUsage.getTypeArguments().isEmpty()) {
			return;
		}

		List<TypeUsage> typeArgumentsList = typeUsage.getTypeArguments();
		Type[] typeArguments = new Type[typeArgumentsList.size()];
		AnnotatedType[] annotatedTypeVariables = new AnnotatedType[typeArgumentsList.size()];
		for (int i = 0; i < typeArgumentsList.size(); i++) {
			typeArguments[i] = typeArgumentsList.get(i).getType();
			annotatedTypeVariables[i] = typeArgumentsList.get(i).getAnnotatedType();
		}
		TypeVariable[] typeVariables = typeUsage.getRawType().getTypeParameters();
		addResolutions(context, typeArguments, typeVariables, annotatedTypeVariables);
	}

	private static void addResolutionsForSupertype(
		Class<?> supertype,
		Type genericSupertype,
		AnnotatedType annotatedSupertype,
		GenericsClassContext context
	) {
		if (!(genericSupertype instanceof ParameterizedType)) {
			return;
		}
		ParameterizedType genericParameterizedType = (ParameterizedType) genericSupertype;
		Type[] typeArguments = genericParameterizedType.getActualTypeArguments();
		TypeVariable[] typeVariables = supertype.getTypeParameters();
		AnnotatedType[] annotatedTypeVariables =
			((AnnotatedParameterizedType) annotatedSupertype).getAnnotatedActualTypeArguments();
		addResolutions(context, typeArguments, typeVariables, annotatedTypeVariables);
	}

	private static void addResolutions(
		GenericsClassContext context,
		Type[] typeArguments,
		TypeVariable[] typeVariables,
		AnnotatedType[] annotatedTypeVariables
	) {
		for (int i = 0; i < typeVariables.length; i++) {
			TypeVariable variable = typeVariables[i];
			Type resolvedType = typeArguments[i];
			AnnotatedType annotatedType = annotatedTypeVariables[i];
			context.addResolution(variable, resolvedType, annotatedType);
		}
	}

}
