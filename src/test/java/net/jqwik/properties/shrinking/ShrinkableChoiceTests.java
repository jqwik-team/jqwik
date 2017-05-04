package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

class ShrinkableChoiceTests {

	private ShrinkableChoice<String> shrinkTree = new ShrinkableChoice<>();

	@Example
	void addingRoutes() {
		shrinkTree.addChoice(ShrinkableValue.of("1", 1));
		shrinkTree.addChoice(ShrinkableValue.of("2", 2));

		assertThat(shrinkTree.choices()).hasSize(2);
		assertThat(shrinkTree.choices().get(0)).isEqualTo(ShrinkableValue.of("1", 1));
		assertThat(shrinkTree.choices().get(1)).isEqualTo(ShrinkableValue.of("2", 2));
	}

	@Example
	void emptyTreeYieldsNoFalsifiedResult() {
		Predicate<String> falsifier = dontCare -> false;
		assertThat(shrinkTree.shrink(falsifier)).isEmpty();
	}

	@Example
	void treeWithSingleRouteFalsifiesToLastValueThatEvaluatesToFalse() {
		Predicate<String> falsifier = value -> value.isEmpty();
		shrinkTree.addChoice(route(
			ShrinkableValue.of("aaa", 3),
			ShrinkableValue.of("aa", 2),
			ShrinkableValue.of("a", 1),
			ShrinkableValue.of("", 0)
		));
		assertThat(shrinkTree.shrink(falsifier).get()).isEqualTo(
			ShrinkResult.of(ShrinkableValue.of("a", 1), null)
		);
	}

	@Example
	void assertionErrorAlsoFalsifies() {
		AssertionError assertionError = new AssertionError();
		Predicate<String> falsifier = value -> {
			if (!value.isEmpty())
				throw assertionError;
			return true;
		};
		shrinkTree.addChoice(route(
			ShrinkableValue.of("aaa", 3),
			ShrinkableValue.of("aa", 2),
			ShrinkableValue.of("a", 1),
			ShrinkableValue.of("", 0)
		));
		assertThat(shrinkTree.shrink(falsifier).get()).isEqualTo(
			ShrinkResult.of(ShrinkableValue.of("a", 1), assertionError)
		);
	}

	@Example
	void treeWithSeveralRouteFalsifiesToAllFalsifyingLastValues() {
		Predicate<String> falsifier = value -> value.isEmpty();
		shrinkTree.addChoice(route(
			ShrinkableValue.of("aaa", 3),
			ShrinkableValue.of("aa", 2),
			ShrinkableValue.of("a", 1),
			ShrinkableValue.of("", 0)
		));
		shrinkTree.addChoice(route(
			ShrinkableValue.of("bbb", 3),
			ShrinkableValue.of("bb", 2),
			ShrinkableValue.of("b", 1),
			ShrinkableValue.of("", 0)
		));
		shrinkTree.addChoice(route(
			ShrinkableValue.of("", 0)
		));

		assertThat(shrinkTree.shrink(falsifier).get()).isEqualTo(
			ShrinkResult.of(ShrinkableValue.of("a", 1), null)
		);
	}

	@Example
	void anyExceptionButAssertionErrorDoesNotFalsify() {
		Predicate<String> falsifier = value -> {
			throw new RuntimeException();
		};
		shrinkTree.addChoice(route(
			ShrinkableValue.of("aaa", 3),
			ShrinkableValue.of("aa", 2),
			ShrinkableValue.of("a", 1),
			ShrinkableValue.of("", 0)
		));
		assertThat(shrinkTree.shrink(falsifier)).isEmpty();
	}

	private List<Shrinkable<String>> route(Shrinkable<String>... values) {
		return Arrays.asList(values);
	}
}
