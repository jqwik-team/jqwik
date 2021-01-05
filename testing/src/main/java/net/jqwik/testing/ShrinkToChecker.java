package net.jqwik.testing;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;
import static org.assertj.core.api.Assertions.*;

@API(status = EXPERIMENTAL, since = "1.4.0")
public abstract class ShrinkToChecker implements Consumer<PropertyExecutionResult> {
	@Override
	public void accept(PropertyExecutionResult propertyExecutionResult) {
		Optional<List<Object>> falsifiedSample = propertyExecutionResult.falsifiedParameters();
		assertThat(falsifiedSample).isPresent();
		assertThat(falsifiedSample.get()).containsExactlyElementsOf(shrunkValues());
	}

	public abstract Iterable<?> shrunkValues();
}


