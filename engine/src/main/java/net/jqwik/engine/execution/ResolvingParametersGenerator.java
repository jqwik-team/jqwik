package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;

public class ResolvingParametersGenerator implements Iterator<List<Shrinkable<Object>>> {
	private final List<MethodParameter> propertyParameters;
	private final ForAllParametersGenerator shrinkablesGenerator;

	public ResolvingParametersGenerator(List<MethodParameter> propertyParameters, ForAllParametersGenerator shrinkablesGenerator) {
		this.propertyParameters = propertyParameters;
		this.shrinkablesGenerator = shrinkablesGenerator;
	}

	public boolean hasNext() {
		return shrinkablesGenerator.hasNext();
	}

	public List<Shrinkable<Object>> next() {
		List<Shrinkable<Object>> next = new ArrayList<>();
		List<Shrinkable<Object>> forAllShrinkables = shrinkablesGenerator.next();

		for (MethodParameter parameter : propertyParameters) {
			if (parameter.isAnnotated(ForAll.class)) {
				next.add(forAllShrinkables.get(0));
				forAllShrinkables.remove(0);
			} else {
				// TODO: Resolve parameter using some kind of parameter resolver
				throw new JqwikException(String.format("Parameter [%s] without @ForAll cannot be resolved", parameter));
			}
		}

		return next;
	}
}
