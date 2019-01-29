package net.jqwik.engine.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;

public class DefaultTypeArbitrary<T> extends OneOfArbitrary<T> implements TypeArbitrary<T> {

	private TypeUsage targetType;
	private final List<Executable> creators = new ArrayList<>();

	public DefaultTypeArbitrary(Class<T> targetClass) {
		this(TypeUsage.of(targetClass));
	}

	public DefaultTypeArbitrary(TypeUsage targetType) {
		super(Collections.emptyList());
		this.targetType = targetType;
	}

	@Override
	public TypeArbitrary<T> use(Executable creator) {
		checkCreator(creator);
		addArbitrary(createArbitrary(creator));
		return this;
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
		TypeUsage returnType = TypeUsage.forType(creator.getAnnotatedReturnType().getType());
		if (!returnType.canBeAssignedTo(targetType)) {
			throw new JqwikException(String.format("Creator %s should return type %s", creator, targetType));
		}
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
			String message = String.format("TypeArbitrary<%s> has no arbitraries to choose from.", targetType);
			throw new JqwikException(message);
		}
		return super.generator(genSize);
	}

	private Arbitrary<T> createArbitrary(Executable creator) {
		return Arbitraries.fromGenerator(generatorForCreator(creator));
	}

	private RandomGenerator<T> generatorForCreator(Executable creator) {
		if (creator instanceof Method) {
			return generatorForMethod((Method) creator);
		}
		if (creator instanceof Constructor) {
			return generatorForConstructor((Constructor) creator);
		}
		throw new JqwikException(String.format("Creator %s is not supported", creator));
	}

	private RandomGenerator<T> generatorForMethod(Method method) {
		method.setAccessible(true);
		return random -> {
			try {
				T value = (T) method.invoke(null);
				return Shrinkable.unshrinkable(value);
			} catch (Exception e) {
				// TODO: Ignore this instance
				throw new RuntimeException(e);
			}
		};
	}

	private RandomGenerator<T> generatorForConstructor(Constructor constructor) {
		constructor.setAccessible(true);
		return random -> {
			try {
				T value = (T) constructor.newInstance();
				return Shrinkable.unshrinkable(value);
			} catch (Exception e) {
				// TODO: Ignore this instance
				throw new RuntimeException(e);
			}
		};
	}
}
