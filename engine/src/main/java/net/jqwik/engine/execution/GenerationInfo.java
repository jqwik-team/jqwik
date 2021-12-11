package net.jqwik.engine.execution;

import java.io.*;
import java.util.*;

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

	@Override
	public String toString() {
		return String.format("GenerationInfo(seed=%s,index=%s)", randomSeed, generationIndex);
	}
}
