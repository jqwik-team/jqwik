package net.jqwik.engine.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultTypeArbitrary<T> extends ArbitraryDecorator<T> implements TypeArbitrary<T> {

	private TraverseArbitrary<T> traverseArbitrary;

	public DefaultTypeArbitrary(Class<T> targetType) {
		this.traverseArbitrary = new DefaultTraverseArbitrary<>(targetType, ignore -> Optional.empty()).useDefaults();
	}

	@Override
	protected Arbitrary<T> arbitrary() {
		return traverseArbitrary;
	}

	@Override
	public TypeArbitrary<T> use(Executable creator) {
		DefaultTypeArbitrary<T> clone = typedClone();
		clone.traverseArbitrary = clone.traverseArbitrary.use(creator);
		return clone;
	}

	@Override
	public TypeArbitrary<T> useConstructors(Predicate<? super Constructor<?>> filter) {
		DefaultTypeArbitrary<T> clone = typedClone();
		clone.traverseArbitrary = clone.traverseArbitrary.useConstructors(filter);
		return clone;
	}

	@Override
	public TypeArbitrary<T> usePublicConstructors() {
		DefaultTypeArbitrary<T> clone = typedClone();
		clone.traverseArbitrary = clone.traverseArbitrary.usePublicConstructors();
		return clone;
	}

	@Override
	public TypeArbitrary<T> useAllConstructors() {
		DefaultTypeArbitrary<T> clone = typedClone();
		clone.traverseArbitrary = clone.traverseArbitrary.useAllConstructors();
		return clone;
	}

	@Override
	public TypeArbitrary<T> useFactoryMethods(Predicate<Method> filter) {
		DefaultTypeArbitrary<T> clone = typedClone();
		clone.traverseArbitrary = clone.traverseArbitrary.useFactoryMethods(filter);
		return clone;
	}

	@Override
	public TypeArbitrary<T> usePublicFactoryMethods() {
		DefaultTypeArbitrary<T> clone = typedClone();
		clone.traverseArbitrary = clone.traverseArbitrary.usePublicFactoryMethods();
		return clone;
	}

	@Override
	public TypeArbitrary<T> useAllFactoryMethods() {
		DefaultTypeArbitrary<T> clone = typedClone();
		clone.traverseArbitrary = clone.traverseArbitrary.useAllFactoryMethods();
		return clone;
	}

	@Override
	public TypeArbitrary<T> allowRecursion() {
		DefaultTypeArbitrary<T> clone = typedClone();
		clone.traverseArbitrary = clone.traverseArbitrary.allowRecursion();
		return clone;
	}

	@Override
	public String toString() {
		return String.format("TypeArbitrary<%s>", traverseArbitrary);
	}

	// TODO: Remove when there are tests for DefaultTraverseArbitrary
	public int countCreators() {
		return ((DefaultTraverseArbitrary<T>) traverseArbitrary).countCreators();
	}
}
