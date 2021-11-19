package net.jqwik.engine.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;

import static org.junit.platform.commons.support.ModifierSupport.*;

import static net.jqwik.engine.discovery.JqwikKotlinSupport.*;

public class DefaultTypeArbitrary<T> extends OneOfArbitrary<T> implements TypeArbitrary<T> {

	private final Class<T> targetType;
	private final Set<Executable> creators = new HashSet<>();
	private boolean defaultsSet = false;
	private boolean allowRecursion = false;

	public DefaultTypeArbitrary(Class<T> targetType) {
		super(Collections.emptyList());
		this.targetType = targetType;
	}

	public TypeArbitrary<T> useDefaults() {
		usePublicConstructors();
		useAllFactoryMethods();
		defaultsSet = true;
		return this;
	}

	@Override
	public TypeArbitrary<T> use(Executable creator) {
		if (defaultsSet) {
			creators.clear();
			arbitraries().clear();
			defaultsSet = false;
		}
		if (creators.contains(creator)) {
			return this;
		}
		checkCreator(creator);
		addArbitrary(createArbitrary(creator));
		creators.add(creator);
		return this;
	}

	@Override
	public TypeArbitrary<T> useConstructors(Predicate<? super Constructor<?>> filter) {
		if (isAbstract(targetType)) {
			return this;
		}
		Arrays.stream(targetType.getDeclaredConstructors())
			  .filter(this::isNotRecursive)
			  .filter(constructor -> !isOverloadedConstructor(constructor))
			  .filter(filter)
			  .forEach(this::use);
		return this;
	}

	@Override
	public TypeArbitrary<T> usePublicConstructors() {
		return useConstructors(ModifierSupport::isPublic);
	}

	@Override
	public TypeArbitrary<T> useAllConstructors() {
		return useConstructors(ctor -> true);
	}

	@Override
	public TypeArbitrary<T> useFactoryMethods(Predicate<Method> filter) {
		Arrays.stream(targetType.getDeclaredMethods())
			  .filter(ModifierSupport::isStatic)
			  .filter(this::hasFittingReturnType)
			  .filter(this::isNotRecursive)
			  .filter(filter)
			  .forEach(this::use);
		return this;
	}

	@Override
	public TypeArbitrary<T> usePublicFactoryMethods() {
		return useFactoryMethods(ModifierSupport::isPublic);
	}

	@Override
	public TypeArbitrary<T> useAllFactoryMethods() {
		return useFactoryMethods(method -> true);
	}

	@Override
	public TypeArbitrary<T> allowRecursion() {
		return this;
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
		if (!isStatic(method)) {
			throw new JqwikException(String.format("Method %s should be static", method));
		}
	}

	private void checkConstructor(Constructor<T> constructor) {
		// All constructors are fine
	}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		if (arbitraries().isEmpty()) {
			String message = String.format("%s has no arbitraries to choose from.", this);
			throw new JqwikException(message);
		}
		return super.generator(genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		// Exhaustive generation cannot work because Arbitraries.defaultFor()
		// is evaluated lazily which prevents ad ante calculation of combinations
		return Optional.empty();
	}

	@Override
	public String toString() {
		return String.format("TypeArbitrary<%s>", targetType.getName());
	}

	private Arbitrary<T> createArbitrary(Executable creator) {
		List<Arbitrary<Object>> parameterArbitraries =
			Arrays.stream(creator.getAnnotatedParameterTypes())
				  .map(annotatedType -> Arbitraries.defaultFor(TypeUsage.forType(annotatedType.getType())))
				  .collect(Collectors.toList());

		Function<List<Object>, T> combinator = paramList -> combinator(creator).apply(paramList.toArray());
		Arbitrary<T> arbitrary = Combinators.combine(parameterArbitraries).as(combinator);
		return arbitrary.ignoreException(GenerationError.class);
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
