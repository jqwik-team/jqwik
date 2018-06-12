package net.jqwik.support;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class GenericsClassContext {

	public static final GenericsClassContext NULL = new GenericsClassContext(null) {
		@Override
		protected Type resolveInSupertypes(TypeResolution typeResolution) {
			return typeResolution.type();
		}
	};

	private final Class<?> contextClass;
	private Map<GenericVariable, TypeResolution> resolutions = new HashMap<>();

	GenericsClassContext(Class<?> contextClass) {
		this.contextClass = contextClass;
	}

	public Class<?> contextClass() {
		return contextClass;
	}

	void addResolution(TypeVariable typeVariable, Type resolvedType, AnnotatedType annotatedType) {
		// TODO: Use annotatedType to keep annotations after resolution
		GenericVariable genericVariable = new GenericVariable(typeVariable);
		resolutions.put(genericVariable, new TypeResolution(resolvedType, true));
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
			return resolveVariable(typeResolution);
		}
		if (typeResolution.type() instanceof ParameterizedType) {
			return resolveParameterizedType(typeResolution);
		}
		return typeResolution;
	}

	private TypeResolution resolveParameterizedType(TypeResolution parameterizedTypeResolution) {
		ParameterizedType type = (ParameterizedType) parameterizedTypeResolution.type();
		List<TypeResolution> resolvedTypeArguments = Arrays
			.stream(type.getActualTypeArguments()) //
			.map(typeArgument -> resolveType(new TypeResolution(typeArgument, false))) //
			.collect(Collectors.toList());

		if (resolvedTypeArguments.stream().noneMatch(TypeResolution::typeHasChanged)) {
			return parameterizedTypeResolution;
		}
		ParameterizedTypeWrapper resolvedType = new ParameterizedTypeWrapper(type) {
			@Override
			public Type[] getActualTypeArguments() {
				return resolvedTypeArguments.stream().map(TypeResolution::type).toArray(Type[]::new);
			}
		};
		return new TypeResolution(resolvedType, true);
	}

	private TypeResolution resolveVariable(TypeResolution typeVariableResolution) {
		TypeVariable typeVariable = (TypeVariable) typeVariableResolution.type();
		GenericVariable variable = new GenericVariable(typeVariable);
		TypeResolution localResolution = resolveLocally(typeVariable, variable);
		Type resolvedType = resolveInSupertypes(localResolution);
		if (resolvedType == typeVariable) {
			return typeVariableResolution;
		}
		// Recursive resolution necessary for variables mapped on variables
		return resolveType(new TypeResolution(resolvedType, true));
	}

	private TypeResolution resolveLocally(TypeVariable typeVariable, GenericVariable variable) {
		return resolutions.getOrDefault(variable, new TypeResolution(typeVariable, false));
	}

	protected Type resolveInSupertypes(TypeResolution typeResolution) {
		return supertypeContexts() //
								   .map(context -> context.resolveType(typeResolution))
								   .filter(TypeResolution::typeHasChanged) //
								   .findFirst() //
								   .map(TypeResolution::type)
								   .orElse(typeResolution.type());
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
