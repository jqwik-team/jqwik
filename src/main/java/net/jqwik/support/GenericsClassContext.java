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
		return type;
	}

	public Type resolveVariable(TypeVariable typeVariable) {
		GenericVariable variable = new GenericVariable(typeVariable);
		Type localResolution = resolutions.getOrDefault(variable, typeVariable);
		return resolveInSupertypes(localResolution);
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
	}
}
