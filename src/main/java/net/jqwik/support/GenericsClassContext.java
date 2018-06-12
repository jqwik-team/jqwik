package net.jqwik.support;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class GenericsClassContext {

	public static final GenericsClassContext NULL = new GenericsClassContext(null) {
		@Override
		protected TypeResolution resolveInSupertypes(TypeResolution typeResolution) {
			return typeResolution.unchanged();
		}

		@Override
		public String toString() {
			return "GenericsContext(null)";
		}
	};

	private final Class<?> contextClass;
	private Map<LookupTypeVariable, TypeResolution> resolutions = new HashMap<>();

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
		return String.format("GenericsContext(%s)", contextClass.getSimpleName());
	}

	public TypeResolution resolveParameter(Parameter parameter) {
		TypeResolution initial = new TypeResolution(parameter.getParameterizedType(), parameter.getAnnotatedType(), false);
		return resolveType(initial);
	}

	private TypeResolution resolveType(TypeResolution typeResolution) {
		if (typeResolution.type() instanceof TypeVariable) {
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
		AnnotatedType[] annotatedActualTypeArguments = annotatedType.getAnnotatedActualTypeArguments();

		List<TypeResolution> resolvedTypeArguments = new ArrayList<>();
		for (int i = 0; i < actualTypeArguments.length; i++) {
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
		TypeVariable typeVariable = (TypeVariable) typeVariableResolution.type();
		LookupTypeVariable variable = new LookupTypeVariable(typeVariable);
		TypeResolution localResolution = resolveLocally(typeVariableResolution, variable);
		TypeResolution supertypeResolution = resolveInSupertypes(localResolution);
		if (!supertypeResolution.typeHasChanged()) {
			return localResolution;
		}
		// Recursive resolution necessary for variables mapped on variables
		return resolveType(supertypeResolution);
	}

	private TypeResolution resolveLocally(TypeResolution typeResolution, LookupTypeVariable variable) {
		return resolutions.getOrDefault(variable, typeResolution.unchanged());
	}

	protected TypeResolution resolveInSupertypes(TypeResolution typeResolution) {
		return supertypeContexts() //
								   .map(context -> context.resolveType(typeResolution)) //
								   .filter(TypeResolution::typeHasChanged) //
								   .findFirst() //
								   .orElse(typeResolution.unchanged());
	}

	private Stream<GenericsClassContext> supertypeContexts() {
		Stream<Class<?>> superclassStream = Stream.of(contextClass.getSuperclass());
		Stream<Class<?>> interfacesStream = Stream.of(contextClass.getInterfaces());
		return Stream.concat(superclassStream, interfacesStream).map(GenericsSupport::contextFor);
	}

	private static class LookupTypeVariable {
		private final String name;
		private final GenericDeclaration declaration;

		public LookupTypeVariable(TypeVariable typeVariable) {
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
			int result = name.hashCode();
			result = 31 * result + declaration.hashCode();
			return result;
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

		// This method exists since JDK 9. A breaking change :-(
		// @Override
		public AnnotatedType getAnnotatedOwnerType​() {
			// TODO: Make it do something useful. But what exactly?
			return null;
		}

		@Override
		public String toString() {
			String typeString = JqwikStringSupport.displayString(getType());
			String annotationsString = Arrays.stream(getAnnotations()) //
											   .map(JqwikStringSupport::displayString) //
											   .collect(Collectors.joining(", "));
			return String.format("%s %s", annotationsString, typeString);
		}

	}
}
