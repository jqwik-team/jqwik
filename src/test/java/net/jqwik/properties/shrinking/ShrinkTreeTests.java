package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

class ShrinkTreeTests {

	private ShrinkTree<String> shrinkTree = new ShrinkTree<>();

	@Example
	void addingRoutes() {
		List<ShrinkValue<String>> route1 = route(
			ShrinkValue.of("aa", 2),
			ShrinkValue.of("a", 0)
		);
		List<ShrinkValue<String>> route2 = route(
			ShrinkValue.of("", 0)
		);

		shrinkTree.addRoute(route1);
		shrinkTree.addRoute(route2);

		assertThat(shrinkTree.shrinkingRoutes()).hasSize(2);
		assertThat(shrinkTree.shrinkingRoutes().get(0)).hasSize(2);
		assertThat(shrinkTree.shrinkingRoutes().get(1)).hasSize(1);
	}

	@Example
	void emptyTreeYieldsNoFalsifiedResult() {
		Predicate<String> falsifier = dontCare -> false;
		assertThat(shrinkTree.falsify(falsifier)).isEmpty();
	}

	@Example
	void treeWithSingleRouteFalsifiesToLastValueThatEvaluatesToFalse() {
		Predicate<String> falsifier = value -> value.isEmpty();
		shrinkTree.addRoute(route(
			ShrinkValue.of("aaa", 3),
			ShrinkValue.of("aa", 2),
			ShrinkValue.of("a", 1),
			ShrinkValue.of("", 0)
		));
		assertThat(shrinkTree.falsify(falsifier)).containsExactly(
			ShrinkResult.of(ShrinkValue.of("a", 1), null)
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
		shrinkTree.addRoute(route(
			ShrinkValue.of("aaa", 3),
			ShrinkValue.of("aa", 2),
			ShrinkValue.of("a", 1),
			ShrinkValue.of("", 0)
		));
		assertThat(shrinkTree.falsify(falsifier)).containsExactly(
			ShrinkResult.of(ShrinkValue.of("a", 1), assertionError)
		);
	}

	@Example
	void treeWithSeveralRouteFalsifiesToAllFalsifyingLastValues() {
		Predicate<String> falsifier = value -> value.isEmpty();
		shrinkTree.addRoute(route(
			ShrinkValue.of("aaa", 3),
			ShrinkValue.of("aa", 2),
			ShrinkValue.of("a", 1),
			ShrinkValue.of("", 0)
		));
		shrinkTree.addRoute(route(
			ShrinkValue.of("bbb", 3),
			ShrinkValue.of("bb", 2),
			ShrinkValue.of("b", 1),
			ShrinkValue.of("", 0)
		));
		shrinkTree.addRoute(route(
			ShrinkValue.of("", 0)
		));

		assertThat(shrinkTree.falsify(falsifier)).containsExactly(
			ShrinkResult.of(ShrinkValue.of("a", 1), null),
			ShrinkResult.of(ShrinkValue.of("b", 1), null)
		);
	}

	@Example
	void anyExceptionButAssertionErrorDoesNotFalsify() {
		Predicate<String> falsifier = value -> {
			throw new RuntimeException();
		};
		shrinkTree.addRoute(route(
			ShrinkValue.of("aaa", 3),
			ShrinkValue.of("aa", 2),
			ShrinkValue.of("a", 1),
			ShrinkValue.of("", 0)
		));
		assertThat(shrinkTree.falsify(falsifier)).isEmpty();
	}

	private List<ShrinkValue<String>> route(ShrinkValue<String>... values) {
		return Arrays.asList(values);
	}
}
