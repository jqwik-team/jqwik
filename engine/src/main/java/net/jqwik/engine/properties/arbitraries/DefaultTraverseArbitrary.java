package net.jqwik.engine.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;
import net.jqwik.engine.support.types.*;

import static org.junit.platform.commons.support.ModifierSupport.*;

import static net.jqwik.engine.discovery.JqwikKotlinSupport.*;
import static net.jqwik.engine.support.JqwikReflectionSupport.*;

public class DefaultTraverseArbitrary<T> extends ArbitraryDecorator<T> implements TraverseArbitrary<T> {

	private enum TraverseMode {
		CONSTRUCTORS, FACTORIES, PUBLIC_CONSTRUCTORS, PUBLIC_FACTORIES
	}

	private final Class<T> targetType;
	private final Set<Executable> creators = new HashSet<>();
	private final Set<TraverseMode> traverseModes = new HashSet<>();
	private final Function<TypeUsage, Optional<Arbitrary<Object>>> parameterResolver;

	private boolean defaultsSet = false;
	private boolean allowRecursion = false;

	public DefaultTraverseArbitrary(Class<T> targetType, Function<TypeUsage, Optional<Arbitrary<Object>>> parameterResolver) {
		this.targetType = targetType;
		this.parameterResolver = parameterResolver;
	}

	@Override
	protected Arbitrary<T> arbitrary() {
		failWithoutCreators();

		List<Arbitrary<? extends T>> arbitraries = creators
			.stream()
			.map(this::createArbitrary)
			.collect(Collectors.toList());
		return Arbitraries.oneOf(arbitraries);
	}

	private void failWithoutCreators() {
		if (creators.isEmpty()) {
			String message = String.format(
				"No usable generator methods (constructors or factory methods) " +
					"could be found for type [%s] and type modes: %s.",
				targetType,
				traverseModes
			);
			throw new JqwikException(message);
		}
	}

	public TraverseArbitrary<T> useDefaults() {
		if (defaultsSet) {
			return this;
		}
		DefaultTraverseArbitrary<T> clone = typedClone();
		clone.creators.clear();
		clone.addPublicConstructors();
		clone.addPublicFactoryMethods();
		clone.defaultsSet = true;
		return clone;
	}

	private void addPublicFactoryMethods() {
		addTraverseMode(TraverseMode.PUBLIC_FACTORIES);
		addFactoryMethods(ModifierSupport::isPublic);
	}

	@Override
	public TraverseArbitrary<T> use(Executable creator) {
		DefaultTraverseArbitrary<T> clone = cloneWithClearedDefaults();
		clone.addCreator(creator);
		return clone;
	}

	private DefaultTraverseArbitrary<T> cloneWithClearedDefaults() {
		DefaultTraverseArbitrary<T> clone = typedClone();
		clone.clearDefaults();
		return clone;
	}

	private void addCreator(Executable creator) {
		checkCreator(creator);
		creators.add(creator);
	}

	private void clearDefaults() {
		if (defaultsSet) {
			creators.clear();
			defaultsSet = false;
		}
	}

	@Override
	public TraverseArbitrary<T> useConstructors(Predicate<? super Constructor<?>> filter) {
		DefaultTraverseArbitrary<T> clone = cloneWithClearedDefaults();
		clone.addConstructors(filter);
		return clone;
	}

	private void addConstructors(Predicate<? super Constructor<?>> filter) {
		if (isAbstract(targetType)) {
			return;
		}
		Arrays.stream(targetType.getDeclaredConstructors())
			  .filter(this::isNotRecursive)
			  .filter(constructor -> !isOverloadedConstructor(constructor))
			  .filter(filter)
			  .forEach(this::addCreator);
	}

	@Override
	public TraverseArbitrary<T> usePublicConstructors() {
		DefaultTraverseArbitrary<T> clone = cloneWithClearedDefaults();
		clone.addPublicConstructors();
		return clone;
	}

	private void addPublicConstructors() {
		addTraverseMode(TraverseMode.PUBLIC_CONSTRUCTORS);
		addConstructors(ModifierSupport::isPublic);
	}

	private void addTraverseMode(TraverseMode TraverseMode) {
		switch (TraverseMode) {
			case PUBLIC_CONSTRUCTORS:
				traverseModes.remove(DefaultTraverseArbitrary.TraverseMode.CONSTRUCTORS);
				break;
			case PUBLIC_FACTORIES:
				traverseModes.remove(DefaultTraverseArbitrary.TraverseMode.FACTORIES);
				break;
		}
		traverseModes.add(TraverseMode);
	}

	@Override
	public TraverseArbitrary<T> useAllConstructors() {
		DefaultTraverseArbitrary<T> clone = cloneWithClearedDefaults();
		clone.addTraverseMode(TraverseMode.CONSTRUCTORS);
		clone.addConstructors(ctor -> true);
		return clone;
	}

	@Override
	public TraverseArbitrary<T> useFactoryMethods(Predicate<Method> filter) {
		DefaultTraverseArbitrary<T> clone = cloneWithClearedDefaults();
		clone.addFactoryMethods(filter);
		return clone;
	}

	private void addFactoryMethods(Predicate<Method> filter) {
		Arrays.stream(targetType.getDeclaredMethods())
			  .filter(ModifierSupport::isStatic)
			  .filter(this::hasFittingReturnType)
			  .filter(this::isNotRecursive)
			  .filter(filter)
			  .forEach(this::addCreator);
	}

	@Override
	public TraverseArbitrary<T> usePublicFactoryMethods() {
		DefaultTraverseArbitrary<T> clone = cloneWithClearedDefaults();
		clone.addPublicFactoryMethods();
		return clone;
	}

	@Override
	public TraverseArbitrary<T> useAllFactoryMethods() {
		DefaultTraverseArbitrary<T> clone = cloneWithClearedDefaults();
		clone.addTraverseMode(TraverseMode.FACTORIES);
		clone.addFactoryMethods(method -> true);
		return clone;
	}

	@Override
	public TraverseArbitrary<T> allowRecursion() {
		DefaultTraverseArbitrary<T> clone = typedClone();
		clone.allowRecursion = true;
		return clone;
	}

	@SuppressWarnings("unchecked")
	private void checkCreator(Executable creator) {
		checkReturnType(creator);

		if (creator instanceof Method) {
			checkMethod((Method) creator);
		}

		if (creator instanceof Constructor) {
			checkConstructor((Constructor<T>) creator);
		}
	}

	private void checkReturnType(Executable creator) {
		if (!hasFittingReturnType(creator)) {
			throw new JqwikException(String.format("Creator %s should return type %s", creator, targetType.getName()));
		}
	}

	private boolean hasFittingReturnType(Executable creator) {
		TypeUsage returnType = TypeUsage.forType(creator.getAnnotatedReturnType().getType());
		return returnType.canBeAssignedTo(TypeUsage.of(targetType));
	}

	private boolean isNotRecursive(Executable creator) {
		return Arrays.stream(creator.getParameterTypes()).noneMatch(parameterType -> parameterType.equals(targetType));
	}

	private void checkMethod(Method method) {
		if (!JqwikReflectionSupport.isStatic(method)) {
			throw new JqwikException(String.format("Method %s should be static", method));
		}
	}

	private void checkConstructor(Constructor<T> constructor) {
		// All constructors are fine
	}

	@Override
	public String toString() {
		return String.format("TraverseArbitrary<%s>(allowRecursion=%s)", targetType.getName(), allowRecursion);
	}

	private Arbitrary<T> createArbitrary(Executable creator) {
		List<Arbitrary<Object>> parameterArbitraries =
			getMethodParameters(creator, targetType)
				.stream()
				.map(methodParameter -> arbitraryFor(TypeUsageImpl.forParameter(methodParameter)))
				.collect(Collectors.toList());

		Function<List<Object>, T> combinator = paramList -> combinator(creator).apply(paramList.toArray());
		Arbitrary<T> arbitrary = Combinators.combine(parameterArbitraries).as(combinator);
		return arbitrary.ignoreException(GenerationError.class);
	}

	private Arbitrary<Object> arbitraryFor(TypeUsage parameterTypeUsage) {
		Optional<Arbitrary<Object>> resolvedArbitrary = parameterResolver.apply(parameterTypeUsage);
		return resolvedArbitrary.orElseGet(() -> Arbitraries.defaultFor(parameterTypeUsage, this::arbitraryForTypeWithoutDefault));
	}

	@SuppressWarnings("unchecked")
	private TraverseArbitrary<Object> arbitraryForTypeWithoutDefault(TypeUsage typeUsage) {
		if (!allowRecursion) {
			throw new CannotFindArbitraryException(typeUsage);
		}
		TraverseArbitrary<Object> traverseArbitrary = new DefaultTraverseArbitrary<>((Class<Object>) typeUsage.getRawType(), parameterResolver);
		for (TraverseMode traverseMode : traverseModes) {
			switch (traverseMode) {
				case PUBLIC_CONSTRUCTORS:
					traverseArbitrary = traverseArbitrary.usePublicConstructors();
					break;
				case CONSTRUCTORS:
					traverseArbitrary = traverseArbitrary.useAllConstructors();
					break;
				case PUBLIC_FACTORIES:
					traverseArbitrary = traverseArbitrary.usePublicFactoryMethods();
					break;
				case FACTORIES:
					traverseArbitrary = traverseArbitrary.useAllFactoryMethods();
					break;
			}
		}
		return traverseArbitrary.allowRecursion();
	}

	@SuppressWarnings("unchecked")
	private Function<Object[], T> combinator(Executable creator) {
		if (creator instanceof Method) {
			return combinatorForMethod((Method) creator);
		}
		if (creator instanceof Constructor) {
			return combinatorForConstructor((Constructor<T>) creator);
		}
		throw new JqwikException(String.format("Creator %s is not supported", creator));
	}

	private Function<Object[], T> combinatorForMethod(Method method) {
		method.setAccessible(true);
		return params -> generateNext(params, p -> method.invoke(null, p));
	}

	private Function<Object[], T> combinatorForConstructor(Constructor<T> constructor) {
		constructor.setAccessible(true);
		return params -> generateNext(params, constructor::newInstance);
	}

	@SuppressWarnings("unchecked")
	private T generateNext(Object[] params, Combinator combinator) {
		try {
			return (T) combinator.combine(params);
		} catch (Throwable throwable) {
			throw new GenerationError(throwable);
		}
	}

	public int countCreators() {
		return creators.size();
	}

	@FunctionalInterface
	private interface Combinator {
		Object combine(Object[] params) throws Throwable;
	}

	private static class GenerationError extends RuntimeException {
		GenerationError(Throwable throwable) {
			super(throwable);
		}
	}

}
