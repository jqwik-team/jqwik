package net.jqwik.engine.properties;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

import static java.util.Arrays.*;

class EdgeCasesGenerationTests {

	private final List<List<Object>> generated = new ArrayList<>();

	@Property(tries = 20, edgeCases = EdgeCasesMode.FIRST)
	@PerProperty(CheckIntEdgeCasesGeneratedFirst.class)
	void intProperty(@ForAll @IntRange(min = -100, max = 100) int anInt) {
		generated.add(asList(anInt));
	}

	private class CheckIntEdgeCasesGeneratedFirst implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			Assertions.assertThat(generated).startsWith(asList(-2), asList(-1), asList(0), asList(1), asList(2));
		}
	}

}


