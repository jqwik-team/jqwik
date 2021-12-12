package net.jqwik.engine.execution;

import java.util.*;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

@SuppressLogging
class AfterFailureParametersGeneratorTests {

	ParametersGenerator generator = new ParametersGenerator() {
		int index = 0;
		@Override
		public boolean hasNext() {
			return index < 25;
		}
		@Override
		public List<Shrinkable<Object>> next(TryLifecycleContext context) {
			return Arrays.asList(Shrinkable.unshrinkable(++index));
		}
		@Override
		public int edgeCasesTotal() {
			return 0;
		}
		@Override
		public int edgeCasesTried() {
			return 0;
		}
		@Override
		public GenerationInfo generationInfo(String randomSeed) {
			return new GenerationInfo(randomSeed, index);
		}
		@Override
		public void reset() {
			index = 0;
		}
	};

	TryLifecycleContext context = Mockito.mock(TryLifecycleContext.class);

	@Example
	void afterFailureMode_PreviousSeed_plainGeneratorIsUsed() {
		ParametersGenerator afterFailureGenerator = new AfterFailureParametersGenerator(
			AfterFailureMode.PREVIOUS_SEED,
			new GenerationInfo("42", 715),
			generator
		);

		assertThat(afterFailureGenerator.hasNext()).isTrue();
		assertThat(afterFailureGenerator.next(context).get(0).value()).isEqualTo(1);
		assertThat(afterFailureGenerator.next(context).get(0).value()).isEqualTo(2);

		assertThat(afterFailureGenerator.generationInfo("42"))
			.isEqualTo(new GenerationInfo("42", 2));
	}

	@Example
	void afterFailureMode_SampleFirst_generationIndex0_plainGeneratorIsUsed() {
		ParametersGenerator afterFailureGenerator = new AfterFailureParametersGenerator(
			AfterFailureMode.SAMPLE_FIRST,
			new GenerationInfo("42", 0),
			generator
		);

		assertThat(afterFailureGenerator.hasNext()).isTrue();
		assertThat(afterFailureGenerator.next(context).get(0).value()).isEqualTo(1);
		assertThat(afterFailureGenerator.next(context).get(0).value()).isEqualTo(2);

		assertThat(afterFailureGenerator.generationInfo("42"))
			.isEqualTo(new GenerationInfo("42", 2));
	}

	@Example
	void afterFailureMode_SampleFirst_generateSampleFirst_thenUsePlainGenerator() {
		ParametersGenerator afterFailureGenerator = new AfterFailureParametersGenerator(
			AfterFailureMode.SAMPLE_FIRST,
			new GenerationInfo("42", 13),
			generator
		);

		assertThat(afterFailureGenerator.hasNext()).isTrue();
		assertThat(afterFailureGenerator.next(context).get(0).value()).isEqualTo(13);
		assertThat(afterFailureGenerator.generationInfo("42"))
			.isEqualTo(new GenerationInfo("42", 13));

		assertThat(afterFailureGenerator.hasNext()).isTrue();
		assertThat(afterFailureGenerator.next(context).get(0).value()).isEqualTo(1);
		assertThat(afterFailureGenerator.generationInfo("42"))
			.isEqualTo(new GenerationInfo("42", 1));
	}

	@Example
	void afterFailureMode_SampleOnly_generateSampleOnly() {
		ParametersGenerator afterFailureGenerator = new AfterFailureParametersGenerator(
			AfterFailureMode.SAMPLE_ONLY,
			new GenerationInfo("42", 13),
			generator
		);

		assertThat(afterFailureGenerator.hasNext()).isTrue();
		assertThat(afterFailureGenerator.next(context).get(0).value()).isEqualTo(13);
		assertThat(afterFailureGenerator.generationInfo("42"))
			.isEqualTo(new GenerationInfo("42", 13));

		assertThat(afterFailureGenerator.hasNext()).isFalse();
	}

	@Example
	void afterFailureMode_SampleCannotBeGenerated_thenUsePlainGenerator() {
		ParametersGenerator afterFailureGenerator = new AfterFailureParametersGenerator(
			AfterFailureMode.SAMPLE_ONLY,
			new GenerationInfo("42", 33),
			generator
		);

		assertThat(afterFailureGenerator.hasNext()).isTrue();
		assertThat(afterFailureGenerator.next(context).get(0).value()).isEqualTo(1);
		assertThat(afterFailureGenerator.generationInfo("42"))
			.isEqualTo(new GenerationInfo("42", 1));

		assertThat(afterFailureGenerator.hasNext()).isTrue();
	}

}