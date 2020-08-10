package net.jqwik.engine.discovery;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public class DefaultPropertyAttributes implements PropertyAttributes {
	public static PropertyAttributes from(Property property) {
		Integer tries = property.tries() == Property.TRIES_NOT_SET
							? null
							: property.tries();

		Integer maxDiscardRatio = property.maxDiscardRatio() == Property.MAX_DISCARD_RATIO_NOT_SET
									  ? null
									  : property.maxDiscardRatio();

		ShrinkingMode shrinking = property.shrinking() == ShrinkingMode.NOT_SET
									  ? null
									  : property.shrinking();

		AfterFailureMode afterFailure = property.afterFailure() == AfterFailureMode.NOT_SET
											? null
											: property.afterFailure();

		GenerationMode generation = property.generation() == GenerationMode.NOT_SET
										? null
										: property.generation();

		EdgeCasesMode edgeCases = property.edgeCases() == EdgeCasesMode.NOT_SET
									  ? null
									  : property.edgeCases();

		String stereotype = property.stereotype().equals(Property.STEREOTYPE_NOT_SET)
								? null
								: property.stereotype();

		String seed = property.seed().equals(Property.STEREOTYPE_NOT_SET)
						  ? null
						  : property.seed();

		return new DefaultPropertyAttributes(
			tries,
			maxDiscardRatio,
			shrinking,
			generation,
			afterFailure,
			edgeCases,
			stereotype,
			seed
		);
	}

	private Integer tries;
	private Integer maxDiscardRatio;
	private ShrinkingMode shrinkingMode;
	private GenerationMode generationMode;
	private AfterFailureMode afterFailureMode;
	private EdgeCasesMode edgeCasesMode;
	private String stereotype;
	private String seed;

	private DefaultPropertyAttributes(
		Integer tries,
		Integer maxDiscardRatio,
		ShrinkingMode shrinkingMode,
		GenerationMode generationMode,
		AfterFailureMode afterFailureMode,
		EdgeCasesMode edgeCasesMode,
		String stereotype,
		String seed
	) {
		this.tries = tries;
		this.maxDiscardRatio = maxDiscardRatio;
		this.shrinkingMode = shrinkingMode;
		this.generationMode = generationMode;
		this.afterFailureMode = afterFailureMode;
		this.edgeCasesMode = edgeCasesMode;
		this.stereotype = stereotype;
		this.seed = seed;
	}

	@Override
	public Optional<Integer> tries() {
		return Optional.ofNullable(tries);
	}

	@Override
	public Optional<Integer> maxDiscardRatio() {
		return Optional.ofNullable(maxDiscardRatio);
	}

	@Override
	public Optional<ShrinkingMode> shrinking() {
		return Optional.ofNullable(shrinkingMode);
	}

	@Override
	public Optional<GenerationMode> generation() {
		return Optional.ofNullable(generationMode);
	}

	@Override
	public Optional<AfterFailureMode> afterFailure() {
		return Optional.ofNullable(afterFailureMode);
	}

	@Override
	public Optional<EdgeCasesMode> edgeCases() {
		return Optional.ofNullable(edgeCasesMode);
	}

	@Override
	public Optional<String> stereotype() {
		return Optional.ofNullable(stereotype);
	}

	@Override
	public Optional<String> seed() {
		return Optional.ofNullable(seed);
	}

	@Override
	public void setTries(Integer tries) {
		this.tries = tries;
	}

	@Override
	public void setMaxDiscardRatio(Integer maxDiscardRatio) {
		this.maxDiscardRatio = maxDiscardRatio;
	}

	@Override
	public void setShrinking(ShrinkingMode shrinkingMode) {
		this.shrinkingMode = shrinkingMode;
	}

	@Override
	public void setGeneration(GenerationMode generationMode) {
		this.generationMode = generationMode;
	}

	@Override
	public void setAfterFailure(AfterFailureMode afterFailureMode) {
		this.afterFailureMode = afterFailureMode;
	}

	@Override
	public void setEdgeCases(EdgeCasesMode edgeCasesMode) {
		this.edgeCasesMode = edgeCasesMode;
	}

	@Override
	public void setStereotype(String stereotype) {
		this.stereotype = stereotype;
	}

	@Override
	public void setSeed(String seed) {
		this.seed = seed;
	}
}
