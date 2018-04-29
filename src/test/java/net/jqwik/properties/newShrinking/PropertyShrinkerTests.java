package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;
import net.jqwik.properties.newShrinking.ShrinkableTypesForTest.*;
import org.junit.platform.engine.reporting.*;
import org.mockito.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;

class PropertyShrinkerTests {

	@SuppressWarnings("unchecked")
	private Consumer<ReportEntry> reporter = Mockito.mock(Consumer.class);

	@Example
	void ifThereIsNothingToShrinkReturnOriginalValue() {
		List<NShrinkable> unshrinkableParameters = asList(NShrinkable.unshrinkable(1), NShrinkable.unshrinkable("hello"));
		PropertyShrinker shrinker = new PropertyShrinker(unshrinkableParameters, ShrinkingMode.FULL, reporter, new Reporting[0]);

		Throwable originalError = new RuntimeException("original error");
		ShrinkingResult result = shrinker.shrink(ignore -> false, originalError);

		assertThat(result.parameters()).isSameAs(unshrinkableParameters);
		assertThat(result.steps()).isEqualTo(0);
		assertThat(result.throwable()).isPresent();
		assertThat(result.throwable().get()).isSameAs(originalError);

		Mockito.verifyZeroInteractions(reporter);
	}

	@Example
	void ifShrinkingIsOffReturnOriginalValue() {
		List<NShrinkable> parameters = asList(
			new OneStepShrinkable(5),
			new OneStepShrinkable(10)
		);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.OFF, reporter, new Reporting[0]);

		Throwable originalError = new RuntimeException("original error");
		ShrinkingResult result = shrinker.shrink(ignore -> false, originalError);

		assertThat(result.parameters()).isSameAs(parameters);
		assertThat(result.steps()).isEqualTo(0);
		assertThat(result.throwable()).isPresent();
		assertThat(result.throwable().get()).isSameAs(originalError);

		Mockito.verifyZeroInteractions(reporter);
	}

	//@Example
	void shrinkAllParameters() {
		List<NShrinkable> parameters = asList(
			new OneStepShrinkable(5),
			new OneStepShrinkable(10)
		);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.FULL, reporter, new Reporting[0]);

		Falsifier<List> listFalsifier = params -> {
			if (((int) params.get(0)) > 0) return false;
			return ((int) params.get(1)) <= 1;
		};
		ShrinkingResult result = shrinker.shrink(listFalsifier, null);

		assertThat(toValues(result)).isEqualTo(asList(1, 2));
		assertThat(result.steps()).isEqualTo(12);
	}

	private List toValues(ShrinkingResult result) {
		return result.parameters().stream().map(NShrinkable::value).collect(Collectors.toList());
	}
}
