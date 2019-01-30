package net.jqwik.engine.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;

public class DefaultTypeArbitrary<T> extends OneOfArbitrary<T> implements TypeArbitrary<T> {

	private final Class<T> targetType;
	private final Set<Executable> creators = new HashSet<>();
	private boolean defaultsSet = false;

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
		Arrays.stream(targetType.getDeclaredConstructors())
			  .filter(this::isNotRecursive)
			  .filter(filter)
			  .forEach(this::use);
		return this;
	}

	@Override
	public TypeArbitrary<T> usePublicConstructors() {
		return useConstructors(JqwikReflectionSupport::isPublic);
	}

	@Override
	public TypeArbitrary<T> useAllConstructors() {
		return useConstructors(ctor -> true);
	}

	@Override
	public TypeArbitrary<T> useFactoryMethods(Predicate<Method> filter) {
		Arrays.stream(targetType.getDeclaredMethods())
			  .filter(JqwikReflectionSupport::isStatic)
			  .filter(this::hasFittingReturnType)
			  .filter(this::isNotRecursive)
			  .filter(filter)
			  .forEach(this::use);
		return this;
	}

	@Override
	public TypeArbitrary<T> usePublicFactoryMethods() {
		return useFactoryMethods(JqwikReflectionSupport::isPublic);
	}

	@Override
	public TypeArbitrary<T> useAllFactoryMethods() {
		return useFactoryMethods(method -> true);
	}

	private void checkCreator(Executable creator) {
		checkReturnType(creator);

		if (creator instanceof Method) {
			checkMethod((Method) creator);
		}

		if (creator instanceof Constructor) {
			checkConstructor((Constructor) creator);
		}
	}

	private void checkReturnType(Executable creator) {
		if (!hasFittingReturnType(creator)) {
			throw new JqwikException(String.format("Creator %s should return type %s", creator, targetType.getName()));
		}
	}

	private boolean hasFittingReturnType(Executable creator) {
		TypeUsage returnType = TypeUsage.forType(creator.getAnnotatedReturnType().getType());
		return returnType.isAssignableFrom(targetType);
	}

	private boolean isNotRecursive(Executable creator) {
		return Arrays.stream(creator.getParameterTypes()).noneMatch(parameterType -> parameterType.equals(targetType));
	}

	private void checkMethod(Method method) {
		if (!JqwikReflectionSupport.isStatic(method)) {
			throw new JqwikException(String.format("Method %s should be static", method));
		}
	}

	private void checkConstructor(Constructor method) {
	}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		if (arbitraries().isEmpty()) {
			String message = String.format("TypeArbitrary<%s> has no arbitraries to choose from.", targetType.getName());
			throw new JqwikException(message);
		}
		return super.generator(genSize);
	}

	private Arbitrary<T> createArbitrary(Executable creator) {
		List<Arbitrary<Object>> parameterArbitraries =
			Arrays.stream(creator.getAnnotatedParameterTypes())
				  .map(annotatedType -> Arbitraries.defaultFor(TypeUsage.forType(annotatedType.getType())))
				  .collect(Collectors.toList());

		Function<List<Object>, T> combinator = paramList -> combinator(creator).apply(paramList.toArray());
		return Combinators.combine(parameterArbitraries).as(combinator);
	}

	private Function<Object[], T> combinator(Executable creator) {
		if (creator instanceof Method) {
			return combinatorForMethod((Method) creator);
		}
		if (creator instanceof Constructor) {
			return combinatorForConstructor((Constructor) creator);
		}
		throw new JqwikException(String.format("Creator %s is not supported", creator));
	}

	private Function<Object[], T> combinatorForMethod(Method method) {
		method.setAccessible(true);
		return params -> generateNext(params, p -> method.invoke(null, p));
	}

	private Function<Object[], T> combinatorForConstructor(Constructor constructor) {
		constructor.setAccessible(true);
		return params -> generateNext(params, constructor::newInstance);
	}

	private T generateNext(Object[] params, Combinator combinator) {
		long count = 0;
		while (count++ <= 1000) {
			try {
				//noinspection unchecked
				return (T) combinator.combine(params);
			} catch (Throwable ignored) {
			}
		}
		String message = String.format("TypeArbitrary<%s>: Trying to generate object failed too often", targetType.getName());
		throw new JqwikException(message);
	}

	public int countCreators() {
		return creators.size();
	}

	@FunctionalInterface
	private interface Combinator {
		Object combine(Object[] params) throws Throwable;
	}

}
