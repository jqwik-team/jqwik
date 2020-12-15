package net.jqwik.engine.properties;

import java.util.*;

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
				createdArbitrary = SelfConfiguringArbitrary.configure(createdArbitrary, arbitraryConfigurator, targetType);
			}
		}
		return createdArbitrary;
	}

	private boolean hasConfigurationAnnotation(TypeUsage targetType) {
		return targetType.getAnnotations().stream()
						 .anyMatch(annotation -> !annotation.annotationType().equals(ForAll.class));
	}

}
