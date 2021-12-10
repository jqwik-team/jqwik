package net.jqwik.engine.execution;

import java.util.*;

public class GenerationInfo {

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
}
