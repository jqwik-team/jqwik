package net.jqwik.support;

import net.jqwik.api.*;

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
	private Set<Type> supertypes = new HashSet<>();
	private Map<GenericVariable, Type> resolutions = new HashMap<>();

	GenericsClassContext(Class<?> contextClass) {
		this.contextClass = contextClass;
	}

	public Set<Type> genericSupertypes() {
		return Collections.unmodifiableSet(supertypes);
	}

	public Class<?> contextClass() {
		return contextClass;
	}

	void addGenericSupertype(Type supertype) {
		if (supertype == null)
			return;
		if (supertype == Object.class)
			return;
		supertypes.add(supertype);
	}

	void addResolution(TypeVariable typeVariable, Type resolvedType) {
		GenericVariable genericVariable = new GenericVariable(typeVariable);
		resolutions.put(genericVariable, resolvedType);
	}

	@Override
	public String toString() {
		return String.format("GenericsContext(%s)", contextClass.getSimpleName());
	}

	public Type resolveParameter(Parameter parameter) {
		return resolveType(parameter.getParameterizedType());
	}

	public Type resolveType(Type type) {
		if (type instanceof TypeVariable) {
			return resolveVariable((TypeVariable) type);
		}
		if (type instanceof ParameterizedType) {
			return resolveParameterizedType((ParameterizedType) type);
		}
		return type;
	}

	private Type resolveParameterizedType(ParameterizedType type) {
		Type[] resolvedTypeArguments = Arrays
			.stream(type.getActualTypeArguments()) //
			.map(this::resolveType) //
			.toArray(Type[]::new);

		if (Arrays.equals(type.getActualTypeArguments(), resolvedTypeArguments)) {
			return type;
		}
		return new ParameterizedTypeWrapper(type) {
			@Override
			public Type[] getActualTypeArguments() {
				return resolvedTypeArguments;
			}
		};
	}

	public Type resolveVariable(TypeVariable typeVariable) {
		GenericVariable variable = new GenericVariable(typeVariable);
		Type localResolution = resolutions.getOrDefault(variable, typeVariable);
		Type resolvedType = resolveInSupertypes(localResolution);
		if (resolvedType == typeVariable) {
			return resolvedType;
		}
		// Recursive resolution necessary for variables mapped on variables
		return resolveType(resolvedType);
	}

	protected Type resolveInSupertypes(Type typeToResolve) {
		return supertypeContexts() //
								   .map(context -> Tuples.tuple(typeToResolve, context.resolveType(typeToResolve)))
								   .filter(tuple -> tuple.get1() != tuple.get2()) //
								   .map(Tuples.Tuple2::get2) //
								   .findFirst() //
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
