package net.jqwik.engine.properties;

import java.lang.annotation.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;

public class RegisteredArbitraryConfigurer {

	private final List<ArbitraryConfigurator> registeredConfigurators = new ArrayList<>();

	public RegisteredArbitraryConfigurer(List<ArbitraryConfigurator> registeredConfigurators) {
		this.registeredConfigurators.addAll(registeredConfigurators);
		this.registeredConfigurators.addAll(DefaultArbitraries.getDefaultConfigurators());
	}

	public Arbitrary<?> configure(Arbitrary<?> createdArbitrary, TypeUsage targetType) {
		if (hasConfigurationAnnotation(targetType)) {
			for (ArbitraryConfigurator arbitraryConfigurator : registeredConfigurators) {
				if (createdArbitrary == null) {
					// Configurators are allowed to return null for filtering out arbitraries
					break;
				}
				// TODO: This condition exists 3 times
				if (createdArbitrary instanceof SelfConfiguringArbitrary) {
					createdArbitrary = performSelfConfiguration(createdArbitrary, arbitraryConfigurator, targetType);
				} else {
					createdArbitrary = arbitraryConfigurator.configure(createdArbitrary, targetType);
				}
			}
		}
		return createdArbitrary;
	}

	private boolean hasConfigurationAnnotation(TypeUsage targetType) {
		return allPotentialAnnotations(targetType)
				   .anyMatch(annotation -> !annotation.annotationType().equals(ForAll.class));
	}

	private Stream<Annotation> allPotentialAnnotations(TypeUsage targetType) {
		// Annotations in class and in one of its containers might be valid
		return targetType.getContainer()
						 .map( container -> Stream.concat(targetType.getAnnotations().stream(), allPotentialAnnotations(container)))
						 .orElse(targetType.getAnnotations().stream());
	}

	private <T> Arbitrary<T> performSelfConfiguration(
		Arbitrary<T> arbitrary,
		ArbitraryConfigurator configurator,
		TypeUsage parameter
	) {
		@SuppressWarnings("unchecked")
		SelfConfiguringArbitrary<T> selfConfiguringArbitrary = (SelfConfiguringArbitrary<T>) arbitrary;
		return selfConfiguringArbitrary.configure(configurator, parameter);
	}

}
