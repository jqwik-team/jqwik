package net.jqwik.api;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import org.assertj.core.api.*;

import net.jqwik.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
@Label("Arbitrary")
@StatisticsReport(onFailureOnly = true)
class ArbitraryTests {

	@Example
	void generatorWithoutEdgeCases(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = Arbitraries.integers().between(-1000, 1000);
		RandomGenerator<Integer> generator = arbitrary.generator(10, false);
		checkAllGenerated(generator, random, i -> i >= -1000 && i <= 1000);
	}

	@Example
	void fixGenSize() {
		int[] injectedGenSize = {0};

		Arbitrary<Integer> arbitrary =
			new Arbitrary<Integer>() {

				@Override
				public RandomGenerator<Integer> generator(final int genSize) {
					injectedGenSize[0] = genSize;
					return ignore -> Shrinkable.unshrinkable(0);
				}

				@Override
				public EdgeCases<Integer> edgeCases(int maxEdgeCases) {
					return EdgeCases.none();
				}
			};

		RandomGenerator<Integer> notUsed = arbitrary.fixGenSize(42).generator(1000, true);
		assertThat(injectedGenSize[0]).isEqualTo(42);
	}

	@Property(tries = 100)
	void nullsWithProbability50Percent(@ForAll Random random) {
		Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
		Arbitrary<Integer> intsWithNulls = ints.injectNull(0.5);

		List<Integer> listWithNulls = intsWithNulls.list()
												   .ofSize(100)
												   .generator(1000)
												   .next(random)
												   .value();

		List<Integer> listWithoutNulls = new ArrayList<>(listWithNulls);
		listWithoutNulls.removeIf(Objects::isNull);

		Statistics.label("at least 25 nulls")
				  .collect(listWithoutNulls.size() <= 75)
				  .coverage(checker -> checker.check(true).percentage(p -> p > 80));
	}

	@Group
	class GenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.ofSuppliers(
				() -> Arbitraries.integers().map(Object::toString),
				() -> Arbitraries.integers().map(i -> i * 2),
				() -> Arbitraries.integers().filter(i -> i % 2 == 0),
				() -> Arbitraries.integers().map(i -> {
					if (i % 2 == 0) throw new RuntimeException();
					return i;
				}).ignoreException(RuntimeException.class),
				() -> Arbitraries.integers().between(1, 10).flatMap(i -> Arbitraries.strings().ofLength(i)),
				() -> Arbitraries.of(-10, 10).injectNull(0.1),
				() -> Arbitraries.of(-1000, 1000).injectDuplicates(0.1),
				() -> Arbitraries.of(-10, 10).optional(),
				() -> Arbitraries.just(42).tuple5(),
				() -> Arbitraries.integers().withoutEdgeCases(),
				() -> Arbitraries.integers().edgeCases(config -> {}),
				() -> Arbitraries.integers().dontShrink(),
				() -> Arbitraries.integers().between(-10, 10).fixGenSize(100),
				() -> Arbitraries.integers().between(1, 3).collect(list -> sum(list) > 10)
			);
		}
	}

	@Group
	@PropertyDefaults(edgeCases = EdgeCasesMode.MIXIN, tries = 2000)
	class WithoutEdgeCases {

		@Property
		void withoutEdgeCases(@ForAll("listsWithoutEdgeCases") List<Integer> listWithoutEdgeCases) {
			Statistics.label("list is empty")
					  .collect(listWithoutEdgeCases.isEmpty())
					  .coverage(checker -> checker.check(true).percentage(p -> p < 6));

			Statistics.label("list contains Integer.MAX_VALUE")
					  .collect(listWithoutEdgeCases.stream().anyMatch(e -> e == Integer.MAX_VALUE))
					  .coverage(checker -> checker.check(true).percentage(p -> p < 1));
		}

		@Provide
		Arbitrary<List<Integer>> listsWithoutEdgeCases() {
			Arbitrary<Integer> ints = Arbitraries.integers();
			return ints.list().withoutEdgeCases();
		}

		@Property
		void withoutEdgeCasesUsingFilterAndMap(@ForAll("intsMappedAndFilteredWithoutEdgeCases") int intWithoutEdgeCases) {
			Statistics.label("int is Integer.MAX_VALUE")
					  .collect(intWithoutEdgeCases == Integer.MAX_VALUE)
					  .coverage(checker -> checker.check(true).percentage(p -> p < 0.1));
		}

		@Provide
		Arbitrary<Integer> intsMappedAndFilteredWithoutEdgeCases() {
			Arbitrary<Integer> ints = Arbitraries.integers();
			return ints.map(it -> it).withoutEdgeCases().filter(it -> true);
		}
	}

	@Group
	@PropertyDefaults(edgeCases = EdgeCasesMode.MIXIN, tries = 5000)
	@StatisticsReport(StatisticsReport.StatisticsReportMode.OFF)
	class GeneratorWithEmbeddedEdgeCases {

		@Example
		void generatorWithEdgeCases(@ForAll Random random) {
			Arbitrary<Integer> arbitrary = Arbitraries.integers().between(-1000, 1000);
			RandomGenerator<Integer> generator = arbitrary.generator(10, true);

			checkAtLeastOneGenerated(generator, random, i -> i == -1000);
			checkAtLeastOneGenerated(generator, random, i -> i == -1);
			checkAtLeastOneGenerated(generator, random, i -> i == 0);
			checkAtLeastOneGenerated(generator, random, i -> i == 1);
			checkAtLeastOneGenerated(generator, random, i -> i == 1000);
			checkAllGenerated(generator, random, i -> i >= -1000 && i <= 1000);
		}

		@Property
		void listGeneratorWithEmbeddedEdgeCases(@ForAll List<Integer> aList) {
			Statistics.label("list contains Integer.MAX_VALUE")
					  .collect(aList.stream().anyMatch(e -> e == Integer.MAX_VALUE))
					  .coverage(checker -> checker.check(true).percentage(p -> p > 5));
		}

		@Property
		void mappedGeneratorWithEmbeddedEdgeCases(@ForAll("number") String number) {
			Statistics.label("number is 0")
					  .collect(number.equals("0"))
					  .coverage(checker -> checker.check(true).percentage(p -> p > 0.5));
		}

		@Provide
		Arbitrary<String> number() {
			return Arbitraries.integers().map(i -> Integer.toString(i));
		}

		@Property
		void combinedGeneratorWithEmbeddedEdgeCases(@ForAll("tuple") Tuple2<Integer, Integer> aTuple) {
			Statistics.label("tuple contains 1000")
					  .collect(aTuple.get1() == 1000 || aTuple.get2() == 1000)
					  .coverage(checker -> checker.check(true).percentage(p -> p > 1));
		}

		@Provide
		Arbitrary<Tuple2<Integer, Integer>> tuple() {
			IntegerArbitrary int1 = Arbitraries.integers().lessOrEqual(1000).withDistribution(RandomDistribution.uniform());
			return Combinators.combine(int1, int1).as(Tuple::of);
		}

		@Property
		void frequencyOfGeneratorWithEmbeddedEdgeCases(@ForAll("frequencies") List<Integer> aList) {
			Statistics.label("list contains Integer.MAX_VALUE")
					  .collect(aList.stream().anyMatch(e -> e == Integer.MAX_VALUE))
					  .coverage(checker -> checker.check(true).percentage(p -> p > 4));
		}

		@Provide
		Arbitrary<List<Integer>> frequencies() {
			Arbitrary<List<Integer>> int1 = Arbitraries.integers().list();
			Arbitrary<List<Integer>> int2 = Arbitraries.integers().list();
			return Arbitraries.frequencyOf(
				Tuple.of(1, int1),
				Tuple.of(5, int2)
			);
		}

		@Property
		void stringGeneratorWithEmbeddedEdgeCases(@ForAll String aString) {
			Statistics.label("string contains ' '")
					  .collect(aString.chars().anyMatch(e -> e == ' '))
					  .coverage(checker -> checker.check(true).percentage(p -> p > 0.5));
		}

		@Property
		void typeGeneratorWithEmbeddedEdgeCases(@ForAll @UseType(UseTypeMode.PUBLIC_CONSTRUCTORS) MyString myString) {
			Statistics.label("string contains ' '")
					  .collect(myString.string.chars().anyMatch(e -> e == ' '))
					  .coverage(checker -> checker.check(true).percentage(p -> p > 0.5));
		}

		@Property
		void lazyOfGeneratorWithEmbeddedEdgeCases(@ForAll("lazyString") String aString) {
			Statistics.label("string contains ' '")
					  .collect(aString.chars().anyMatch(e -> e == ' '))
					  .coverage(checker -> checker.check(true).percentage(p -> p > 0.5));
		}

		@Provide
		Arbitrary<String> lazyString() {
			return Arbitraries.lazy(Arbitraries::strings);
		}

	}

	static class MyString {
		String string;

		public MyString(String string) {
			this.string = string;
		}
	}

	private static int sum(List<Integer> list) {
		return list.stream().mapToInt(i -> i).sum();
	}


	@Group
	class Filtering {
		@Example
		void filterInteger(@ForAll Random random) {
			Arbitrary<Integer> arbitrary = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5);
			Arbitrary<Integer> filtered = arbitrary.filter(anInt -> anInt % 2 != 0);
			RandomGenerator<Integer> generator = filtered.generator(10, true);

			assertThat(generator.next(random).value()).isEqualTo(1);
			assertThat(generator.next(random).value()).isEqualTo(3);
			assertThat(generator.next(random).value()).isEqualTo(5);
			assertThat(generator.next(random).value()).isEqualTo(1);
		}

		@Example
		void failIfFilterWillDiscard10000ValuesInARow(@ForAll Random random) {
			Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3, 4, 5);
			Arbitrary<Integer> filtered = arbitrary.filter(anInt -> false);
			RandomGenerator<Integer> generator = filtered.generator(10, true);

			assertThatThrownBy(() -> generator.next(random).value()).isInstanceOf(JqwikException.class);
		}
	}

	@Group
	class IgnoreException {
		@Example
		void ignoreIllegalArgumentException(@ForAll Random random) {
			Arbitrary<Integer> arbitrary =
				new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5)
					.map(anInt -> {
						if (anInt % 2 == 0) {
							throw new IllegalArgumentException("No even numbers");
						}
						return anInt;
					});
			Arbitrary<Integer> filtered = arbitrary.ignoreException(IllegalArgumentException.class);
			RandomGenerator<Integer> generator = filtered.generator(10, true);

			assertThat(generator.next(random).value()).isEqualTo(1);
			assertThat(generator.next(random).value()).isEqualTo(3);
			assertThat(generator.next(random).value()).isEqualTo(5);
			assertThat(generator.next(random).value()).isEqualTo(1);
		}

		@Example
		void ignoreSubtypeOfException(@ForAll Random random) {
			Arbitrary<Integer> arbitrary =
				new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5)
					.map(anInt -> {
						if (anInt % 2 == 0) {
							throw new IllegalArgumentException("No even numbers");
						}
						return anInt;
					});
			Arbitrary<Integer> filtered = arbitrary.ignoreException(RuntimeException.class);
			RandomGenerator<Integer> generator = filtered.generator(10, true);

			assertThat(generator.next(random).value()).isEqualTo(1);
			assertThat(generator.next(random).value()).isEqualTo(3);
			assertThat(generator.next(random).value()).isEqualTo(5);
			assertThat(generator.next(random).value()).isEqualTo(1);
		}

		@Example
		void failIfFilterWillDiscard10000ValuesInARow(@ForAll Random random) {
			Arbitrary<Integer> arbitrary =
				new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5)
					.map(anInt -> {
						throw new IllegalArgumentException("No even numbers");
					});
			Arbitrary<Integer> filtered = arbitrary.ignoreException(RuntimeException.class);
			RandomGenerator<Integer> generator = filtered.generator(10, true);

			assertThatThrownBy(() -> generator.next(random).value()).isInstanceOf(JqwikException.class);
		}
	}

	@Group
	class StreamOfAllValues {

		@Example
		void generateAllValues() {
			Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3, 4, 5);
			assertThat(arbitrary.allValues()).isPresent();
			assertThat(arbitrary.allValues().get()).containsExactlyInAnyOrder(1, 2, 3, 4, 5);
		}

		@Example
		void notPossibleWithoutExhaustiveGenerator() {
			Arbitrary<String> arbitrary = Arbitraries.strings();
			assertThat(arbitrary.allValues()).isEmpty();
		}
	}

	@Group
	class ForEachValue {

		@Example
		void iterateThroughEachValue() {
			Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3, 4, 5);
			AtomicInteger count = new AtomicInteger(0);
			arbitrary.forEachValue(i -> {
				count.incrementAndGet();
				assertThat(i).isIn(1, 2, 3, 4, 5);
			});
			assertThat(count.get()).isEqualTo(5);
		}

		@Example
		void notPossibleWithoutExhaustiveGenerator() {
			Arbitrary<String> arbitrary = Arbitraries.strings();
			Assertions.assertThatThrownBy(() -> arbitrary.forEachValue(i -> {}))
					  .isInstanceOf(AssertionError.class);
		}
	}

	@Group
	class Mapping {

		@Example
		void mapIntegerToString(@ForAll Random random) {
			Arbitrary<Integer> arbitrary = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5);
			Arbitrary<String> mapped = arbitrary.map(anInt -> "value=" + anInt);
			RandomGenerator<String> generator = mapped.generator(10, true);

			assertThat(generator.next(random).value()).isEqualTo("value=1");
			assertThat(generator.next(random).value()).isEqualTo("value=2");
			assertThat(generator.next(random).value()).isEqualTo("value=3");
			assertThat(generator.next(random).value()).isEqualTo("value=4");
			assertThat(generator.next(random).value()).isEqualTo("value=5");
			assertThat(generator.next(random).value()).isEqualTo("value=1");
		}

		// To ensure optimization of just(value).map(..) works
		@Example
		void mapJust(@ForAll Random random) {
			Arbitrary<Integer> arbitrary = Arbitraries.just(5);
			Arbitrary<String> mapped = arbitrary.map(anInt -> "value=" + anInt);

			RandomGenerator<String> generator = mapped.generator(10, true);

			checkAllGenerated(generator, random, s -> s.equals("value=5"));
		}

	}

	@Group
	class FlatMapping {

		@Example
		void flatMapIntegerToString(@ForAll Random random) {
			Arbitrary<Integer> arbitrary = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5);
			Arbitrary<String> mapped = arbitrary.flatMap(anInt -> Arbitraries.strings() //
																			 .withCharRange('a', 'e') //
																			 .ofMinLength(anInt).ofMaxLength(anInt));

			RandomGenerator<String> generator = mapped.generator(10, true);

			assertThat(generator.next(random).value()).hasSize(1);
			assertThat(generator.next(random).value()).hasSize(2);
			assertThat(generator.next(random).value()).hasSize(3);
			assertThat(generator.next(random).value()).hasSize(4);
			assertThat(generator.next(random).value()).hasSize(5);

			checkAtLeastOneGenerated(generator, random, s -> s.startsWith("a"));
			checkAtLeastOneGenerated(generator, random, s -> s.startsWith("b"));
			checkAtLeastOneGenerated(generator, random, s -> s.startsWith("c"));
			checkAtLeastOneGenerated(generator, random, s -> s.startsWith("d"));
			checkAtLeastOneGenerated(generator, random, s -> s.startsWith("e"));
		}

		// To ensure optimization of just(value).flatMap(..) works
		@Example
		void flatMapJust(@ForAll Random random) {
			Arbitrary<Integer> arbitrary = Arbitraries.just(5);
			Arbitrary<String> mapped = arbitrary.flatMap(anInt -> Arbitraries.strings()
																			 .withCharRange('a', 'e')
																			 .ofMinLength(anInt).ofMaxLength(anInt));

			RandomGenerator<String> generator = mapped.generator(10, true);


			checkAllGenerated(generator, random, s -> s.length() == 5);

			checkAtLeastOneGenerated(generator, random, s -> s.startsWith("a"));
			checkAtLeastOneGenerated(generator, random, s -> s.startsWith("b"));
			checkAtLeastOneGenerated(generator, random, s -> s.startsWith("c"));
			checkAtLeastOneGenerated(generator, random, s -> s.startsWith("d"));
			checkAtLeastOneGenerated(generator, random, s -> s.startsWith("e"));
		}

	}

	@Group
	class Combination {

		@Example
		void generateCombination(@ForAll Random random) {
			Arbitrary<Integer> a1 = new OrderedArbitraryForTesting<>(1, 2, 3);
			Arbitrary<Integer> a2 = new OrderedArbitraryForTesting<>(4, 5, 6);
			Arbitrary<String> combined = Combinators.combine(a1, a2).as((i1, i2) -> i1 + ":" + i2);
			RandomGenerator<String> generator = combined.generator(10, true);

			assertThat(generator.next(random).value()).isEqualTo("1:4");
			assertThat(generator.next(random).value()).isEqualTo("2:5");
			assertThat(generator.next(random).value()).isEqualTo("3:6");
			assertThat(generator.next(random).value()).isEqualTo("1:4");
		}

	}

	@Group
	class Collect {

		@Example
		void collectList(@ForAll Random random) {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 3);
			Arbitrary<List<Integer>> collected = integers.collect(list -> sum(list) >= 10);
			RandomGenerator<List<Integer>> generator = collected.generator(10, true);

			assertAllGenerated(generator, random, value -> {
				assertThat(sum(value)).isBetween(10, 12);
				assertThat(value.size()).isBetween(4, 10);
			});
		}

		@Example
		void collectListWillThrowExceptionIfTooBig(@ForAll Random random) {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 3);
			Arbitrary<List<Integer>> collected = integers.collect(list -> sum(list) < 0);
			RandomGenerator<List<Integer>> generator = collected.generator(10, true);

			assertThatThrownBy(() -> generator.next(random))
				.isInstanceOf(JqwikException.class);
		}

	}

	@Group
	@PropertyDefaults(tries = 10)
	class Sampling {

		@Property
		void singleSample(@ForAll @Size(min = 1) List<@WithNull Integer> values) {
			Arbitrary<Integer> ints = Arbitraries.of(values);

			Integer anInt = ints.sample();
			assertThat(anInt).isIn(values);
		}

		@Property
		void sampleStream(@ForAll @Size(min = 1) List<@WithNull Integer> values) {
			Arbitrary<Integer> ints = Arbitraries.of(values);

			ints.sampleStream()
				.limit(10)
				.forEach(anInt -> assertThat(anInt).isIn(values));
		}

	}

	@Group
	class Duplicates {

		@Property(tries = 100)
		void duplicatesWith20Percent(@ForAll Random random) {
			Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
			Arbitrary<Integer> intsWithDuplicates = ints.injectDuplicates(0.2);
			ListArbitrary<Integer> arbitrary = intsWithDuplicates.list().ofSize(100);

			RandomGenerator<List<Integer>> generator = arbitrary.generator(1000, false);
			List<Integer> listWithDuplicates = generator.next(random).value();
			Set<Integer> noMoreDuplicates = new HashSet<>(listWithDuplicates);

			Statistics
				.label("duplicates > 10%")
				.collect(noMoreDuplicates.size() <= 90)
				.coverage(checker -> checker.check(true).percentage(p -> p > 80));
		}

		@Property(tries = 100)
		void duplicatesWith50Percent(@ForAll Random random) {
			Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
			Arbitrary<Integer> intsWithDuplicates = ints.injectDuplicates(0.5);
			ListArbitrary<Integer> arbitrary = intsWithDuplicates.list().ofSize(100);

			RandomGenerator<List<Integer>> generator = arbitrary.generator(1000, false);
			List<Integer> listWithDuplicates = generator.next(random).value();
			Set<Integer> noMoreDuplicates = new HashSet<>(listWithDuplicates);

			Statistics
				.label("duplicates > 40%")
				.collect(noMoreDuplicates.size() <= 60)
				.coverage(checker -> checker.check(true).percentage(p -> p > 80));
		}

		@Property(tries = 100)
		void duplicatesWith100Percent(@ForAll Random random) {
			Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
			Arbitrary<Integer> intsWithDuplicates = ints.injectDuplicates(1.0);
			ListArbitrary<Integer> arbitrary = intsWithDuplicates.list().ofSize(100);

			RandomGenerator<List<Integer>> generator = arbitrary.generator(1000, false);
			List<Integer> listWithDuplicates = generator.next(random).value();
			Set<Integer> noMoreDuplicates = new HashSet<>(listWithDuplicates);
			assertThat(noMoreDuplicates).hasSize(1);
		}

		@Example
		void duplicatesWith100Percent() {
			Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
			Arbitrary<Integer> intsWithDuplicates = ints.injectDuplicates(1.0);

			List<Integer> listWithDuplicates = intsWithDuplicates.list().ofSize(50).sample();
			Set<Integer> noMoreDuplicates = new HashSet<>(listWithDuplicates);

			assertThat(noMoreDuplicates).hasSize(1);
		}

		@Property
		@StatisticsReport(StatisticsReport.StatisticsReportMode.OFF)
		void duplicatesAreNotPreservedAcrossTries(@ForAll("duplicateInts") int anInt) {
			Statistics.collect(anInt);

			Statistics.coverage(checker -> {
				for (int i = -1000; i <= 1000; i++) {
					int finalI = i;
					checker.check(i).percentage(
						(Consumer<Double>) p -> assertThat(p).describedAs("Value: %s", finalI).isLessThan(1)
					);
				}
			});
		}

		@Provide
		private Arbitrary<Integer> duplicateInts() {
			Arbitrary<Integer> ints = Arbitraries.integers()
												 .between(-1000, 1000).withDistribution(RandomDistribution.uniform())
												 .withoutEdgeCases();
			return ints.injectDuplicates(0.5);
		}
	}

	@Group
	@Label("tuple1..4")
	class TupleOfSameType {

		@Example
		void tuple1() {
			Arbitrary<Integer> integers = Arbitraries.just(1);
			Arbitrary<Tuple1<Integer>> tuple = integers.tuple1();

			assertThat(tuple.sample()).isEqualTo(Tuple.of(1));
		}

		@Example
		void tuple2() {
			Arbitrary<Integer> integers = Arbitraries.of(1, 2);
			Arbitrary<Tuple2<Integer, Integer>> tuple = integers.tuple2();

			Tuple2<Integer, Integer> sample = tuple.sample();
			assertThat(sample.v1).isIn(1, 2);
			assertThat(sample.v2).isIn(1, 2);
		}

		@Example
		void tuple3() {
			Arbitrary<Integer> integers = Arbitraries.of(1, 2);
			Arbitrary<Tuple3<Integer, Integer, Integer>> tuple = integers.tuple3();

			Tuple3<Integer, Integer, Integer> sample = tuple.sample();
			assertThat(sample.v1).isIn(1, 2);
			assertThat(sample.v2).isIn(1, 2);
			assertThat(sample.v3).isIn(1, 2);
		}

		@Example
		void tuple4() {
			Arbitrary<Integer> integers = Arbitraries.of(1, 2);
			Arbitrary<Tuple4<Integer, Integer, Integer, Integer>> tuple = integers.tuple4();

			Tuple4<Integer, Integer, Integer, Integer> sample = tuple.sample();
			assertThat(sample.v1).isIn(1, 2);
			assertThat(sample.v2).isIn(1, 2);
			assertThat(sample.v3).isIn(1, 2);
			assertThat(sample.v4).isIn(1, 2);
		}

		@Example
		void tuple5() {
			Arbitrary<Integer> integers = Arbitraries.of(1, 2);
			Arbitrary<Tuple5<Integer, Integer, Integer, Integer, Integer>> tuple = integers.tuple5();

			Tuple5<Integer, Integer, Integer, Integer, Integer> sample = tuple.sample();
			assertThat(sample.v1).isIn(1, 2);
			assertThat(sample.v2).isIn(1, 2);
			assertThat(sample.v3).isIn(1, 2);
			assertThat(sample.v4).isIn(1, 2);
			assertThat(sample.v5).isIn(1, 2);
		}

	}

}
