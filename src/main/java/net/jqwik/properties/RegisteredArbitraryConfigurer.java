package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;

import java.lang.annotation.*;
import java.util.*;
import java.util.function.*;

public class RegisteredArbitraryConfigurer {

	private final List<ArbitraryConfigurator> registeredConfigurators;

	public RegisteredArbitraryConfigurer(List<ArbitraryConfigurator> registeredConfigurators) {
		this.registeredConfigurators = registeredConfigurators;
	}

	public Arbitrary<?> configure(Arbitrary<?> createdArbitrary, List<Annotation> configurationAnnotations) {
		if (!configurationAnnotations.isEmpty()) {
			for (ArbitraryConfigurator arbitraryConfigurator : registeredConfigurators) {
				createdArbitrary = arbitraryConfigurator.configure(createdArbitrary, configurationAnnotations);
			}
		}
		return createdArbitrary;
	}
}
