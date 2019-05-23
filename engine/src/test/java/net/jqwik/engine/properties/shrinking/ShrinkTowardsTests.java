package net.jqwik.engine.properties.shrinking;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

class ShrinkTowardsTests {

	@Example
	void longs(@ForAll Random random) {
		Arbitrary<Long> longs = Arbitraries.longs().shrinkTowards(50);
		Long shrunkValue = ArbitraryTestHelper.shrinkToEnd(longs, random);
		Assertions.assertThat(shrunkValue).isEqualTo(50);
	}
}
