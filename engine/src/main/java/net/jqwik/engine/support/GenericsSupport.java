package net.jqwik.engine.support;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.providers.*;

public class GenericsSupport {

	private static Map<TypeUsage, GenericsClassContext> contextsCache = new HashMap<>();

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

		List<TypeUsage> typeArguments = typeUsage.getTypeArguments();
		Type[] supertypeTypeArguments = new Type[typeArguments.size()];
		for (int i = 0; i < typeArguments.size(); i++) {
			supertypeTypeArguments[i] = typeArguments.get(i).getType();
		}
		TypeVariable[] superclassTypeVariables = typeUsage.getRawType().getTypeParameters();
		for (int i = 0; i < superclassTypeVariables.length; i++) {
			TypeVariable variable = superclassTypeVariables[i];
			Type resolvedType = supertypeTypeArguments[i];
			// TODO: Is there some useful annotated type somewhere?
			AnnotatedType annotatedType = null;
			context.addResolution(variable, resolvedType, annotatedType);
		}
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
