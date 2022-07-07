package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.*;

/**
 * Is loaded through reflection in api module
 */
public class CombinatorsFacadeImpl extends Combinators.CombinatorsFacade {

	@Override
	public <R> Arbitrary<R> combine(Function<List<Object>, R> combinator, Arbitrary<?>... arbitraries) {
		return new CombineArbitrary<>(combinator, arbitraries);
	}

}
