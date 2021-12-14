package net.jqwik.engine.execution;

import java.io.*;
import java.util.*;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.TryExecutionResult.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.lifecycle.TryExecutionResult.Status.*;

class GenerationInfoTests {

	@Example
	void createWithJustSeed() {
		GenerationInfo generationInfo = new GenerationInfo("4242");
		assertThat(generationInfo.randomSeed()).hasValue("4242");
		assertThat(generationInfo.generationIndex()).isEqualTo(0);
		assertThat(generationInfo.shrinkingSequence()).isEmpty();
	}

	@Example
	void createWithSeedAndGenerationIndex() {
		GenerationInfo generationInfo = new GenerationInfo("4242", 41);
		assertThat(generationInfo.randomSeed()).hasValue("4242");
		assertThat(generationInfo.generationIndex()).isEqualTo(41);
	}

	@Example
	void appendShrinkingSequence() {
		GenerationInfo generationInfo = new GenerationInfo("4242", 41)
			.appendShrinkingSequence(Arrays.asList(SATISFIED, FALSIFIED));
		assertThat(generationInfo.shrinkingSequence()).containsExactly(SATISFIED, FALSIFIED);

		generationInfo = generationInfo.appendShrinkingSequence(Arrays.asList(INVALID, FALSIFIED));
		assertThat(generationInfo.shrinkingSequence()).containsExactly(SATISFIED, FALSIFIED, INVALID, FALSIFIED);
	}

	@Group
	class SampleGeneration {

		ParametersGenerator generator = new ParametersGeneratorForTests();
		TryLifecycleContext context = Mockito.mock(TryLifecycleContext.class);

		@Example
		void generateWithoutShrinkingSequence() {
			GenerationInfo generationInfo = new GenerationInfo("4242", 14);

			Optional<List<Shrinkable<Object>>> sample = generationInfo.generateOn(generator, context);
			assertThat(sample).isPresent();
			sample.ifPresent(shrinkables -> {
				Object value = shrinkables.get(0).value();
				assertThat(value).isEqualTo(14);
			});
		}

		@Example
		void generateWithShrinkingSequence() {
			GenerationInfo generationInfo = new GenerationInfo("4242", 23)
				.appendShrinkingSequence(Arrays.asList(SATISFIED, SATISFIED, FALSIFIED));

			Optional<List<Shrinkable<Object>>> sample = generationInfo.generateOn(generator, context);
			assertThat(sample).isPresent();
			sample.ifPresent(shrinkables -> {
				Object value = shrinkables.get(0).value();
				assertThat(value).isEqualTo(2);
			});
		}

		@Example
		void noGenerationWithoutGenerationIndex() {
			GenerationInfo generationInfo = new GenerationInfo("4242");

			Optional<List<Shrinkable<Object>>> sample = generationInfo.generateOn(generator, context);
			assertThat(sample).isEmpty();
		}

		@Example
		void noGenerationWhenShrinkingSequenceIsTooLong() {
			GenerationInfo generationInfo = new GenerationInfo("4242", 1)
				.appendShrinkingSequence(Arrays.asList(FALSIFIED, FALSIFIED, FALSIFIED));

			Optional<List<Shrinkable<Object>>> sample = generationInfo.generateOn(generator, context);
			assertThat(sample).isEmpty();
		}
	}

	@Group
	class Serialization {

		private ByteArrayOutputStream byteArrayOutputStream;

		@BeforeTry
		void resetByteArrayOutputStream() {
			byteArrayOutputStream = new ByteArrayOutputStream();
		}

		@Example
		void serializeWithEmptyShrinkingSequence() throws Exception {
			GenerationInfo generationInfo = new GenerationInfo("4242", 41);

			outputStream().writeObject(generationInfo);

			// System.out.println("### size: " + byteArrayOutputStream.toByteArray().length);

			GenerationInfo read = (GenerationInfo) inputStream().readObject();
			assertThat(read).isEqualTo(generationInfo);
		}

		@Property(tries = 10)
		void serializeWithShortShrinkingSequence(@ForAll("shrinkingSequence") @Size(max = 10) List<Status> sequence) throws Exception {
			GenerationInfo generationInfo = new GenerationInfo("4242", 41)
				.appendShrinkingSequence(sequence);

			outputStream().writeObject(generationInfo);

			// System.out.println("### size: " + byteArrayOutputStream.toByteArray().length);

			GenerationInfo read = (GenerationInfo) inputStream().readObject();
			assertThat(read).isEqualTo(generationInfo);
		}

		@Property(tries = 10)
		void serializeWithLongShrinkingSequence(@ForAll("shrinkingSequence") @Size(min = 100, max = 1500) List<Status> sequence) throws Exception {
			GenerationInfo generationInfo = new GenerationInfo("4242", 41)
				.appendShrinkingSequence(sequence);

			outputStream().writeObject(generationInfo);

			// System.out.printf("### size: %s (%s)%n", sequence.size(), byteArrayOutputStream.toByteArray().length);

			GenerationInfo read = (GenerationInfo) inputStream().readObject();
			assertThat(read).isEqualTo(generationInfo);
		}

		@Provide
		Arbitrary<List<Status>> shrinkingSequence() {
			return Arbitraries.of(Status.class).list();
		}

		private ObjectOutputStream outputStream() throws IOException {
			return new ObjectOutputStream(byteArrayOutputStream);
		}

		private ObjectInputStream inputStream() throws IOException {
			return new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
		}

	}

}
