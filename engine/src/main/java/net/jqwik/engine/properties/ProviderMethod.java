package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.providers.ArbitraryProvider.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.support.*;
import net.jqwik.engine.support.types.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;
import static net.jqwik.engine.support.OverriddenMethodAnnotationSupport.*;

class ProviderMethod {

	@SuppressWarnings("unchecked")
	static ProviderMethod forMethod(
		Method method,
		TypeUsage targetType,
		Object instance,
		ArbitraryProvider.SubtypeProvider subtypeProvider
	) {
		Class<? extends Throwable>[] ignoreExceptions = findDeclaredOrInheritedAnnotation(method, Provide.class)
															.map(Provide::ignoreExceptions)
															.orElse(new Class[0]);
		return new ProviderMethod(method, targetType, instance, subtypeProvider, ignoreExceptions);
	}

	private final Class<? extends Throwable>[] ignoreExceptions;

	private ProviderMethod(
		Method underlyingMethod, TypeUsage targetType, Object instance, SubtypeProvider subtypeProvider,
		Class<? extends Throwable>[] ignoreExceptions
	) {
		this.method = underlyingMethod;
		this.targetType = targetType;
		this.instance = instance;
		this.subtypeProvider = subtypeProvider;
		this.ignoreExceptions = ignoreExceptions;
	}

	private final Method method;
	private final TypeUsage targetType;
	private final Object instance;
	private final SubtypeProvider subtypeProvider;

	Set<Arbitrary<?>> invoke() {
		List<MethodParameter> parameters = JqwikReflectionSupport.getMethodParameters(method, instance.getClass());
		Set<Function<List<Object>, Arbitrary<?>>> baseInvoker = Collections.singleton(this::invokeProviderMethod);
		Set<Supplier<Arbitrary<?>>> suppliers = arbitrarySuppliers(baseInvoker, parameters, Collections.emptyList());
		return mapSet(suppliers, arbitrarySupplier -> arbitrarySupplier.get().ignoreExceptions(ignoreExceptions));
	}

	private Arbitrary<?> invokeProviderMethod(List<Object> argList) {
		// TODO: Hand in all test instances instead of just target
		return (Arbitrary<?>) invokeMethodPotentiallyOuter(method, instance, argList.toArray());
	}

	private Set<Supplier<Arbitrary<?>>> arbitrarySuppliers(
		Set<Function<List<Object>, Arbitrary<?>>> invokers,
		List<MethodParameter> unresolvedParameters,
		List<Object> args
	) {
		if (unresolvedParameters.isEmpty()) {
			return mapSet(invokers, invoker -> () -> invoker.apply(args));
		}
		List<MethodParameter> newUnresolvedParameters = new ArrayList<>(unresolvedParameters);
		MethodParameter toResolve = newUnresolvedParameters.remove(0);
		if (isForAllParameter(toResolve)) {
			List<Object> newArgs = new ArrayList<>(args);
			newArgs.add(arbitraryFor(toResolve)); // Arbitrary is now in position toResolve.getIndex()
			Set<Function<List<Object>, Arbitrary<?>>> newInvokers = flatMapArbitraryInInvocations(invokers, toResolve.getIndex());
			return arbitrarySuppliers(newInvokers, newUnresolvedParameters, newArgs);
		} else {
			List<Object> newArgs = new ArrayList<>(args);
			newArgs.add(resolvePlainParameter(toResolve.getRawParameter()));
			return arbitrarySuppliers(invokers, newUnresolvedParameters, newArgs);
		}
	}

	private Set<Function<List<Object>, Arbitrary<?>>> flatMapArbitraryInInvocations(
		Set<Function<List<Object>, Arbitrary<?>>> invokers,
		int position
	) {
		Function<Function<List<Object>, Arbitrary<?>>, Function<List<Object>, Arbitrary<?>>> mapper = invoker -> arguments -> {
			Arbitrary<?> a = (Arbitrary<?>) arguments.get(position);
			return a.flatMap(argument -> {
				List<Object> resolved = new ArrayList<>(arguments);
				resolved.set(position, argument);
				return invoker.apply(resolved);
			});
		};
		return mapSet(invokers, mapper);
	}

	private Arbitrary<?> arbitraryFor(MethodParameter toResolve) {
		TypeUsage parameterType = TypeUsageImpl.forParameter(toResolve);
		Optional<Arbitrary<?>> optionalArbitrary = subtypeProvider.provideOneFor(parameterType);
		return optionalArbitrary.orElseThrow(
			() ->
				new CannotFindArbitraryException(
					parameterType,
					parameterType.findAnnotation(ForAll.class).orElse(null),
					method
				)
		);
	}

	private <T, R> Set<R> mapSet(Set<T> invokers, Function<T, R> mapper) {
		return invokers.stream().map(mapper).collect(CollectorsSupport.toLinkedHashSet());
	}

	private boolean isForAllParameter(MethodParameter parameter) {
		return parameter.isAnnotated(ForAll.class);
	}

	protected Object resolvePlainParameter(Parameter parameter) {
		if (parameter.getType().isAssignableFrom(TypeUsage.class)) {
			return targetType;
		} else {
			String message = String.format(
				"Parameter [%s] cannot be resolved in @Provide method [%s]." +
					"%nMaybe you want to add annotation `@ForAll`?",
				parameter,
				method
			);
			throw new JqwikException(message);
		}
	}
}
