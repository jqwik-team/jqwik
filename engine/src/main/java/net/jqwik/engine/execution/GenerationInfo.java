package net.jqwik.engine.execution;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.shrinking.*;

public class GenerationInfo implements Serializable {

	public final static GenerationInfo NULL = new GenerationInfo(null);

	private final String randomSeed;
	private final int generationIndex;

	// Store ordinals instead of enum objects so that serialization
	// in jqwik.database uses less disk space
	private final List<Byte> byteSequence;

	public GenerationInfo(String randomSeed) {
		this(randomSeed, 0);
	}

	public GenerationInfo(String randomSeed, int generationIndex) {
		this(randomSeed, generationIndex, Collections.emptyList());
	}

	private GenerationInfo(String randomSeed, int generationIndex, List<Byte> byteSequence) {
		this.randomSeed = randomSeed != null ? (randomSeed.isEmpty() ? null : randomSeed) : null;
		this.generationIndex = generationIndex;
		this.byteSequence = byteSequence;
	}

	private List<Byte> toByteSequence(List<TryExecutionResult.Status> shrinkingSequence) {
		return shrinkingSequence.stream().map(status -> (byte) status.ordinal()).collect(Collectors.toList());
	}

	public GenerationInfo appendShrinkingSequence(List<TryExecutionResult.Status> toAppend) {
		List<Byte> newByteSequence = new ArrayList<>(byteSequence);
		newByteSequence.addAll(toByteSequence(toAppend));
		return new GenerationInfo(randomSeed, generationIndex, newByteSequence);
	}

	public Optional<String> randomSeed() {
		return Optional.ofNullable(randomSeed);
	}

	public int generationIndex() {
		return generationIndex;
	}

	public Optional<List<Shrinkable<Object>>> generateOn(ParametersGenerator generator, TryLifecycleContext context) {
		List<Shrinkable<Object>> sample = useGenerationIndex(generator, context);
		if (sample == null) {
			return Optional.empty();
		}
		return useShrinkingSequence(sample);
	}

	private Optional<List<Shrinkable<Object>>> useShrinkingSequence(List<Shrinkable<Object>> sample) {
		ShrunkSampleRecreator recreator = new ShrunkSampleRecreator(sample);
		return recreator.recreateFrom(shrinkingSequence());
	}

	private List<Shrinkable<Object>> useGenerationIndex(ParametersGenerator generator, TryLifecycleContext context) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GenerationInfo that = (GenerationInfo) o;
		if (generationIndex != that.generationIndex) return false;
		if (!Objects.equals(randomSeed, that.randomSeed)) return false;
		return byteSequence.equals(that.byteSequence);
	}

	@Override
	public int hashCode() {
		int result = randomSeed != null ? randomSeed.hashCode() : 0;
		result = 31 * result + generationIndex;
		return result;
	}

	@Override
	public String toString() {
		return String.format("GenerationInfo(seed=%s,index=%s,shrinkingSequence.size=%s)", randomSeed, generationIndex, byteSequence.size());
	}

	public List<TryExecutionResult.Status> shrinkingSequence() {
		return byteSequence.stream().map(ordinal -> TryExecutionResult.Status.values()[ordinal]).collect(Collectors.toList());
	}

}
