package net.jqwik.engine.properties.arbitraries.randomized;

import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.support.*;

abstract class AbstractFunctionGenerator<F, R> implements RandomGenerator<F> {
	final Class<F> functionalType;
	final RandomGenerator<R> resultGenerator;
	final List<Tuple2<Predicate<? super List<?>>, Function<? super List<?>, ? extends R>>> conditions;

	AbstractFunctionGenerator(
		Class<F> functionalType,
		RandomGenerator<R> resultGenerator,
		List<Tuple2<Predicate<? super List<?>>, Function<? super List<?>, ? extends R>>> conditions
	) {
		this.functionalType = functionalType;
		this.resultGenerator = resultGenerator;
		this.conditions = conditions;
	}

	@SuppressWarnings("unchecked")
	F createFunctionProxy(InvocationHandler handler) {
		return (F) Proxy.newProxyInstance(functionalType.getClassLoader(), new Class[]{functionalType}, handler);
	}


	public Shrinkable<F> createConstantFunction(Shrinkable<R> shrinkableConstant) {
		return shrinkableConstant.map(this::constantFunction);
	}

	private F constantFunction(R constant) {
		InvocationHandler handler = (proxy, method, args) -> {
			if (JqwikReflectionSupport.isEqualsMethod(method)) {
				return handleEqualsMethod(proxy, args);
			}
			if (JqwikReflectionSupport.isToStringMethod(method)) {
				return handleToStringOfConstantMethod(constant);
			}
			if (JqwikReflectionSupport.isHashCodeMethod(method)) {
				return HashCodeSupport.hash(constant);
			}
			if (method.isDefault()) {
				return handleDefaultMethod(proxy, method, args);
			}

			return conditionalResult(args).orElse(new Object[]{constant})[0];
		};
		return createFunctionProxy(handler);
	}

	protected Object handleEqualsMethod(final Object proxy, Object[] args) {
		return proxy == args[0];
	}

	private Object handleToStringOfConstantMethod(final R constant) {
		return String.format(
			"Constant Function<%s>(%s)",
			functionalType.getSimpleName(),
			JqwikStringSupport.displayString(constant)
		);
	}

	// Returns result wrapped in array to allow null as result
	protected Optional<Object[]> conditionalResult(Object[] args) {
		Optional<Object[]> conditionalResult = Optional.empty();
		for (Tuple2<Predicate<? super List<?>>, Function<? super List<?>, ? extends R>> condition : conditions) {
			List<Object> params = Arrays.asList(args);
			if (condition.get1().test(params)) {
				Object[] result = new Object[]{condition.get2().apply(params)};
				conditionalResult = Optional.of(result);
				break;
			}
		}
		return conditionalResult;
	}

	protected Object handleDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
		MethodHandle handle = handleForDefaultMethod(method);
		return handle.bindTo(proxy).invokeWithArguments(args);
	}

	protected MethodHandle handleForDefaultMethod(Method method) throws Throwable {
		return new DefaultMethodHandleFactory().create(method);
	}

}
