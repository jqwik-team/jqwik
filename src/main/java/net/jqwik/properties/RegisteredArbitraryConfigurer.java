package net.jqwik.properties;

import java.lang.annotation.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;

public class RegisteredArbitraryConfigurer {

	private final List<ArbitraryConfigurator> registeredConfigurators;

	public RegisteredArbitraryConfigurer(List<ArbitraryConfigurator> registeredConfigurators) {
		this.registeredConfigurators = registeredConfigurators;
	}

	public Arbitrary<?> configure(Arbitrary<?> createdArbitrary, List<Annotation> annotations) {
		List<Annotation> configurationAnnotations = configurationAnnotations(annotations);
		if (!configurationAnnotations.isEmpty()) {
			for (ArbitraryConfigurator arbitraryConfigurator : registeredConfigurators) {
				// TODO: This condition exists 3 times
				if (createdArbitrary instanceof SelfConfiguringArbitrary) {
					createdArbitrary = performSelfConfiguration(createdArbitrary, arbitraryConfigurator, annotations);
				} else {
					createdArbitrary = arbitraryConfigurator.configure(createdArbitrary, configurationAnnotations);
				}
			}
		}
		return createdArbitrary;
	}

	private <T> Arbitrary<T> performSelfConfiguration(
		Arbitrary<T> arbitrary,
		ArbitraryConfigurator configurator,
		List<Annotation> annotations
	) {
		@SuppressWarnings("unchecked")
		SelfConfiguringArbitrary<T> selfConfiguringArbitrary = (SelfConfiguringArbitrary<T>) arbitrary;
		return selfConfiguringArbitrary.configure(configurator, annotations);
	}

	private List<Annotation> configurationAnnotations(List<Annotation> annotations) {
		return annotations.stream() //
				.filter(annotation -> !annotation.annotationType().equals(ForAll.class)) //
				.collect(Collectors.toList());
	}
}
