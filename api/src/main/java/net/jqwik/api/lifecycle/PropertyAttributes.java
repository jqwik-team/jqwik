package net.jqwik.api.lifecycle;

import java.util.*;

import net.jqwik.api.*;

public interface PropertyAttributes {

	Optional<Integer> tries();

	Optional<Integer> maxDiscardRatio();

	Optional<ShrinkingMode> shrinking();

	Optional<GenerationMode> generation();

	Optional<AfterFailureMode> afterFailure();

	Optional<EdgeCasesMode> edgeCases();

	Optional<String> stereotype();

	Optional<String> seed();

	void setTries(Integer tries);

	void setMaxDiscardRatio(Integer maxDiscardRatio);

	void setShrinking(ShrinkingMode shrinkingMode);

	void setGeneration(GenerationMode generationMode);

	void setAfterFailure(AfterFailureMode afterFailureMode);

	void setEdgeCases(EdgeCasesMode edgeCasesMode);

	void setStereotype(String stereotype);

	void setSeed(String seed);

}
