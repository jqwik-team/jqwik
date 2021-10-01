package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Represents a property method's attributes which are set (or not)
 * in attributes of the {@linkplain Property} annotation.
 *
 * <p>
 * This object can be used to query, set or change a property's attribute
 * during the {@linkplain AroundPropertyHook} lifecycle hook.
 * </p>
 */
@API(status = EXPERIMENTAL, since = "1.3.4")
public interface PropertyAttributes {

	/**
	 * The number of tries to be run in the property at hand.
	 * Only present when set explicitly through {@linkplain Property#tries()}
	 * or {@linkplain #setTries(Integer)}.
	 *
	 * <p>
	 * jqwik may override an explicit tries value if exhaustive or data-driven
	 * generation is chosen.
	 * </p>
	 *
	 * @return optional number of tries
	 */
	Optional<Integer> tries();

	/**
	 * The maximum allowed discard ration in the property at hand.
	 * Only present when set explicitly through {@linkplain Property#maxDiscardRatio()}
	 * or {@linkplain #setMaxDiscardRatio(Integer)}.
	 *
	 * @return optional maximum discard ratio
	 */
	Optional<Integer> maxDiscardRatio();

	/**
	 * The shrinking mode of the property at hand.
	 * Only present when set explicitly through {@linkplain Property#shrinking()}
	 * or {@linkplain #setShrinking(ShrinkingMode)}.
	 *
	 * @return optional shrinking mode
	 */
	Optional<ShrinkingMode> shrinking();

	/**
	 * The generation mode of the property at hand.
	 * Only present when set explicitly through {@linkplain Property#generation()}
	 * or {@linkplain #setGeneration(GenerationMode)}.
	 *
	 * @return optional generation mode
	 */
	Optional<GenerationMode> generation();

	/**
	 * The after failure mode of the property at hand.
	 * Only present when set explicitly through {@linkplain Property#afterFailure()}
	 * or {@linkplain #setAfterFailure(AfterFailureMode)}.
	 *
	 * @return optional after failure mode
	 */
	Optional<AfterFailureMode> afterFailure();

	/**
	 * The edge cases mode of the property at hand.
	 * Only present when set explicitly through {@linkplain Property#edgeCases()}
	 * or {@linkplain #setEdgeCases(EdgeCasesMode)}.
	 *
	 * @return optional edge cases mode
	 */
	Optional<EdgeCasesMode> edgeCases();

	/**
	 * The stereotype of the property at hand.
	 * Only present when set explicitly through {@linkplain Property#stereotype()}
	 * or {@linkplain #setStereotype(String)}.
	 *
	 * @return optional stereotype
	 */
	Optional<String> stereotype();

	/**
	 * The random seed used when running the property at hand.
	 * Only present when set explicitly through {@linkplain Property#seed()}
	 * or {@linkplain #setSeed(String)}.
	 *
	 * @return optional random seed
	 */
	Optional<String> seed();

	/**
	 * The fixed seed mode of the property at hand.
	 * Only present when set explicitly through {@linkplain Property#whenFixedSeed()} ()}
	 * or {@linkplain #setWhenFixedSeed(FixedSeedMode)}.
	 *
	 * @return optional fixed seed mode
	 */
	@API(status = EXPERIMENTAL, since = "1.4.0")
	Optional<FixedSeedMode> whenFixedSeed();

	void setTries(Integer tries);

	void setMaxDiscardRatio(Integer maxDiscardRatio);

	void setShrinking(ShrinkingMode shrinkingMode);

	void setGeneration(GenerationMode generationMode);

	void setAfterFailure(AfterFailureMode afterFailureMode);

	void setEdgeCases(EdgeCasesMode edgeCasesMode);

	void setStereotype(String stereotype);

	void setSeed(String seed);

	void setWhenFixedSeed(FixedSeedMode fixedSeedMode);

}
