package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;
import org.assertj.core.api.*;
import org.mockito.*;

import java.util.*;
import java.util.function.*;

import static java.util.Arrays.asList;

public class FalsifiedShrinkerTests {

	private FalsifiedShrinker shrinker;

	@Example
	void ifShrinkerDoesntProvideAnythingOriginalParametersAreReturned() {
		Arbitrary<Integer> a1 = Mockito.mock(Arbitrary.class);
		Mockito.when(a1.shrink(42)).thenReturn(ShrinkTree.empty());
		Arbitrary<Integer> a2 = Mockito.mock(Arbitrary.class);
		Mockito.when(a1.shrink(43)).thenReturn(ShrinkTree.empty());
		List<Arbitrary> arbitraries = asList(a1, a2);

		Function<List<Object>, Boolean> alwaysFail = params -> false;
		shrinker = new FalsifiedShrinker(arbitraries, alwaysFail);

		List<Object> shrinkedParams = shrinker.shrink(asList(42, 43));

		Assertions.assertThat(shrinkedParams).containsExactly(42, 43);
	}
}
