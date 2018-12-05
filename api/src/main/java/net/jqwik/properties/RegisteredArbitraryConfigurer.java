package net.jqwik.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;

public class RegisteredArbitraryConfigurer {

	private final List<ArbitraryConfigurator> registeredConfigurators;

	public RegisteredArbitraryConfigurer(List<ArbitraryConfigurator> registeredConfigurators) {
		this.registeredConfigurators = registeredConfigurators;
	}

	public Arbitrary<?> configure(Arbitrary<?> createdArbitrary, TypeUsage targetType) {
		if (hasConfigurationAnnotation(targetType)) {
			for (ArbitraryConfigurator arbitraryConfigurator : registeredConfigurators) {
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
		return targetType.getAnnotations().stream()
						 .anyMatch(annotation -> !annotation.annotationType().equals(ForAll.class));
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
