package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.facades.*;

public class TestingSupportFacadeImpl extends TestingSupportFacade {

	@Override
	public  <T> Shrinkable<T> generateUntil(RandomGenerator<T> generator, Random random, Function<T, Boolean> condition) {
		long maxTries = 1000;
		return generator
					   .stream(random)
					   .limit(maxTries)
					   .filter(shrinkable -> condition.apply(shrinkable.value()))
					   .findFirst()
					   .orElseThrow(() -> new JqwikException("Failed to generate value that fits condition after " + maxTries + " tries."));
	}


}
