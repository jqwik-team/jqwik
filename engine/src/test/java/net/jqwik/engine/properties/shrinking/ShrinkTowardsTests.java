package net.jqwik.engine.properties.shrinking;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

class ShrinkTowardsTests {

	@Example
	void bytes(@ForAll Random random) {
		Arbitrary<Byte> bytes = Arbitraries.bytes().shrinkTowards(50);
		byte shrunkValue = ArbitraryTestHelper.shrinkToEnd(bytes, random);
		Assertions.assertThat(shrunkValue).isEqualTo((byte) 50);
	}

	@Example
	void longs(@ForAll Random random) {
		Arbitrary<Long> longs = Arbitraries.longs().shrinkTowards(50);
		long shrunkValue = ArbitraryTestHelper.shrinkToEnd(longs, random);
		Assertions.assertThat(shrunkValue).isEqualTo(50);
	}
}
