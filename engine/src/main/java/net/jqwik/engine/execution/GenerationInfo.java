package net.jqwik.engine.execution;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.shrinking.*;

import org.jspecify.annotations.*;

public class GenerationInfo implements Serializable {

	public final static GenerationInfo NULL = new GenerationInfo(null);

	private final @Nullable String randomSeed;
	private final int generationIndex;

	// Store ordinals instead of enum objects so that serialization
	// in jqwik.database uses less disk space
	private final List<List<Byte>> byteSequences;

	public GenerationInfo(@Nullable String randomSeed) {
		this(randomSeed, 0);
	}

	public GenerationInfo(@Nullable String randomSeed, int generationIndex) {
		this(randomSeed, generationIndex, Collections.emptyList());
	}

	private GenerationInfo(@Nullable String randomSeed, int generationIndex, List<List<Byte>> byteSequences) {
		this.randomSeed = randomSeed != null ? (randomSeed.isEmpty() ? null : randomSeed) : null;
		this.generationIndex = generationIndex;
		this.byteSequences = byteSequences;
	}

	private List<Byte> toByteSequence(List<TryExecutionResult.Status> shrinkingSequence) {
		return shrinkingSequence.stream().map(status -> (byte) status.ordinal()).collect(Collectors.toList());
	}

	public GenerationInfo appendShrinkingSequence(List<TryExecutionResult.Status> toAppend) {
		if (toAppend.isEmpty()) {
			return this;
		}
		List<List<Byte>> newByteSequences = new ArrayList<>(byteSequences);
		newByteSequences.add(toByteSequence(toAppend));
		return new GenerationInfo(randomSeed, generationIndex, newByteSequences);
	}

	public Optional<String> randomSeed() {
		return Optional.ofNullable(randomSeed);
	}

	public int generationIndex() {
		return generationIndex;
	}

	public Optional<List<Shrinkable<Object>>> generateOn(ParametersGenerator generator, TryLifecycleContext context) {
		List<Shrinkable<Object>> sample = useGenerationIndex(generator, context);
		return useShrinkingSequences(sample);
	}

	private Optional<List<Shrinkable<Object>>> useShrinkingSequences(@Nullable List<Shrinkable<Object>> sample) {
		Optional<List<Shrinkable<Object>>> shrunkSample = Optional.ofNullable(sample);
		for (List<TryExecutionResult.Status> shrinkingSequence : shrinkingSequences()) {
			if (!shrunkSample.isPresent()) {
				break;
			}
			shrunkSample = shrink(shrunkSample.get(), shrinkingSequence);
		}
		return shrunkSample;
	}

	private Optional<List<Shrinkable<Object>>> shrink(
		List<Shrinkable<Object>> sample,
		List<TryExecutionResult.Status> shrinkingSequence
	) {
		ShrunkSampleRecreator recreator = new ShrunkSampleRecreator(sample);
		return recreator.recreateFrom(shrinkingSequence);
	}

	private @Nullable List<Shrinkable<Object>> useGenerationIndex(ParametersGenerator generator, TryLifecycleContext context) {
		List<Shrinkable<Object>> sample = null;
		for (int i = 0; i < generationIndex; i++) {
			if (generator.hasNext()) {
				sample = generator.next(context);
			} else {
				return null;
			}
		}
		return sample;
	}

	public List<List<TryExecutionResult.Status>> shrinkingSequences() {
		return byteSequences.stream()
							.map(this::toShrinkingSequence)
							.collect(Collectors.toList());
	}

	private List<TryExecutionResult.Status> toShrinkingSequence(List<Byte> sequence) {
		return sequence.stream().map(ordinal -> TryExecutionResult.Status.values()[ordinal]).collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GenerationInfo that = (GenerationInfo) o;
		if (generationIndex != that.generationIndex) return false;
		if (!Objects.equals(randomSeed, that.randomSeed)) return false;
		return byteSequences.equals(that.byteSequences);
	}

	@Override
	public int hashCode() {
		int result = randomSeed != null ? randomSeed.hashCode() : 0;
		result = 31 * result + generationIndex;
		return result;
	}

	@Override
	public String toString() {
		List<String> sizes = byteSequences.stream().map(bytes -> "size=" + bytes.size()).collect(Collectors.toList());
		Tuple.Tuple3<String, Integer, List<String>> tuple = Tuple.of(randomSeed, generationIndex, sizes);
		return String.format("GenerationInfo%s", tuple);
	}
}
