package net.jqwik.engine.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

import static org.junit.platform.commons.support.ModifierSupport.*;

import static net.jqwik.engine.discovery.JqwikKotlinSupport.*;

public class DefaultTypeArbitrary<T> extends ArbitraryDecorator<T> implements TypeArbitrary<T> {

	private final Class<T> targetType;
	private final Set<Executable> creators = new HashSet<>();
	private final Set<UseTypeMode> useTypeModes = new HashSet<>();
	private boolean defaultsSet = false;
	private boolean allowRecursion = false;

	public DefaultTypeArbitrary(Class<T> targetType) {
		this.targetType = targetType;
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
				useTypeModes
			);
			throw new JqwikException(message);
		}
	}

	public TypeArbitrary<T> useDefaults() {
		if (defaultsSet) {
			return this;
		}
		DefaultTypeArbitrary<T> clone = typedClone();
		clone.creators.clear();
		clone.addPublicConstructors();
		clone.addPublicFactoryMethods();
		clone.defaultsSet = true;
		return clone;
	}

	private void addPublicFactoryMethods() {
		addUseTypeMode(UseTypeMode.PUBLIC_FACTORIES);
		addFactoryMethods(ModifierSupport::isPublic);
	}

	@Override
	public TypeArbitrary<T> use(Executable creator) {
		DefaultTypeArbitrary<T> clone = cloneWithClearedDefaults();
		clone.addCreator(creator);
		return clone;
	}

	private DefaultTypeArbitrary<T> cloneWithClearedDefaults() {
		DefaultTypeArbitrary<T> clone = typedClone();
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
	public TypeArbitrary<T> useConstructors(Predicate<? super Constructor<?>> filter) {
		DefaultTypeArbitrary<T> clone = cloneWithClearedDefaults();
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
	public TypeArbitrary<T> usePublicConstructors() {
		DefaultTypeArbitrary<T> clone = cloneWithClearedDefaults();
		clone.addPublicConstructors();
		return clone;
	}

	private void addPublicConstructors() {
		addUseTypeMode(UseTypeMode.PUBLIC_CONSTRUCTORS);
		addConstructors(ModifierSupport::isPublic);
	}

	private void addUseTypeMode(UseTypeMode useTypeMode) {
		switch (useTypeMode) {
			case PUBLIC_CONSTRUCTORS:
				useTypeModes.remove(UseTypeMode.CONSTRUCTORS);
				break;
			case PUBLIC_FACTORIES:
				useTypeModes.remove(UseTypeMode.FACTORIES);
				break;
		}
		useTypeModes.add(useTypeMode);
	}

	@Override
	public TypeArbitrary<T> useAllConstructors() {
		DefaultTypeArbitrary<T> clone = cloneWithClearedDefaults();
		clone.addUseTypeMode(UseTypeMode.CONSTRUCTORS);
		clone.addConstructors(ctor -> true);
		return clone;
	}

	@Override
	public TypeArbitrary<T> useFactoryMethods(Predicate<Method> filter) {
		DefaultTypeArbitrary<T> clone = cloneWithClearedDefaults();
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
	public TypeArbitrary<T> usePublicFactoryMethods() {
		DefaultTypeArbitrary<T> clone = cloneWithClearedDefaults();
		clone.addPublicFactoryMethods();
		return clone;
	}

	@Override
	public TypeArbitrary<T> useAllFactoryMethods() {
		DefaultTypeArbitrary<T> clone = cloneWithClearedDefaults();
		clone.addUseTypeMode(UseTypeMode.FACTORIES);
		clone.addFactoryMethods(method -> true);
		return clone;
	}

	@Override
	public TypeArbitrary<T> allowRecursion() {
		DefaultTypeArbitrary<T> clone = typedClone();
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
		if (!isStatic(method)) {
			throw new JqwikException(String.format("Method %s should be static", method));
		}
	}

	private void checkConstructor(Constructor<T> constructor) {
		// All constructors are fine
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		// Exhaustive generation cannot work because Arbitraries.defaultFor()
		// is evaluated lazily which prevents ad ante calculation of combinations
		return Optional.empty();
	}

	@Override
	public String toString() {
		return String.format("TypeArbitrary<%s>(allowRecursion=%s)", targetType.getName(), allowRecursion);
	}

	private Arbitrary<T> createArbitrary(Executable creator) {
		List<Arbitrary<Object>> parameterArbitraries =
			Arrays.stream(creator.getAnnotatedParameterTypes())
				  .map(annotatedType -> arbitraryFor(TypeUsage.forType(annotatedType.getType())))
				  .collect(Collectors.toList());

		Function<List<Object>, T> combinator = paramList -> combinator(creator).apply(paramList.toArray());
		Arbitrary<T> arbitrary = Combinators.combine(parameterArbitraries).as(combinator);
		return arbitrary.ignoreException(GenerationError.class);
	}

	private Arbitrary<Object> arbitraryFor(TypeUsage parameterTypeUsage) {
		// TODO: Get rid of lazy arbitrary
		return Arbitraries.lazy(
			() -> Arbitraries.defaultFor(parameterTypeUsage, this::arbitraryForTypeWithoutDefault)
		);
	}

	@SuppressWarnings("unchecked")
	private TypeArbitrary<Object> arbitraryForTypeWithoutDefault(TypeUsage typeUsage) {
		if (!allowRecursion) {
			throw new CannotFindArbitraryException(typeUsage);
		}
		TypeArbitrary<Object> typeArbitrary = new DefaultTypeArbitrary<>((Class<Object>) typeUsage.getRawType());
		for (UseTypeMode useTypeMode : useTypeModes) {
			typeArbitrary = useTypeMode.modify(typeArbitrary);
		}
		return typeArbitrary.allowRecursion();
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
