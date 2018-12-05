package net.jqwik.api.configurators;

import net.jqwik.api.*;

import java.lang.reflect.*;

public class ArbitraryConfigurationException extends JqwikException {
	ArbitraryConfigurationException(Method configurationMethod) {
		super(String.format("Configuration method <%s> must return object of type Arbitrary or null", configurationMethod.toString()));
	}
}
