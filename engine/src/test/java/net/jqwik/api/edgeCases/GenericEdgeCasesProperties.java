package net.jqwik.api.edgeCases;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public interface GenericEdgeCasesProperties {

	@Provide
	Arbitrary<Arbitrary<?>> arbitraries();

	@Property
	default void askingForZeroEdgeCases(@ForAll("arbitraries") Arbitrary<?> arbitrary) {
		assertThat(arbitrary.edgeCases(0)).hasSize(0);
	}

	@Property
	default void askingForNegativeNumberOfEdgeCases(@ForAll("arbitraries") Arbitrary<?> arbitrary) {
		assertThat(arbitrary.edgeCases(-42)).hasSize(0);
	}

}
