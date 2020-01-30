package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.support.*;

public class SampleOnlyShrinkablesGenerator implements ShrinkablesGenerator {

	private final List<MethodParameter> forAllParameters;
	private final List<Object> sample;
	private boolean generated = false;

	public SampleOnlyShrinkablesGenerator(List<MethodParameter> forAllParameters, List<Object> sample) {
		this.forAllParameters = forAllParameters;
		this.sample = sample;
	}

	@Override
	public boolean hasNext() {
		return !generated;
	}

	@Override
	public List<Shrinkable<Object>> next() {
		if (generated) {
			throw new NoSuchElementException("Sample has already been provided");
		}
		checkCompatibility(sample);
		generated = true;
		return sample.stream().map(Shrinkable::unshrinkable).collect(Collectors.toList());
	}

	private void checkCompatibility(List<Object> sample) {
		if (sample.size() != forAllParameters.size()) {
			throw new IncompatibleDataException(createIncompatibilityMessage(sample));
		}
		for (int i = 0; i < sample.size(); i++) {
			TypeUsage valueType = TypeUsage.of(sample.get(i).getClass());
			TypeUsage parameterType = TypeUsageImpl.forParameter(forAllParameters.get(i));
			if (!valueType.canBeAssignedTo(parameterType)) {
				throw new IncompatibleDataException(createIncompatibilityMessage(sample));
			}
		}
	}

	private String createIncompatibilityMessage(List<Object> sample) {
		List<TypeUsage> parameterTypes =
			this.forAllParameters
				.stream()
				.map(TypeUsageImpl::forParameter)
				.collect(Collectors.toList());

		return String.format(
			"Sample %s is not compatible with parameters %s",
			JqwikStringSupport.displayString(sample),
			JqwikStringSupport.displayString(parameterTypes)
		);
	}
}
