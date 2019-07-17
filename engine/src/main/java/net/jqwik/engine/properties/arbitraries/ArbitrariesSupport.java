package net.jqwik.engine.properties.arbitraries;

import net.jqwik.api.*;

public class ArbitrariesSupport {
	public static int maxNumberOfElements(Arbitrary<?> elementArbitrary, int defaultNumber) {
		return elementArbitrary
				   .exhaustive()
				   .map(generator -> {
					   long maxCount = generator.maxCount();
					   return (int) Math.min(maxCount, defaultNumber);
				   })
				   .orElse(defaultNumber);
	}
}
