package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

public class InjectingParametersGenerator implements Iterator<List<Shrinkable<Object>>> {
	private final List<MethodParameter> propertyParameters;
	private final Iterator<List<Shrinkable<Object>>> forAllParametersGenerator;
	private final InjectParameterHook injectParameterHook;

	public InjectingParametersGenerator(
		List<MethodParameter> propertyParameters,
		Iterator<List<Shrinkable<Object>>> forAllParametersGenerator,
		InjectParameterHook injectParameterHook
	) {
		this.propertyParameters = propertyParameters;
		this.forAllParametersGenerator = forAllParametersGenerator;
		this.injectParameterHook = injectParameterHook;
	}

	public boolean hasNext() {
		return forAllParametersGenerator.hasNext();
	}

	public List<Shrinkable<Object>> next() {
		List<Shrinkable<Object>> next = new ArrayList<>();
		List<Shrinkable<Object>> forAllShrinkables = new ArrayList<>(forAllParametersGenerator.next());

		for (MethodParameter parameter : propertyParameters) {
			if (parameter.isAnnotated(ForAll.class)) {
				next.add(forAllShrinkables.get(0));
				forAllShrinkables.remove(0);
			} else {
				next.add(findInjectableParameter(parameter));
			}
		}

		return next;
	}

	private Shrinkable<Object> findInjectableParameter(MethodParameter parameter) {
		// TODO: Resolve parameter using some kind of parameter resolver
		throw new JqwikException(String.format("Parameter [%s] without @ForAll cannot be resolved", parameter));
	}
}
