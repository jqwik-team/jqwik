package net.jqwik.engine.execution;

import java.io.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.shrinking.*;

public class GenerationInfo implements Serializable {

	public final static GenerationInfo NULL = new GenerationInfo(null);

	private final String randomSeed;
	private final int generationIndex;
	private final List<TryExecutionResult.Status> shrinkingSequence;

	public GenerationInfo(String randomSeed) {
		this(randomSeed, 0);
	}

	public GenerationInfo(String randomSeed, int generationIndex) {
		this(randomSeed, generationIndex, Collections.emptyList());
	}

	private GenerationInfo(String randomSeed, int generationIndex, List<TryExecutionResult.Status> shrinkingSequence) {
		this.randomSeed = randomSeed != null ? (randomSeed.isEmpty() ? null : randomSeed) : null;
		this.generationIndex = generationIndex;
		this.shrinkingSequence = shrinkingSequence;
	}

	public GenerationInfo appendShrinkingSequence(List<TryExecutionResult.Status> toAppend) {
		List<TryExecutionResult.Status> newShrinkingSequence = new ArrayList<>(shrinkingSequence);
		newShrinkingSequence.addAll(toAppend);
		return new GenerationInfo(randomSeed, generationIndex, newShrinkingSequence);
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
		return recreator.recreateFrom(shrinkingSequence);
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
		return generationIndex == that.generationIndex && Objects.equals(randomSeed, that.randomSeed);
	}

	@Override
	public int hashCode() {
		return Objects.hash(randomSeed, generationIndex);
	}

	@Override
	public String toString() {
		return String.format("GenerationInfo(seed=%s,index=%s,shrinkingSequence.size=%s)", randomSeed, generationIndex, shrinkingSequence.size());
	}
}
