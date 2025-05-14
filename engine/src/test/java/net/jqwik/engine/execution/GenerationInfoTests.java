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
		assertThat(generationInfo.shrinkingSequences()).isEmpty();
	}

	@Example
	void createWithSeedAndGenerationIndex() {
		GenerationInfo generationInfo = new GenerationInfo("4242", 41);
		assertThat(generationInfo.randomSeed()).hasValue("4242");
		assertThat(generationInfo.generationIndex()).isEqualTo(41);
	}

	@Example
	void appendShrinkingSequences() {
		GenerationInfo generationInfo = new GenerationInfo("4242", 41)
			.appendShrinkingSequence(Arrays.asList(SATISFIED, FALSIFIED));
		assertThat(generationInfo.shrinkingSequences()).containsExactly(
			Arrays.asList(SATISFIED, FALSIFIED)
		);

		generationInfo = generationInfo.appendShrinkingSequence(Arrays.asList(INVALID, FALSIFIED));
		assertThat(generationInfo.shrinkingSequences()).containsExactly(
			Arrays.asList(SATISFIED, FALSIFIED),
			Arrays.asList(INVALID, FALSIFIED)
		);

		generationInfo = generationInfo.appendShrinkingSequence(Arrays.asList());
		assertThat(generationInfo.shrinkingSequences()).containsExactly(
			Arrays.asList(SATISFIED, FALSIFIED),
			Arrays.asList(INVALID, FALSIFIED)
		);
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
			// Shrink 23 to 2
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
		void generateWithShrinkingSequenceWithInvalid() {
			// Shrink 45 to 5
			GenerationInfo generationInfo = new GenerationInfo("4242", 45)
				.appendShrinkingSequence(Arrays.asList(SATISFIED, SATISFIED, INVALID, SATISFIED, FALSIFIED));

			Optional<List<Shrinkable<Object>>> sample = generationInfo.generateOn(generator, context);
			assertThat(sample).isPresent();
			sample.ifPresent(shrinkables -> {
				Object value = shrinkables.get(0).value();
				assertThat(value).isEqualTo(5);
			});
		}

		@Example
		void generateWithMixedShrinkingSequence() {
			// Shrink 100 to 34, then to 29
			GenerationInfo generationInfo = new GenerationInfo("4242", 100)
				.appendShrinkingSequence(Arrays.asList(
					SATISFIED, SATISFIED, SATISFIED, SATISFIED, SATISFIED, SATISFIED, SATISFIED, SATISFIED, SATISFIED, FALSIFIED,
					SATISFIED, FALSIFIED
				));

			Optional<List<Shrinkable<Object>>> sample = generationInfo.generateOn(generator, context);
			assertThat(sample).isPresent();
			sample.ifPresent(shrinkables -> {
				Object value = shrinkables.get(0).value();
				assertThat(value).isEqualTo(29);
			});
		}

		@Example
		void generateWithSeveralShrinkingSequences() {
			// Shrink 199 to 13, then shrink 13 to 2.
			GenerationInfo generationInfo = new GenerationInfo("4242", 199)
				.appendShrinkingSequence(Arrays.asList(SATISFIED, SATISFIED, SATISFIED, SATISFIED, SATISFIED, SATISFIED, FALSIFIED))
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

			try (ObjectOutputStream os = outputStream();) {
				os.writeObject(generationInfo);
			}

			// System.out.println("### size: " + byteArrayOutputStream.toByteArray().length);

			GenerationInfo read = (GenerationInfo) inputStream().readObject();
			assertThat(read).isEqualTo(generationInfo);
		}

		@Property(tries = 10)
		void serializeWithShortShrinkingSequence(@ForAll("shrinkingSequence") @Size(max = 10) List<Status> sequence) throws Exception {
			GenerationInfo generationInfo = new GenerationInfo("4242", 41)
				.appendShrinkingSequence(sequence);

			try (ObjectOutputStream os = outputStream();) {
				os.writeObject(generationInfo);
			}

			// System.out.println("### size: " + byteArrayOutputStream.toByteArray().length);

			GenerationInfo read = (GenerationInfo) inputStream().readObject();
			assertThat(read).isEqualTo(generationInfo);
		}

		@Property(tries = 10)
		void serializeWithSeveralShrinkingSequences(@ForAll @Size(3) List<@Size(max = 10) @From("shrinkingSequence") List<Status>> sequences) throws Exception {
			GenerationInfo generationInfo = new GenerationInfo("4242", 41);
			for (List<Status> sequence : sequences) {
				generationInfo = generationInfo.appendShrinkingSequence(sequence);
			}

			try (ObjectOutputStream os = outputStream();) {
				os.writeObject(generationInfo);
			}

			// System.out.println("### size: " + byteArrayOutputStream.toByteArray().length);

			GenerationInfo read = (GenerationInfo) inputStream().readObject();
			assertThat(read).isEqualTo(generationInfo);
		}

		@Property(tries = 10)
		void serializeWithLongShrinkingSequence(@ForAll("shrinkingSequence") @Size(min = 100, max = 1500) List<Status> sequence) throws Exception {
			GenerationInfo generationInfo = new GenerationInfo("4242", 41)
				.appendShrinkingSequence(sequence);

			try (ObjectOutputStream os = outputStream();) {
				os.writeObject(generationInfo);
			}

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
