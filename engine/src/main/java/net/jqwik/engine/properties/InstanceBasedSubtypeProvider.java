package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;
import static net.jqwik.engine.support.OverriddenMethodAnnotationSupport.*;

abstract class InstanceBasedSubtypeProvider implements ArbitraryProvider.SubtypeProvider {

	private final Object instance;

	protected InstanceBasedSubtypeProvider(Object instance) {
		this.instance = instance;
	}

	@Override
	public Set<Arbitrary<?>> apply(TypeUsage targetType) {
		Optional<String> optionalForAllValue =
			targetType
				.findAnnotation(ForAll.class)
				.map(ForAll::value).filter(name -> !name.equals(ForAll.NO_VALUE));

		Optional<String> optionalFromValue =
			targetType
				.findAnnotation(From.class)
				.map(From::value);

		if (optionalForAllValue.isPresent() && optionalFromValue.isPresent()) {
			String message = String.format(
				"You cannot have both @ForAll(\"%s\") and @From(\"%s\") in parameter %s",
				optionalForAllValue.get(),
				optionalFromValue.get(),
				targetType
			);
			throw new JqwikException(message);
		}

		String generatorName = optionalForAllValue.orElseGet(() -> optionalFromValue.orElse(ForAll.NO_VALUE));
		final Set<Arbitrary<?>> resolvedArbitraries =
			findArbitraryGeneratorByName(targetType, generatorName)
				.map(providerMethod -> invokeProviderMethod(providerMethod, targetType))
				.orElseGet(() -> generatorName.equals(ForAll.NO_VALUE)
									 ? resolve(targetType)
									 : Collections.emptySet());

		return resolvedArbitraries
				   .stream()
				   .map(arbitrary -> configure(arbitrary, targetType))
				   .filter(Objects::nonNull)
				   .collect(Collectors.toSet());
	}

	private Set<Arbitrary<?>> invokeProviderMethod(Method providerMethod, TypeUsage targetType) {
		return new ProviderMethodInvoker(providerMethod, targetType, instance, this).invoke();
	}

	private Optional<Method> findArbitraryGeneratorByName(TypeUsage typeUsage, String generatorToFind) {
		if (generatorToFind.isEmpty())
			return Optional.empty();

		Function<Method, String> generatorNameSupplier = method -> {
			Optional<Provide> provideAnnotation = findDeclaredOrInheritedAnnotation(method, Provide.class);
			return provideAnnotation.map(Provide::value).orElse("");
		};
		TypeUsage targetArbitraryType = TypeUsage.of(Arbitrary.class, typeUsage);

		return findGeneratorMethod(generatorToFind, this.instance.getClass(), Provide.class, generatorNameSupplier, targetArbitraryType);
	}

	protected abstract Set<Arbitrary<?>> resolve(TypeUsage parameterType);

	protected abstract Arbitrary<?> configure(Arbitrary<?> arbitrary, TypeUsage targetType);

}
