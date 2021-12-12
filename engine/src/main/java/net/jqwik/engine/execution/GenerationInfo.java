package net.jqwik.engine.execution;

import java.io.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public class GenerationInfo implements Serializable {

	public final static GenerationInfo NULL = new GenerationInfo(null);

	private final String randomSeed;
	private final int generationIndex;

	public GenerationInfo(String randomSeed) {
		this(randomSeed, 0);
	}

	public GenerationInfo(String randomSeed, int generationIndex) {
		this.randomSeed = randomSeed != null ? (randomSeed.isEmpty() ? null : randomSeed) : null;
		this.generationIndex = generationIndex;
	}

	public Optional<String> randomSeed() {
		return Optional.ofNullable(randomSeed);
	}

	public int generationIndex() {
		return generationIndex;
	}

	public List<Shrinkable<Object>> generateOn(ParametersGenerator generator, TryLifecycleContext context) {
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
		return String.format("GenerationInfo(seed=%s,index=%s)", randomSeed, generationIndex);
	}
}
