package net.jqwik.properties;

import net.jqwik.api.*;
import org.assertj.core.api.*;
import org.junit.platform.engine.reporting.*;

import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

class ValueShrinkerTests {

	@Example
	void unshrinkableValueIsShrinkedToItself() {
		Shrinkable<String> unshrinkable = Shrinkable.unshrinkable("hello");

		MockFalsifier<String> falsifier = MockFalsifier.falsifyAll();
		AssertionError originalError = new AssertionError();
		ValueShrinker<String> singleValueShrinker = new ValueShrinker<>(unshrinkable, ignore -> {}, ShrinkingMode.FULL, ignore -> {});

		ShrinkResult<Shrinkable<String>> shrinkResult = singleValueShrinker.shrink(falsifier, originalError);
		assertThat(shrinkResult.shrunkValue()).isSameAs(unshrinkable);
		assertThat(shrinkResult.throwable().get()).isSameAs(originalError);
	}

	@Example
	void shrinkSingletonShrinkSetToFalsifiedValueWithLowestDistance() {
		Shrinkable<Integer> shrinkable = ArbitraryTestHelper.shrinkableInteger(10);
		MockFalsifier<Integer> falsifier = MockFalsifier.falsifyWhen(anInt -> anInt < 3);
		ValueShrinker<Integer> singleValueShrinker = new ValueShrinker<>(shrinkable, ignore -> {}, ShrinkingMode.FULL, ignore -> {});
		ShrinkResult<Shrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier, null);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo(3);
		assertThat(shrinkResult.throwable()).isNotPresent();
	}

	@Example
	void shrinkMultiShrinkSetToFalsifiedValueWithLowestDistance() {
		Shrinkable<String> shrinkable = ArbitraryTestHelper.shrinkableString("hello this is a longer sentence.");
		MockFalsifier<String> falsifier = MockFalsifier.falsifyWhen(aString -> aString.length() < 3 || !aString.startsWith("h"));
		ValueShrinker<String> singleValueShrinker = new ValueShrinker<>(shrinkable, ignore -> {}, ShrinkingMode.FULL, ignore -> {});
		ShrinkResult<Shrinkable<String>> shrinkResult = singleValueShrinker.shrink(falsifier, null);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo("haa");
		assertThat(shrinkResult.throwable()).isNotPresent();
	}

	@Example
	void shrinkWithAssertionError() {
		Shrinkable<Integer> shrinkable = ArbitraryTestHelper.shrinkableInteger(10);
		Predicate<Integer> falsifier = anInt -> {
			Assertions.assertThat(anInt).isEqualTo(0);
			return true;
		};
		ValueShrinker<Integer> singleValueShrinker = new ValueShrinker<>(shrinkable, ignore -> {}, ShrinkingMode.FULL, ignore -> {});
		ShrinkResult<Shrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier, null);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo(1);
		assertThat(shrinkResult.throwable()).isPresent();
		assertThat(shrinkResult.throwable().get()).isInstanceOf(AssertionError.class);
	}

	@Example
	void shrinkResultsOutsideAssumptionsAreNotConsidered() {
		Shrinkable<Integer> shrinkable = ArbitraryTestHelper.shrinkableInteger(10);
		Predicate<Integer> falsifier = anInt -> {
			Assumptions.assumeThat(anInt % 2 == 0);
			return anInt < 3;
		};
		ValueShrinker<Integer> singleValueShrinker = new ValueShrinker<>(shrinkable, ignore -> {}, ShrinkingMode.FULL, ignore -> {});
		ShrinkResult<Shrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier, null);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo(4);
		assertThat(shrinkResult.throwable()).isNotPresent();
	}

	@Example
	void boundedShrinkingWillStopAfter1000Steps() {
		Shrinkable<Integer> shrinkable = ArbitraryTestHelper.shrinkableInteger(2000);
		ReportEntry[] lastEntry = new ReportEntry[1];
		Consumer<ReportEntry> reporter = entry -> {lastEntry[0] = entry;};
		ValueShrinker<Integer> singleValueShrinker = new ValueShrinker<>(shrinkable, reporter, ShrinkingMode.BOUNDED, ignore -> {});
		ShrinkResult<Shrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(MockFalsifier.falsifyAll(), null);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo(1000);
		assertThat(lastEntry[0].getKeyValuePairs()).containsKeys("shrinking bound reached");
	}

}
