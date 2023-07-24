package net.jqwik.api;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * The edge-cases mode determines if and when combined edge-cases of all parameters will be explicitly generated.
 * It can be set in {@linkplain Property#edgeCases()} for any property method.
 *
 * <p>
 * If it is not set explicitly mode {@linkplain #MIXIN} will be used unless the property has
 * only a single parameter. Then {@linkplain #NONE} applies.
 *
 * @see Property
 */
@API(status = MAINTAINED, since = "1.8.0")
public enum EdgeCasesMode {

	/**
	 * Generate edge cases first.
	 */
	FIRST,

	/**
	 * Mix edge cases into random generation.
	 */
	MIXIN,

	/**
	 * Do not _explicitly_ generate edge cases. They might be generated randomly though.
	 */
	NONE,

	@API(status = INTERNAL)
	NOT_SET;

	@API(status = INTERNAL)
	public boolean generateFirst() {
		return this == FIRST;
	}

	@API(status = INTERNAL)
	public boolean mixIn() {
		return this == MIXIN;
	}

	@API(status = INTERNAL)
	public boolean activated() {
		return mixIn() || generateFirst();
	}
}
