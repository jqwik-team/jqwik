package net.jqwik.engine.support;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class GenericsClassContext {

	static final GenericsClassContext NULL = new GenericsClassContext(null) {
		@Override
		public String toString() {
			return "GenericsContext(null)";
		}
	};

	private final Class<?> contextClass;
	private final Map<LookupTypeVariable, TypeResolution> resolutions = new LinkedHashMap<>();

	GenericsClassContext(Class<?> contextClass) {
		this.contextClass = contextClass;
	}

	public Class<?> contextClass() {
		return contextClass;
	}

	void addResolution(TypeVariable typeVariable, Type resolvedType, AnnotatedType annotatedType) {
		LookupTypeVariable genericVariable = new LookupTypeVariable(typeVariable);
		resolutions.put(genericVariable, new TypeResolution(resolvedType, annotatedType, true));
	}

	@Override
	public String toString() {
		return String.format("GenericsContext(%s)", contextClass);
	}

	public TypeResolution resolveParameter(Parameter parameter) {
		TypeResolution initial = new TypeResolution(parameter.getParameterizedType(), parameter.getAnnotatedType(), false);
		return resolveType(initial);
	}

	public TypeResolution resolveReturnType(Method method) {
		TypeResolution initial = new TypeResolution(method.getGenericReturnType(), method.getAnnotatedReturnType(), false);
		return resolveType(initial);
	}

	private TypeResolution resolveType(TypeResolution typeResolution) {
		if (typeResolution.isVariable()) {
			return resolveVariable(typeResolution);
		}
		if (typeResolution.type() instanceof ParameterizedType) {
			return resolveParameterizedType(typeResolution);
		}
		return typeResolution;
	}

	private TypeResolution resolveParameterizedType(TypeResolution parameterizedTypeResolution) {
		ParameterizedType type = (ParameterizedType) parameterizedTypeResolution.type();
		Type[] actualTypeArguments = type.getActualTypeArguments();

		AnnotatedParameterizedType annotatedType = (AnnotatedParameterizedType) parameterizedTypeResolution.annotatedType();
		// Sometimes a type resolution does not have an annotated type
		AnnotatedType[] annotatedActualTypeArguments = annotatedType == null
														   ? new AnnotatedType[0]
														   : annotatedType.getAnnotatedActualTypeArguments();

		int numberOfArguments = Math.min(annotatedActualTypeArguments.length, actualTypeArguments.length);
		List<TypeResolution> resolvedTypeArguments = new ArrayList<>();
		for (int i = 0; i < numberOfArguments; i++) {
			Type typeArgument = actualTypeArguments[i];
			AnnotatedType annotatedTypeArgument = annotatedActualTypeArguments[i];
			TypeResolution typeResolution = resolveType(new TypeResolution(typeArgument, annotatedTypeArgument, false));
			resolvedTypeArguments.add(typeResolution);
		}

		if (resolvedTypeArguments.stream().noneMatch(TypeResolution::typeHasChanged)) {
			return parameterizedTypeResolution;
		}
		ParameterizedTypeWrapper resolvedType = new ParameterizedTypeWrapper(type) {
			@Override
			public Type[] getActualTypeArguments() {
				return resolvedTypeArguments.stream().map(TypeResolution::type).toArray(Type[]::new);
			}
		};
		AnnotatedParameterizedType resolvedAnnotatedType = new AnnotatedParameterizedTypeWrapper(annotatedType) {
			@Override
			public Type getType() {
				return resolvedType;
			}

			@Override
			public AnnotatedType[] getAnnotatedActualTypeArguments() {
				return resolvedTypeArguments.stream().map(TypeResolution::annotatedType).toArray(AnnotatedType[]::new);
			}
		};
		return new TypeResolution(resolvedType, resolvedAnnotatedType, true);
	}

	private TypeResolution resolveVariable(TypeResolution typeVariableResolution) {
		TypeResolution localResolution = resolveVariableLocally(typeVariableResolution);
		if (localResolution.isVariable()) {
			TypeResolution supertypeResolution = resolveVariableInSupertypesOf(localResolution, contextClass);
			if (supertypeResolution.typeHasChanged()) {
				return resolveType(supertypeResolution);
			}
		}
		if (localResolution.typeHasChanged()) {
			return resolveType(localResolution);
		}
		return typeVariableResolution;
	}

	private TypeResolution resolveVariableLocally(TypeResolution typeResolution) {
		TypeVariable typeVariable = (TypeVariable) typeResolution.type();
		LookupTypeVariable variable = new LookupTypeVariable(typeVariable);
		return resolutions.getOrDefault(variable, typeResolution.unchanged());
	}

	private static TypeResolution resolveVariableInSupertypesOf(TypeResolution variableResolution, Class<?> clazz) {
		return resolveVariableInTypes(variableResolution, supertypesOf(clazz));
	}

	private static Collection<Class<?>> supertypesOf(Class<?> clazz) {
		if (clazz == null) {
			return Collections.emptySet();
		}
		Set<Class<?>> supertypes = new LinkedHashSet<>();
		supertypes.add(clazz.getSuperclass());
		supertypes.addAll(Arrays.asList(clazz.getInterfaces()));
		return supertypes;
	}

	private static TypeResolution resolveVariableInTypes(TypeResolution variableResolution, Collection<Class<?>> superTypes) {
		for (Class<?> superType : superTypes) {
			GenericsClassContext context = GenericsSupport.contextFor(superType);
			TypeResolution resolved = context.resolveVariableLocally(variableResolution);
			if (resolved.typeHasChanged()) {
				return resolved;
			}
		}
		for (Class<?> superType : superTypes) {
			TypeResolution typeResolution = resolveVariableInSupertypesOf(variableResolution, superType);
			if (typeResolution.typeHasChanged()) {
				return typeResolution;
			}
		}
		return variableResolution.unchanged();
	}

	private static class LookupTypeVariable {
		private final String name;
		private final GenericDeclaration declaration;

		private LookupTypeVariable(TypeVariable typeVariable) {
			this.name = typeVariable.getName();
			this.declaration = typeVariable.getGenericDeclaration();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			LookupTypeVariable that = (LookupTypeVariable) o;

			if (!name.equals(that.name)) return false;
			return declaration.equals(that.declaration);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, declaration);
		}

		@Override
		public String toString() {
			return String.format("<%s>", name);
		}
	}

	private static class ParameterizedTypeWrapper implements ParameterizedType {

		private final ParameterizedType wrapped;

		private ParameterizedTypeWrapper(ParameterizedType wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public Type[] getActualTypeArguments() {
			return wrapped.getActualTypeArguments();
		}

		@Override
		public Type getRawType() {
			return wrapped.getRawType();
		}

		@Override
		public Type getOwnerType() {
			return wrapped.getOwnerType();
		}

		@Override
		public String toString() {
			String baseString = JqwikStringSupport.displayString(getRawType());
			String typeArgumentsString = Arrays.stream(getActualTypeArguments()) //
											   .map(JqwikStringSupport::displayString) //
											   .collect(Collectors.joining(", "));
			return String.format("%s<%s>", baseString, typeArgumentsString);
		}
	}

	private static class AnnotatedParameterizedTypeWrapper implements AnnotatedParameterizedType {

		private final AnnotatedParameterizedType annotatedType;

		private AnnotatedParameterizedTypeWrapper(AnnotatedParameterizedType annotatedType) {
			this.annotatedType = annotatedType;
		}

		@Override
		public AnnotatedType[] getAnnotatedActualTypeArguments() {
			return annotatedType.getAnnotatedActualTypeArguments();
		}

		@Override
		public Type getType() {
			return annotatedType.getType();
		}

		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
			return annotatedType.getAnnotation(annotationClass);
		}

		@Override
		public Annotation[] getAnnotations() {
			return annotatedType.getAnnotations();
		}

		@Override
		public Annotation[] getDeclaredAnnotations() {
			return annotatedType.getDeclaredAnnotations();
		}

		// For compatibility with JDK >= 9. A breaking change in the JDK :-(
		// @Override
		@SuppressWarnings("Since15")
		public AnnotatedType getAnnotatedOwnerType() {
			// TODO: Return annotatedType.getAnnotatedOwnerType() as soon as Java >= 9 is being used
			return null;
		}

		@Override
		public String toString() {
			String typeString = JqwikStringSupport.displayString(getType());
			String annotationsString = Arrays.stream(getAnnotations())
											 .map(JqwikStringSupport::displayString)
											 .collect(Collectors.joining(", "));
			return String.format("%s %s", annotationsString, typeString);
		}

	}
}
