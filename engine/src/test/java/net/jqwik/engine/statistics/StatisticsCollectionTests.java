package net.jqwik.engine.statistics;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.hooks.statistics.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.data.Percentage.*;

@Group
class StatisticsCollectionTests {

	@Group
	class Counting {
		@Example
		void countSingleValues() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("two");
			collector.collect("three");
			collector.collect("two");
			collector.collect("one");
			collector.collect("three");
			collector.collect("three");

			Map<List<Object>, Integer> counts = collector.getCounts();
			assertThat(counts.get(asList("one"))).isEqualTo(1);
			assertThat(counts.get(asList("two"))).isEqualTo(2);
			assertThat(counts.get(asList("three"))).isEqualTo(3);
		}

		@Example
		void countDoubleValues() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("two", 2);
			collector.collect("three", 3);
			collector.collect("two", 2);
			collector.collect("one", 1);
			collector.collect("three", 3);
			collector.collect("three", 3);

			Map<List<Object>, Integer> counts = collector.getCounts();
			assertThat(counts.get(asList("one", 1))).isEqualTo(1);
			assertThat(counts.get(asList("two", 2))).isEqualTo(2);
			assertThat(counts.get(asList("three", 3))).isEqualTo(3);
		}

		@Example
		void callingCollectWithNoValueFails() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			assertThatThrownBy(() -> collector.collect()).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void callingCollectWithDifferentNumberOfValuesFails() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("a string", 1);
			collector.collect("another string", null);
			collector.collect(null, 2);

			assertThatThrownBy(() -> collector.collect("just a string")).isInstanceOf(IllegalArgumentException.class);
			assertThatThrownBy(() -> collector.collect("a string", 3, new Object())).isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Group
	class Percentages {

		@Example
		void exactPercentages() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("two");
			collector.collect("three");
			collector.collect("two");
			collector.collect("four");
			collector.collect("four");
			collector.collect("one");
			collector.collect("three");
			collector.collect("four");
			collector.collect("four");
			collector.collect("three");

			assertThat(collector.percentage("four")).isEqualTo(40.0);
			assertThat(collector.percentage("three")).isEqualTo(30.0);
			assertThat(collector.percentage("two")).isEqualTo(20.0);
			assertThat(collector.percentage("one")).isEqualTo(10.0);
		}

		@Example
		void unseenValuesHaveZeroPercentage() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("two");
			collector.collect("one");

			assertThat(collector.percentage("zero")).isEqualTo(0.0);
			assertThat(collector.percentage((Object[]) null)).isEqualTo(0.0);
		}

		@Example
		void nullValueIsAlsoCounted() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("one");
			collector.collect((Object) null);

			assertThat(collector.percentage("one")).isEqualTo(50.0);
			assertThat(collector.percentage((Object) null)).isEqualTo(50.0);
		}

		@Example
		void circaPercentages() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("one");
			collector.collect("two");
			collector.collect("three");

			assertThat(collector.percentage("one")).isCloseTo(33.3, withPercentage(1));
			assertThat(collector.percentage("two")).isCloseTo(33.3, withPercentage(1));
			assertThat(collector.percentage("three")).isCloseTo(33.3, withPercentage(1));
		}

		@Example
		void percentagesAreRecalculated() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("one");
			collector.collect("two");

			assertThat(collector.percentage("one")).isEqualTo(50.0);
			assertThat(collector.percentage("two")).isEqualTo(50.0);

			collector.collect("three");
			collector.collect("three");

			assertThat(collector.percentage("one")).isEqualTo(25.0);
			assertThat(collector.percentage("two")).isEqualTo(25.0);
			assertThat(collector.percentage("three")).isEqualTo(50.0);
		}

	}

	@Group
	class Counts {

		@Example
		void exactCounts() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("two");
			collector.collect("three");
			collector.collect("two");
			collector.collect("four");
			collector.collect("four");
			collector.collect("one");
			collector.collect("three");
			collector.collect("four");
			collector.collect("four");
			collector.collect("three");

			assertThat(collector.countAllCollects()).isEqualTo(10);
			assertThat(collector.count("four")).isEqualTo(4);
			assertThat(collector.count("three")).isEqualTo(3);
			assertThat(collector.count("two")).isEqualTo(2);
			assertThat(collector.count("one")).isEqualTo(1);
		}

		@Example
		void unseenValuesHaveZeroCount() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("two");
			collector.collect("one");

			assertThat(collector.count("zero")).isEqualTo(0);
			assertThat(collector.count((Object) null)).isEqualTo(0);
		}

		@Example
		void nullArrayIsCountedAsNullValue() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("one");
			collector.collect((Object[]) null);

			assertThat(collector.count("one")).isEqualTo(1);
			assertThat(collector.count((Object) null)).isEqualTo(1);
		}

		@Example
		void nullValueIsAlsoCounted() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("one");
			collector.collect((Object) null);

			assertThat(collector.count("one")).isEqualTo(1);
			assertThat(collector.count((Object) null)).isEqualTo(1);
		}

		@Example
		void nullValueIsAlsoCountedWhenPartOfManyValues() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("one", null);
			collector.collect("one", null);
			collector.collect(null, "two");
			collector.collect(null, "two");

			assertThat(collector.count("one", null)).isEqualTo(2);
			assertThat(collector.count(null, "two")).isEqualTo(2);
		}

		@Example
		void countsAreRecalculated() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("one");
			collector.collect("two");

			assertThat(collector.count("one")).isEqualTo(1);
			assertThat(collector.count("two")).isEqualTo(1);

			collector.collect("two");
			collector.collect("three");
			collector.collect("three");
			collector.collect("three");

			assertThat(collector.count("one")).isEqualTo(1);
			assertThat(collector.count("two")).isEqualTo(2);
			assertThat(collector.count("three")).isEqualTo(3);
		}

	}
}
