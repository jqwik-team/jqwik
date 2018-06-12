package net.jqwik.support;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class GenericsClassContext {

	public static final GenericsClassContext NULL = new GenericsClassContext(null) {
		@Override
		protected Type resolveInSupertypes(Type typeToResolve) {
			return typeToResolve;
		}
	};

	private final Class<?> contextClass;
	private Map<GenericVariable, Type> resolutions = new HashMap<>();

	GenericsClassContext(Class<?> contextClass) {
		this.contextClass = contextClass;
	}


	public Class<?> contextClass() {
		return contextClass;
	}

	void addResolution(TypeVariable typeVariable, Type resolvedType, AnnotatedType annotatedType) {
		// TODO: Use annotatedType to keep annotations after resolution
		GenericVariable genericVariable = new GenericVariable(typeVariable);
		resolutions.put(genericVariable, resolvedType);
	}

	@Override
	public String toString() {
		return String.format("GenericsContext(%s)", contextClass.getSimpleName());
	}

	public TypeResolution resolveParameter(Parameter parameter) {
		TypeResolution initial = new TypeResolution(parameter.getParameterizedType(), false);
		return resolveType(initial);
	}

	private TypeResolution resolveType(TypeResolution typeResolution) {
		if (typeResolution.type() instanceof TypeVariable) {
			return typeResolution.then(resolveVariable((TypeVariable) typeResolution.type()));
		}
		if (typeResolution.type() instanceof ParameterizedType) {
			return typeResolution.then(resolveParameterizedType((ParameterizedType) typeResolution.type()));
		}
		return typeResolution;
	}

	private TypeResolution resolveParameterizedType(ParameterizedType type) {
		Type[] resolvedTypeArguments = Arrays
			.stream(type.getActualTypeArguments()) //
			.map(typeArgument -> resolveType(new TypeResolution(typeArgument, false)).type()) //
			.toArray(Type[]::new);

		if (Arrays.equals(type.getActualTypeArguments(), resolvedTypeArguments)) {
			return new TypeResolution(type, false);
		}
		ParameterizedTypeWrapper resolvedType = new ParameterizedTypeWrapper(type) {
			@Override
			public Type[] getActualTypeArguments() {
				return resolvedTypeArguments;
			}
		};
		return new TypeResolution(resolvedType, true);
	}

	private TypeResolution resolveVariable(TypeVariable typeVariable) {
		GenericVariable variable = new GenericVariable(typeVariable);
		Type localResolution = resolutions.getOrDefault(variable, typeVariable);
		Type resolvedType = resolveInSupertypes(localResolution);
		if (resolvedType == typeVariable) {
			return new TypeResolution(resolvedType, false);
		}
		// Recursive resolution necessary for variables mapped on variables
		return resolveType(new TypeResolution(resolvedType, true));
	}

	protected Type resolveInSupertypes(Type typeToResolve) {
		return supertypeContexts() //
								   .map(context -> context.resolveType(new TypeResolution(typeToResolve, false)))
								   .filter(TypeResolution::typeHasChanged) //
								   .findFirst() //
								   .map(TypeResolution::type)
								   .orElse(typeToResolve);
	}

	private Stream<GenericsClassContext> supertypeContexts() {
		Stream<Class<?>> superclassStream = Stream.of(contextClass.getSuperclass());
		Stream<Class<?>> interfacesStream = Stream.of(contextClass.getInterfaces());
		return Stream.concat(superclassStream, interfacesStream).map(GenericsSupport::contextFor);
	}

	private static class GenericVariable {
		private final String name;
		private final GenericDeclaration declaration;

		public GenericVariable(TypeVariable typeVariable) {
			this.name = typeVariable.getName();
			this.declaration = typeVariable.getGenericDeclaration();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			GenericVariable that = (GenericVariable) o;

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

		public ParameterizedTypeWrapper(ParameterizedType wrapped) {
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
}
