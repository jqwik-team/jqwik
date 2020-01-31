package net.jqwik.engine;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

public abstract class ShrinkToChecker implements Consumer<PropertyExecutionResult> {
	@Override
	public void accept(PropertyExecutionResult propertyExecutionResult) {
		Optional<List<Object>> falsifiedSample = propertyExecutionResult.getFalsifiedSample();
		assertThat(falsifiedSample).isPresent();
		assertThat(falsifiedSample.get()).containsExactlyElementsOf(shrunkValues());

	}

	public abstract Iterable<?> shrunkValues();
}


