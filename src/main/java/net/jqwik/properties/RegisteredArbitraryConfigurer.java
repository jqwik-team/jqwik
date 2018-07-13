package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;

import java.lang.annotation.*;
import java.util.*;
import java.util.stream.*;

public class RegisteredArbitraryConfigurer {

	private final List<ArbitraryConfigurator> registeredConfigurators;

	public RegisteredArbitraryConfigurer(List<ArbitraryConfigurator> registeredConfigurators) {
		this.registeredConfigurators = registeredConfigurators;
	}

	public Arbitrary<?> configure(Arbitrary<?> createdArbitrary, List<Annotation> annotations) {
		List<Annotation> configurationAnnotations = configurationAnnotations(annotations);
		if (!configurationAnnotations.isEmpty()) {
			for (ArbitraryConfigurator arbitraryConfigurator : registeredConfigurators) {
				createdArbitrary = arbitraryConfigurator.configure(createdArbitrary, configurationAnnotations);
			}
		}
		return createdArbitrary;
	}

	private List<Annotation> configurationAnnotations(List<Annotation> annotations) {
		return annotations.stream() //
				.filter(annotation -> !annotation.annotationType().equals(ForAll.class)) //
				.collect(Collectors.toList());
	}
}
