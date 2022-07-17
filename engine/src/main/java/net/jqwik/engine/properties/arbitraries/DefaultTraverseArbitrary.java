package net.jqwik.engine.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.support.types.*;

import static org.junit.platform.commons.support.ModifierSupport.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

public class DefaultTraverseArbitrary<T> extends ArbitraryDecorator<T> implements TraverseArbitrary<T> {

	private final Class<T> targetType;
	private final Traverser traverser;
	private final Map<TypeUsage, Arbitrary<Object>> arbitrariesCache;

	private boolean enableRecursion = false;

	public DefaultTraverseArbitrary(Class<T> targetType, Traverser traverser) {
		this(targetType, traverser, new LinkedHashMap<>());
	}

	private DefaultTraverseArbitrary(Class<T> targetType, Traverser traverser, Map<TypeUsage, Arbitrary<Object>> arbitrariesCache) {
		this.targetType = targetType;
		this.traverser = traverser;
		this.arbitrariesCache = arbitrariesCache;
	}

	@Override
	protected Arbitrary<T> arbitrary() {
		TypeUsage targetTypeUsage = TypeUsage.forType(targetType);
		List<Arbitrary<? extends T>> arbitraries = streamCreators(targetTypeUsage)
			.map(this::createArbitrary)
			.collect(Collectors.toList());

		if (arbitraries.isEmpty()) {
			String message = String.format(
				"No usable generator executables (constructors or factory methods) " +
					"have been provided for type [%s].",
				targetType
			);
			throw new JqwikException(message);
		}

		return Arbitraries.oneOf(arbitraries);
	}

	private Stream<Executable> streamCreators(TypeUsage targetTypeUsage) {
		Set<Executable> creators = traverser.findCreators(targetTypeUsage);
		return creators
			.stream()
			.filter(this::constructorIsConcrete)
			.filter(this::methodIsStatic)
			.filter(this::isNotRecursive)
			.map(this::checkFittingReturnType);
	}

	private boolean constructorIsConcrete(Executable executable) {
		if (executable instanceof Constructor) {
			Constructor<?> ctor = (Constructor<?>) executable;
			return !isAbstract(targetType);
		}
		return true;
	}

	private boolean methodIsStatic(Executable executable) {
		if (executable instanceof Method) {
			Method method = (Method) executable;
			return ModifierSupport.isStatic(method);
		}
		return true;
	}

	@Override
	public TraverseArbitrary<T> enableRecursion() {
		DefaultTraverseArbitrary<T> clone = typedClone();
		clone.enableRecursion = true;
		return clone;
	}

	private Executable checkFittingReturnType(Executable creator) {
		TypeUsage returnType = TypeUsage.forType(creator.getAnnotatedReturnType().getType());
		if (!returnType.canBeAssignedTo(TypeUsage.of(targetType))) {
			throw new JqwikException(String.format("%s should return type assignable to %s", creator, targetType));
		}
		return creator;
	}

	private boolean isNotRecursive(Executable creator) {
		// TODO: Check for real recursiveness not just direct recursive calls
		return Arrays.stream(creator.getParameterTypes()).noneMatch(parameterType -> parameterType.equals(targetType));
	}

	@Override
	public String toString() {
		return String.format("TraverseArbitrary<%s>(allowRecursion=%s)", targetType.getName(), enableRecursion);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultTraverseArbitrary<?> that = (DefaultTraverseArbitrary<?>) o;
		if (enableRecursion != that.enableRecursion) return false;
		if (!targetType.equals(that.targetType)) return false;
		return LambdaSupport.areEqual(traverser, that.traverser);
	}

	@Override
	public int hashCode() {
		return HashCodeSupport.hash(targetType, enableRecursion);
	}

	private Arbitrary<T> createArbitrary(
		Executable creator
	) {
		List<Arbitrary<Object>> parameterArbitraries =
			getMethodParameters(creator, targetType)
				.stream()
				.map(methodParameter -> arbitraryFor(TypeUsageImpl.forParameter(methodParameter)))
				.collect(Collectors.toList());

		Function<List<Object>, T> combinator = paramList -> combinator(creator).apply(paramList.toArray());
		Arbitrary<T> arbitrary = Combinators.combine(parameterArbitraries).as(combinator);
		return arbitrary.ignoreException(GenerationError.class);
	}

	private Arbitrary<Object> arbitraryFor(
		TypeUsage parameterTypeUsage
	) {
		// arbitrariesCache.computeIfAbsent doesn't work here due to concurrent modification
		Arbitrary<Object> arbitrary = arbitrariesCache.get(parameterTypeUsage);
		if (arbitrary == null) {
			Optional<Arbitrary<Object>> resolvedArbitrary = traverser.resolveParameter(parameterTypeUsage);
			arbitrary = resolvedArbitrary.orElseGet(() -> Arbitraries.defaultFor(parameterTypeUsage, this::arbitraryForTypeWithoutDefault));
			arbitrariesCache.put(parameterTypeUsage, arbitrary);
		}
		return arbitrary;
	}

	@SuppressWarnings("unchecked")
	private TraverseArbitrary<Object> arbitraryForTypeWithoutDefault(TypeUsage typeUsage) {
		if (!enableRecursion) {
			throw new CannotFindArbitraryException(typeUsage);
		}
		TraverseArbitrary<Object> traverseArbitrary = new DefaultTraverseArbitrary<>((Class<Object>) typeUsage.getRawType(), traverser, arbitrariesCache);
		return traverseArbitrary.enableRecursion();
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
