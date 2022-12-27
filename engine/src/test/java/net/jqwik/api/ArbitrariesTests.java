package net.jqwik.api;

import java.lang.reflect.*;
import java.math.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;
import net.jqwik.engine.support.types.*;
import net.jqwik.testing.*;

import static java.math.BigInteger.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Label("Arbitraries")
class ArbitrariesTests {

	enum MyEnum {
		Yes,
		No,
		Maybe
	}

	@Example
	void randomValues(@ForAll JqwikRandom random) {
		Arbitrary<String> stringArbitrary = Arbitraries.randomValue(r -> Integer.toString(r.nextInt(10)));
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		checkAllGenerated(generator, random, value -> Integer.parseInt(value) < 10);
		assertAtLeastOneGeneratedOf(generator, random, "1", "2", "3", "4", "5", "6", "7", "8", "9");
	}

	@Example
	void fromGenerator(@ForAll JqwikRandom random) {
		Arbitrary<String> stringArbitrary =
			Arbitraries.fromGenerator(r -> Shrinkable.unshrinkable(Integer.toString(r.nextInt(10))));
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		checkAllGenerated(generator, random, value -> Integer.parseInt(value) < 10);
	}

	@Example
	void ofValues(@ForAll JqwikRandom random) {
		Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		checkAllGenerated(generator, random, (String value) -> Arrays.asList("1", "hallo", "test").contains(value));
		assertAtLeastOneGeneratedOf(generator, random, "1", "hallo", "test");
	}

	@Example
	void ofValueList(@ForAll JqwikRandom random) {
		List<String> valueList = Arrays.asList("1", "hallo", "test");
		Arbitrary<String> stringArbitrary = Arbitraries.of(valueList);
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		checkAllGenerated(generator, random, (String value) -> Arrays.asList("1", "hallo", "test").contains(value));
		assertAtLeastOneGeneratedOf(generator, random, "1", "hallo", "test");
	}

	@Example
	void ofNonNullableValueList(@ForAll JqwikRandom random) {
		// TODO: Replace with List.of("a", "b") when moving to JDK >= 11
		List<String> valueList = new ArrayList<String>() {
			@Override
			public boolean contains(final Object o) {
				if (o == null) {
					throw new NullPointerException();
				}
				return super.contains(o);
			}
		};
		valueList.add("a");
		valueList.add("b");

		Arbitrary<String> stringArbitrary = Arbitraries.of(valueList);
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		checkAllGenerated(generator, random, (String value) -> Arrays.asList("a", "b").contains(value));
	}

	@Example
	void ofValueSet(@ForAll JqwikRandom random) {
		Set<String> valueSet = new HashSet<>(Arrays.asList("1", "hallo", "test"));
		Arbitrary<String> stringArbitrary = Arbitraries.of(valueSet);
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		checkAllGenerated(generator, random, (String value) -> Arrays.asList("1", "hallo", "test").contains(value));
		assertAtLeastOneGeneratedOf(generator, random, "1", "hallo", "test");
	}

	@Example
	void ofSuppliers(@ForAll JqwikRandom random) {
		Arbitrary<List<String>> listArbitrary = Arbitraries.ofSuppliers(ArrayList::new, ArrayList::new);
		RandomGenerator<List<String>> generator = listArbitrary.generator(1);
		assertAllGenerated(generator, random, (List<String> value) -> {
			assertThat(value).isEmpty();
			value.add("aString");
		});
	}

	@Example
	void ofSupplierList(@ForAll JqwikRandom random) {
		@SuppressWarnings("unchecked")
		Supplier<List<String>>[] suppliers = new Supplier[]{ArrayList::new, ArrayList::new};
		List<Supplier<List<String>>> supplierList = Arrays.asList(suppliers);
		Arbitrary<List<String>> listArbitrary = Arbitraries.ofSuppliers(supplierList);
		RandomGenerator<List<String>> generator = listArbitrary.generator(1);
		assertAllGenerated(generator, random, (List<String> value) -> {
			assertThat(value).isEmpty();
			value.add("aString");
		});
	}

	@Example
	void ofSupplierSet(@ForAll JqwikRandom random) {
		@SuppressWarnings("unchecked")
		Supplier<List<String>>[] suppliers = new Supplier[]{ArrayList::new, ArrayList::new};
		Set<Supplier<List<String>>> supplierList = new HashSet<>(Arrays.asList(suppliers));
		Arbitrary<List<String>> listArbitrary = Arbitraries.ofSuppliers(supplierList);
		RandomGenerator<List<String>> generator = listArbitrary.generator(1);
		assertAllGenerated(generator, random, (List<String> value) -> {
			assertThat(value).isEmpty();
			value.add("aString");
		});
	}

	@Example
	void ofEnum(@ForAll JqwikRandom random) {
		Arbitrary<MyEnum> enumArbitrary = Arbitraries.of(MyEnum.class);
		RandomGenerator<MyEnum> generator = enumArbitrary.generator(1);
		checkAllGenerated(generator, random, (MyEnum value) -> Arrays.asList(MyEnum.values()).contains(value));
		assertAtLeastOneGeneratedOf(generator, random, MyEnum.values());
	}

	@Example
	void just(@ForAll JqwikRandom random) {
		Arbitrary<String> constant = Arbitraries.just("hello");
		assertAllGenerated(constant.generator(1000), random, value -> {
			assertThat(value).isEqualTo("hello");
		});
	}

	@Example
	void forType(@ForAll JqwikRandom random) {
		TypeArbitrary<Person> constant = Arbitraries.forType(Person.class);
		assertAllGenerated(constant.generator(1000), random, value -> {
			assertThat(value).isInstanceOf(Person.class);
		});
	}

	@Group
	class Randoms {
		@Example
		void randoms(@ForAll JqwikRandom random) {
			Arbitrary<JqwikRandom> randomArbitrary = Arbitraries.randoms();
			RandomGenerator<JqwikRandom> generator = randomArbitrary.generator(1);
			checkAllGenerated(generator, random, (JqwikRandom value) -> value.nextInt(100) < 100);
		}

		// GenericGenerationProperties cannot be used due to different equality semantics of Random

		@Property(tries = 100)
		void sameRandomWillGenerateSameValueOnFreshGenerator(
			@ForAll JqwikRandom random,
			@ForAll @IntRange(min = 1, max = 10000) int genSize,
			@ForAll boolean withEdgeCases
		) {
			long seed = random.nextLong();
			Arbitrary<JqwikRandom> arbitrary = Arbitraries.randoms();

			RandomGenerator<JqwikRandom> gen1 = arbitrary.generator(genSize, withEdgeCases);
			JqwikRandom valueA = gen1.next(SourceOfRandomness.newRandom(seed)).value();
			RandomGenerator<JqwikRandom> gen2 = arbitrary.generator(genSize, withEdgeCases);
			JqwikRandom valueB = gen2.next(SourceOfRandomness.newRandom(seed)).value();
			assertThat(valueA.nextLong()).isEqualTo(valueB.nextLong());
		}

		@Property(tries = 100)
		void memoizableArbitrariesWillMemoizeGenerators(
			@ForAll @IntRange(min = 1, max = 10000) int genSize,
			@ForAll boolean withEdgeCases
		) {
			Arbitrary<JqwikRandom> arbitrary1 = Arbitraries.randoms();
			Arbitrary<JqwikRandom> arbitrary2 = Arbitraries.randoms();

			RandomGenerator<JqwikRandom> gen1 = Memoize.memoizedGenerator(
				arbitrary1, genSize, withEdgeCases,
				() -> arbitrary1.generator(genSize, withEdgeCases)
			);
			RandomGenerator<JqwikRandom> gen2 = Memoize.memoizedGenerator(
				arbitrary2, genSize, withEdgeCases,
				() -> arbitrary2.generator(genSize, withEdgeCases)
			);
			assertThat(gen1).isSameAs(gen2);
		}

		@Property(tries = 100)
		void sameRandomWillGenerateSameValueOnMemoizedGenerator(
			@ForAll JqwikRandom randomToGenerateValue,
			@ForAll @IntRange(min = 1, max = 10000) int genSize,
			@ForAll boolean withEdgeCases
		) {
			Arbitrary<JqwikRandom> arbitrary1 = Arbitraries.randoms();
			Arbitrary<JqwikRandom> arbitrary2 = Arbitraries.randoms();

			long seedToGenerateValue = randomToGenerateValue.nextLong();

			RandomGenerator<JqwikRandom> gen1 = Memoize.memoizedGenerator(arbitrary1, genSize, withEdgeCases, () -> arbitrary1.generator(genSize, withEdgeCases));
			RandomGenerator<JqwikRandom> gen2 = Memoize.memoizedGenerator(arbitrary2, genSize, withEdgeCases, () -> arbitrary2.generator(genSize, withEdgeCases));

			JqwikRandom valueA = gen1.next(SourceOfRandomness.newRandom(seedToGenerateValue)).value();
			JqwikRandom valueB = gen2.next(SourceOfRandomness.newRandom(seedToGenerateValue)).value();
			assertThat(valueA.nextLong()).isEqualTo(valueB.nextLong());
		}

	}

	@Group
	class GenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.ofSuppliers(
				() -> Arbitraries.of(1, 2, 3),
				() -> Arbitraries.of('a', 'b', 'c'),
				() -> Arbitraries.of(MyEnum.class),
				() -> Arbitraries.frequency(
					Tuple.of(1, "one"),
					Tuple.of(2, "two"),
					Tuple.of(3, "three")
				),
				() -> Arbitraries.just("abc"),
				() -> Arbitraries.create(() -> "new string"),
				() -> Arbitraries.ofSuppliers(() -> 1, () -> 2, () -> 3),
				() -> Arbitraries.shuffle(1, 2, 3),
				() -> Arbitraries.oneOf(Arbitraries.integers(), Arbitraries.strings()),
				() -> Arbitraries.frequencyOf(
					Tuple.of(2, Arbitraries.integers()),
					Tuple.of(3, Arbitraries.strings())
				),
				() -> Arbitraries.entries(Arbitraries.integers(), Arbitraries.integers()),
				() -> Arbitraries.randomValue(random -> random.nextInt()),
				() -> Arbitraries.fromGenerator(random -> Shrinkable.unshrinkable(random.nextInt())),
				() -> Arbitraries.defaultFor(String.class)
			);
		}
	}

	private static class Person {
		private final String firstName;
		private final String lastName;

		public Person(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public static Person create(String firstName) {
			return new Person(firstName, "Stranger");
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Person person = (Person) o;
			return firstName.equals(person.firstName) &&
					   lastName.equals(person.lastName);
		}

		@Override
		public int hashCode() {
			return Objects.hash(firstName, lastName);
		}
	}

	@Example
	void create_regenerates_objects_on_each_call(@ForAll JqwikRandom random) {
		Arbitrary<AtomicInteger> constant = Arbitraries.create(() -> new AtomicInteger(42));
		AtomicInteger[] previous = new AtomicInteger[]{new AtomicInteger(42)};
		assertAllGenerated(constant.generator(1000, true), random, value -> {
			assertThat(value.get()).isEqualTo(42);
			// Value is generated freshly
			assertThat(value).isNotSameAs(previous[0]);
			previous[0] = value;
		});
	}

	@Group
	@Label("shuffle(..)")
	class Shuffle {
		@Example
		void varArgsValues(@ForAll JqwikRandom random) {
			Arbitrary<List<Integer>> shuffled = Arbitraries.shuffle(1, 2, 3);
			assertPermutations(shuffled, random);
		}

		@Example
		void noValues(@ForAll JqwikRandom random) {
			Arbitrary<List<Integer>> shuffled = Arbitraries.shuffle();
			assertAllGenerated(
				shuffled.generator(1000),
				random,
				list -> {assertThat(list).isEmpty();}
			);
		}

		@Example
		void listOfValues(@ForAll JqwikRandom random) {
			Arbitrary<List<Integer>> shuffled = Arbitraries.shuffle(Arrays.asList(1, 2, 3));
			assertPermutations(shuffled, random);
		}

		private void assertPermutations(Arbitrary<List<Integer>> shuffled, JqwikRandom random) {
			assertAtLeastOneGeneratedOf(
				shuffled.generator(1000),
				random,
				Arrays.asList(1, 2, 3),
				Arrays.asList(1, 3, 2),
				Arrays.asList(2, 3, 1),
				Arrays.asList(2, 1, 3),
				Arrays.asList(3, 1, 2),
				Arrays.asList(3, 2, 1)
			);
		}
	}

	@Group
	@Label("oneOf(..)")
	class OneOf {

		@Example
		void choosesOneOfManyArbitraries(@ForAll JqwikRandom random) {
			Arbitrary<Integer> one = Arbitraries.of(1);
			Arbitrary<Integer> two = Arbitraries.of(2);
			Arbitrary<Integer> threeToFive = Arbitraries.of(3, 4, 5);

			Arbitrary<Integer> oneOfArbitrary = Arbitraries.oneOf(one, two, threeToFive);
			assertAllGenerated(oneOfArbitrary.generator(1000), random, value -> {
				assertThat(value).isIn(1, 2, 3, 4, 5);
			});

			RandomGenerator<Integer> generator = oneOfArbitrary.generator(1000);
			assertAtLeastOneGeneratedOf(generator, random, 1, 2, 3, 4, 5);
		}

		@Example
		void choosesOneOfDifferentCovariantTypes(@ForAll JqwikRandom random) {
			Arbitrary<Integer> ones = Arbitraries.of(1);
			Arbitrary<String> twos = Arbitraries.of("2");

			Arbitrary<?> anyOfArbitrary = Arbitraries.oneOf(ones, twos);

			RandomGenerator<?> generator = anyOfArbitrary.generator(1000);

			assertAllGenerated(generator, random, value -> {
				assertThat(value).isIn(1, "2");
			});

			assertAtLeastOneGeneratedOf(generator, random, 1, "2");
		}

		@Property
		void willHandDownConfigurations(@ForAll("stringLists") @Size(10) Collection<?> stringList) {
			assertThat(stringList).hasSize(10);
			assertThat(stringList).allMatch(element -> element instanceof String);
		}

		@Provide
		Arbitrary<List<String>> stringLists() {
			return Arbitraries.oneOf(
				Arbitraries.strings().ofLength(2).list(),
				Arbitraries.strings().ofLength(3).list()
			);
		}
	}

	@Group
	@Label("frequencyOf(..)")
	class FrequencyOf {

		@Example
		void choosesOneOfManyAccordingToFrequency(@ForAll JqwikRandom random) {
			Arbitrary<Integer> one = Arbitraries.of(1);
			Arbitrary<Integer> two = Arbitraries.of(2);

			Arbitrary<Integer> frequencyOfArbitrary = Arbitraries.frequencyOf(Tuple.of(10, one), Tuple.of(1, two));
			assertAllGenerated(frequencyOfArbitrary.generator(1000), random, value -> {
				assertThat(value).isIn(1, 2);
			});

			RandomGenerator<Integer> generator = frequencyOfArbitrary.generator(1000);
			assertAtLeastOneGeneratedOf(generator, random, 1, 2);

			List<Integer> elements = generator.stream(random).map(Shrinkable::value).limit(100).collect(Collectors.toList());
			int countOnes = Collections.frequency(elements, 1);
			int countTwos = Collections.frequency(elements, 2);

			assertThat(countOnes).isGreaterThan(countTwos * 2);
		}

		@Property
		void willHandDownConfigurations(@ForAll("stringLists") @Size(10) Collection<?> stringList) {
			assertThat(stringList).hasSize(10);
			assertThat(stringList).allMatch(element -> element instanceof String);
		}

		@Provide
		Arbitrary<List<String>> stringLists() {
			return Arbitraries.frequencyOf(
				Tuple.of(1, Arbitraries.strings().ofLength(2).list()),
				Tuple.of(2, Arbitraries.strings().ofLength(3).list())
			);
		}
	}

	@Group
	class Lazy {

		@Example
		void lazy(@ForAll JqwikRandom random) {
			Arbitrary<Integer> samples = Arbitraries.lazy(() -> new OrderedArbitraryForTesting<>(1, 2, 3));

			assertGeneratedExactly(samples.generator(1000), random, 1, 2, 3, 1);
			assertGeneratedExactly(samples.generator(1000), random, 1, 2, 3, 1);
		}

		@Example
		void recursiveLazy(@ForAll JqwikRandom random) {
			Arbitrary<Tree> trees = trees();
			checkAllGenerated(
				trees.generator(1000, true),
				random,
				tree -> {
					//System.out.println(tree);
					return tree != null;
				}
			);
		}

		private Arbitrary<Tree> trees() {
			return Combinators.combine(aName(), aBranch(), aBranch()).as(Tree::new);
		}

		private Arbitrary<String> aName() {
			return Arbitraries.strings().alpha().ofLength(3);
		}

		private Arbitrary<Tree> aBranch() {
			return Arbitraries.lazy(() -> Arbitraries.frequencyOf(
				Tuple.of(2, Arbitraries.just(null)),
				Tuple.of(1, trees())
			));
		}

	}

	@Group
	class LazyOf {

		@Property(tries = 100)
		void recursiveTree(@ForAll("trees") Tree tree) {
			assertThat(tree.name).hasSize(3);
			assertThat(tree.left).satisfiesAnyOf(
				branch -> assertThat(branch).isNull(),
				branch -> assertThat(branch).isInstanceOf(Tree.class)
			);
			assertThat(tree.right).satisfiesAnyOf(
				branch -> assertThat(branch).isNull(),
				branch -> assertThat(branch).isInstanceOf(Tree.class)
			);
		}

		@Provide
		private Arbitrary<Tree> trees() {
			return Combinators.combine(aName(), aBranch(), aBranch()).as(Tree::new);
		}

		private Arbitrary<String> aName() {
			return Arbitraries.strings().alpha().ofLength(3);
		}

		private Arbitrary<Tree> aBranch() {
			return Arbitraries.lazyOf(
				() -> Arbitraries.just(null),
				() -> Arbitraries.just(null),
				this::trees
			);
		}

		@Property(tries = 100)
		void recursiveListWithIDs(@ForAll("listWithIds") List<Integer> list) {
			assertThat(list).hasSizeGreaterThanOrEqualTo(0);
			assertThat(list).allMatch(i -> i >= 1 && i <= 20);
		}

		@Provide
		Arbitrary<List<Integer>> listWithIds() {
			Arbitrary<Integer> uniqueId = Arbitraries.integers().between(1, 20);
			return Arbitraries.lazyOf(
				() -> Arbitraries.just(new ArrayList<>()),
				() -> Combinators.combine(listWithIds(), uniqueId)
								 .as((l, id) -> {
									 ArrayList<Integer> list = new ArrayList<>(l);
									 list.add(id);
									 return list;
								 })
			);
		}

	}

	private static class Tree {
		final String name;
		final Tree left;
		final Tree right;

		private Tree(final String name, final Tree left, final Tree right) {
			this.name = name;
			this.left = left;
			this.right = right;
		}

		@Override
		public String toString() {
			return String.format("%s[%s]", name, depth());
		}

		private int depth() {
			if (left == null && right == null) {
				return 0;
			}
			return Math.max(
				left == null ? 0 : left.depth() + 1,
				right == null ? 0 : right.depth() + 1
			);
		}
	}

	@Group
	@Label("frequency(..)")
	class Frequency {

		@Example
		void onePair(@ForAll JqwikRandom random) {
			Arbitrary<String> one = Arbitraries.frequency(Tuple.of(1, "a"));
			checkAllGenerated(one.generator(1000), random, value -> {return value.equals("a");});
		}

		@Property(tries = 10)
		void twoEqualPairs(@ForAll JqwikRandom random) {
			Arbitrary<String> one = Arbitraries.frequency(Tuple.of(1, "a"), Tuple.of(1, "b"));
			Map<String, Long> counts = count(one.generator(1000, true), 1000, random);
			assertThat(counts.get("a") > 200).isTrue();
			assertThat(counts.get("b") > 200).isTrue();
		}

		@Property(tries = 10)
		void twoUnequalPairs(@ForAll JqwikRandom random) {
			Arbitrary<String> one = Arbitraries.frequency(Tuple.of(1, "a"), Tuple.of(10, "b"));
			Map<String, Long> counts = count(one.generator(1000, true), 1000, random);
			assertThat(counts.get("a")).isLessThan(counts.get("b"));
		}

		@Property(tries = 10)
		void fourUnequalPairs(@ForAll JqwikRandom random) {
			Arbitrary<String> one = Arbitraries.frequency(
				Tuple.of(1, "a"),
				Tuple.of(5, "b"),
				Tuple.of(10, "c"),
				Tuple.of(20, "d")
			);
			Map<String, Long> counts = count(one.generator(1000, true), 1000, random);
			assertThat(counts.get("a")).isLessThan(counts.get("b"));
			assertThat(counts.get("b")).isLessThan(counts.get("c"));
			assertThat(counts.get("c")).isLessThan(counts.get("d"));
		}

		@Example
		void noPositiveFrequencies() {
			assertThatThrownBy(() -> Arbitraries.frequency(Tuple.of(0, "a"))).isInstanceOf(JqwikException.class);
		}

	}

	@Group
	@Label("defaultFor(..)")
	class DefaultFor {
		@Example
		void simpleType(@ForAll JqwikRandom random) {
			Arbitrary<Integer> integerArbitrary = Arbitraries.defaultFor(Integer.class);
			checkAllGenerated(integerArbitrary.generator(1000), random, Objects::nonNull);
		}

		@SuppressWarnings("rawtypes")
		@Example
		void parameterizedType(@ForAll JqwikRandom random) {
			Arbitrary<List> list = Arbitraries.defaultFor(List.class, String.class);
			checkAllGenerated(list.generator(1000), random, List.class::isInstance);
		}

		@SuppressWarnings("rawtypes")
		@Example
		void moreThanOneDefault(@ForAll JqwikRandom random) {
			Arbitrary<Collection> collections = Arbitraries.defaultFor(Collection.class, String.class);
			TestingSupport.checkAtLeastOneGenerated(collections.generator(1000), random, List.class::isInstance);
			TestingSupport.checkAtLeastOneGenerated(collections.generator(1000), random, Set.class::isInstance);
		}

		@Example
		void defaultForWithTypeUsage(@ForAll JqwikRandom random) throws NoSuchMethodException {
			class Container {
				void method(@Size(3) List<Integer> intList) {}
			}

			Method method = Container.class.getDeclaredMethod("method", List.class);
			MethodParameter intListParameter = JqwikReflectionSupport.getMethodParameters(method, Container.class).get(0);
			TypeUsage parameterUsage = TypeUsageImpl.forParameter(intListParameter);

			Arbitrary<List<Integer>> arbitrary = Arbitraries.defaultFor(parameterUsage);

			TestingSupport.assertAllGenerated(
				arbitrary,
				random,
				list -> assertThat(list).hasSize(3)
			);
		}

		@Property(tries = 100)
		void defaultForParameterizedType(@ForAll("stringLists") @Size(10) List<String> stringList) {
			assertThat(stringList).hasSize(10);
			assertThat(stringList).allMatch(element -> element instanceof String);
		}

		@SuppressWarnings("rawtypes")
		@Provide
		Arbitrary<List> stringLists() {
			return Arbitraries.defaultFor(List.class, String.class);
		}

		@Property(tries = 100)
		@Domain(Int42.class)
		void defaultForWithDomain(@ForAll("intLists") List<Integer> intList) {
			assertThat(intList).allMatch(element -> element == 42);
		}

		@SuppressWarnings("rawtypes")
		@Provide
		Arbitrary<List> intLists() {
			return Arbitraries.defaultFor(List.class, Integer.class);
		}

		class Int42 extends DomainContextBase {
			@Provide
			Arbitrary<Integer> fortyTwo() {
				return Arbitraries.just(42);
			}
		}
	}

	@Group
	@Label("chars()")
	class Chars {
		@Example
		void charsDefault(@ForAll JqwikRandom random) {
			Arbitrary<Character> arbitrary = Arbitraries.chars();
			RandomGenerator<Character> generator = arbitrary.generator(1);
			checkAllGenerated(generator, random, Objects::nonNull);
		}

		@Example
		void chars(@ForAll JqwikRandom random) {
			Arbitrary<Character> arbitrary = Arbitraries.chars().range('a', 'd');
			RandomGenerator<Character> generator = arbitrary.generator(1);
			List<Character> allowedChars = Arrays.asList('a', 'b', 'c', 'd');
			checkAllGenerated(generator, random, (Character value) -> allowedChars.contains(value));
		}
	}

	// TODO: Extract into StringArbitraryTests
	@Group
	@Label("strings()")
	class Strings {
		@Example
		void string(@ForAll JqwikRandom random) {
			Arbitrary<String> stringArbitrary = Arbitraries.strings()
														   .withCharRange('a', 'd')
														   .ofMinLength(0).ofMaxLength(5);
			RandomGenerator<String> generator = stringArbitrary.generator(1);
			assertGeneratedString(generator, random, 0, 5);
		}

		@Property(tries = 20)
		void stringWithFixedLength(@ForAll @IntRange(min = 1, max = 10) int size, @ForAll JqwikRandom random) {
			Arbitrary<String> stringArbitrary = Arbitraries.strings()
														   .withCharRange('a', 'a')
														   .ofMinLength(size).ofMaxLength(size);
			RandomGenerator<String> generator = stringArbitrary.generator(1);
			checkAllGenerated(generator, random, value -> value.length() == size);
			checkAllGenerated(generator, random, (String value) -> value.chars().allMatch(i -> i == 'a'));
		}

		@Example
		void stringFromCharset(@ForAll JqwikRandom random) {
			char[] validChars = new char[]{'a', 'b', 'c', 'd'};
			Arbitrary<String> stringArbitrary = Arbitraries.strings()
														   .withChars(validChars)
														   .ofMinLength(2).ofMaxLength(5);
			RandomGenerator<String> generator = stringArbitrary.generator(1);
			assertGeneratedString(generator, random, 2, 5);
		}
	}

	@Group
	@Label("Integrals")
	class IntegralNumbers {

		@Group
		class IntegralGenerationTests implements GenericGenerationProperties {
			@Override
			public Arbitrary<Arbitrary<?>> arbitraries() {
				return Arbitraries.of(
					Arbitraries.integers(),
					Arbitraries.shorts(),
					Arbitraries.bytes(),
					Arbitraries.longs(),
					Arbitraries.bigIntegers()
				);
			}
		}

		@Example
		void shorts(@ForAll JqwikRandom random) {
			Arbitrary<Short> enumArbitrary = Arbitraries.shorts();
			RandomGenerator<Short> generator = enumArbitrary.generator(100);
			checkAllGenerated(generator, random, (Short value) -> value >= Short.MIN_VALUE && value <= Short.MAX_VALUE);
		}

		@Example
		void shortsMinsAndMaxes(@ForAll JqwikRandom random) {
			Arbitrary<Short> enumArbitrary = Arbitraries.shorts().between((short) -10, (short) 10);
			RandomGenerator<Short> generator = enumArbitrary.generator(100);

			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value < 0 && value > -5);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value > 0 && value < 5);
			checkAllGenerated(generator, random, value -> value >= -10 && value <= 10);
		}

		@Example
		void bytes(@ForAll JqwikRandom random) {
			Arbitrary<Byte> enumArbitrary = Arbitraries.bytes();
			RandomGenerator<Byte> generator = enumArbitrary.generator(1);
			checkAllGenerated(generator, random, (Byte value) -> value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE);
		}

		@Example
		void bytesMinsAndMaxes(@ForAll JqwikRandom random) {
			Arbitrary<Byte> enumArbitrary = Arbitraries.bytes().between((byte) -10, (byte) 10);
			RandomGenerator<Byte> generator = enumArbitrary.generator(1);

			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value < 0 && value > -5);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value > 0 && value < 5);
			checkAllGenerated(generator, random, value -> value >= -10 && value <= 10);
		}

		@Example
		void integerMinsAndMaxesWithEdgeCases(@ForAll JqwikRandom random) {
			RandomGenerator<Integer> generator = Arbitraries.integers().generator(1, true);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value == Integer.MIN_VALUE);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value == Integer.MAX_VALUE);
		}

		@Example
		void integersInt(@ForAll JqwikRandom random) {
			Arbitrary<Integer> intArbitrary = Arbitraries.integers().between(-10, 10);
			RandomGenerator<Integer> generator = intArbitrary.generator(10);

			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value < 0 && value > -5);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value > 0 && value < 5);
			checkAllGenerated(generator, random, value -> value >= -10 && value <= 10);
		}

		@Example
		void longMinsAndMaxesWithEdgeCases(@ForAll JqwikRandom random) {
			RandomGenerator<Long> generator = Arbitraries.longs().generator(1, true);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value == Long.MIN_VALUE);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value == Long.MAX_VALUE);
		}

		@Example
		void integersLong(@ForAll JqwikRandom random) {
			Arbitrary<Long> longArbitrary = Arbitraries.longs().between(-100L, 100L);
			RandomGenerator<Long> generator = longArbitrary.generator(1000);

			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value < -50);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value > 50);
			checkAllGenerated(generator, random, value -> value >= -100L && value <= 100L);
		}

		@Example
		void bigIntegers(@ForAll JqwikRandom random) {
			Arbitrary<BigInteger> bigIntegerArbitrary = Arbitraries.bigIntegers().between(valueOf(-100L), valueOf(100L));
			RandomGenerator<BigInteger> generator = bigIntegerArbitrary.generator(1);

			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.compareTo(valueOf(-50L)) < 0);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.compareTo(valueOf(50L)) > 0);
			checkAllGenerated(
				generator,
				random,
				value -> value.compareTo(valueOf(-100L)) >= 0
							 && value.compareTo(valueOf(100L)) <= 0
			);
		}

		@Property(tries = 10)
		void bigIntegersWithUniformDistribution(@ForAll JqwikRandom random) {
			Arbitrary<BigInteger> bigIntegerArbitrary =
				Arbitraries.bigIntegers()
						   .between(valueOf(-1000L), valueOf(1000L))
						   .withDistribution(RandomDistribution.uniform());
			RandomGenerator<BigInteger> generator = bigIntegerArbitrary.generator(1);

			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.longValue() > -1000 && value.longValue() < -980);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.longValue() < 1000 && value.longValue() > 980);
			TestingSupport.checkAllGenerated(
				generator,
				random,
				value -> value.compareTo(valueOf(-1000L)) >= 0 && value.compareTo(valueOf(1000L)) <= 0
			);
		}

		@Example
		void integralEdgeCasesAreGenerated(@ForAll JqwikRandom random) {
			BigInteger min = valueOf(Integer.MIN_VALUE);
			BigInteger max = valueOf(Integer.MAX_VALUE);
			BigInteger shrinkingTarget = valueOf(101);
			Arbitrary<BigInteger> bigIntegerArbitrary = Arbitraries.bigIntegers().between(min, max).shrinkTowards(shrinkingTarget);
			RandomGenerator<BigInteger> generator = bigIntegerArbitrary.generator(1000, true);
			assertAtLeastOneGeneratedOf(
				generator,
				random,
				shrinkingTarget,
				valueOf(-2), valueOf(-1),
				valueOf(0),
				valueOf(1), valueOf(2),
				min, max
			);
		}
	}

	@Group
	class DecimalsGenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.of(
				Arbitraries.bigDecimals(),
				Arbitraries.floats(),
				Arbitraries.doubles()
			);
		}
	}

	@Group
	@Label("bigDecimals()")
	class BigDecimals {
		@Example
		void bigDecimalsWithEdgeCases(@ForAll JqwikRandom random) {
			Arbitrary<BigDecimal> arbitrary = Arbitraries.bigDecimals()
														 .between(BigDecimal.valueOf(-100.0), BigDecimal.valueOf(100.0))
														 .ofScale(2)
														 .shrinkTowards(BigDecimal.valueOf(4.2));
			RandomGenerator<BigDecimal> generator = arbitrary.generator(1, true);

			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.compareTo(BigDecimal.valueOf(4.2)) == 0);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.compareTo(BigDecimal.valueOf(-100.0)) == 0);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.compareTo(BigDecimal.valueOf(100.0)) == 0);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.compareTo(BigDecimal.ZERO) == 0);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.compareTo(BigDecimal.ONE) == 0);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.compareTo(BigDecimal.ONE.negate()) == 0);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.doubleValue() < -1.0 && value.doubleValue() > -9.0);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.doubleValue() > 1.0 && value.doubleValue() < 9.0);
			checkAllGenerated(generator, random, value -> value.scale() <= 2);
		}

		@Example
		void bigDecimalsLessOrEqual(@ForAll JqwikRandom random) {
			BigDecimal max = BigDecimal.valueOf(10);
			Arbitrary<BigDecimal> arbitrary = Arbitraries.bigDecimals().lessOrEqual(max);
			RandomGenerator<BigDecimal> generator = arbitrary.generator(1);
			checkAllGenerated(generator, random, value -> value.compareTo(max) <= 0);
		}

		@Example
		void bigDecimalsLessThan(@ForAll JqwikRandom random) {
			BigDecimal max = BigDecimal.valueOf(10);
			Arbitrary<BigDecimal> arbitrary = Arbitraries.bigDecimals().lessThan(max).ofScale(1);
			RandomGenerator<BigDecimal> generator = arbitrary.generator(1);
			checkAllGenerated(generator, random, value -> value.compareTo(max) < 0);
		}

		@Example
		void bigDecimalsGreaterOrEqual(@ForAll JqwikRandom random) {
			BigDecimal min = BigDecimal.valueOf(10);
			Arbitrary<BigDecimal> arbitrary = Arbitraries.bigDecimals().greaterOrEqual(min);
			RandomGenerator<BigDecimal> generator = arbitrary.generator(1);
			checkAllGenerated(generator, random, value -> value.compareTo(min) >= 0);
		}

		@Example
		void bigDecimalsGreaterThan(@ForAll JqwikRandom random) {
			BigDecimal min = BigDecimal.valueOf(10);
			Arbitrary<BigDecimal> arbitrary = Arbitraries.bigDecimals().greaterThan(min).ofScale(1);
			RandomGenerator<BigDecimal> generator = arbitrary.generator(1);
			checkAllGenerated(generator, random, value -> value.compareTo(min) > 0);
		}

		@Example
		void bigDecimalsWithShrinkingTargetOutsideBorders() {
			Arbitrary<BigDecimal> arbitrary = Arbitraries.bigDecimals()
														 .between(BigDecimal.ONE, BigDecimal.TEN)
														 .shrinkTowards(BigDecimal.valueOf(-1));
			assertThatThrownBy(() -> arbitrary.generator(1)).isInstanceOf(JqwikException.class);
		}

		@Example
		void bigDecimalsWithBordersExcludedAndEdgeCases(@ForAll JqwikRandom random) {
			Range<BigDecimal> range = Range.of(BigDecimal.valueOf(-10.0), false, BigDecimal.valueOf(10.0), false);
			Arbitrary<BigDecimal> arbitrary = Arbitraries.bigDecimals()
														 .between(range.min, range.minIncluded, range.max, range.maxIncluded)
														 .ofScale(1);
			RandomGenerator<BigDecimal> generator = arbitrary.generator(1000, true);

			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.compareTo(BigDecimal.ZERO) == 0);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.compareTo(BigDecimal.ONE) == 0);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.compareTo(BigDecimal.valueOf(-1)) == 0);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.compareTo(BigDecimal.ONE.negate()) == 0);
			checkAllGenerated(generator, random, range::includes);
		}

		@Property(tries = 10)
		void bigDecimalsWithUniformDistribution(@ForAll JqwikRandom random) {
			Range<BigDecimal> range = Range.of(BigDecimal.valueOf(-1000.0), BigDecimal.valueOf(1000.0));
			Arbitrary<BigDecimal> arbitrary = Arbitraries.bigDecimals()
														 .between(range.min, range.max)
														 .ofScale(0)
														 .withDistribution(RandomDistribution.uniform());
			RandomGenerator<BigDecimal> generator = arbitrary.generator(1);

			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.longValue() > -1000 && value.longValue() < -980);
			TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value.longValue() < 1000 && value.longValue() > 980);
			TestingSupport.checkAllGenerated(
				generator,
				random,
				value -> value.compareTo(BigDecimal.valueOf(-1000L)) >= 0
							 && value.compareTo(BigDecimal.valueOf(1000L)) <= 0
			);
		}

	}

	@Group
	class GenericTypes {

		@Example
		void optional(@ForAll JqwikRandom random) {
			Arbitrary<String> stringArbitrary = Arbitraries.of("one", "two");
			Arbitrary<Optional<String>> optionalArbitrary = stringArbitrary.optional();

			RandomGenerator<Optional<String>> generator = optionalArbitrary.generator(1);

			TestingSupport.checkAtLeastOneGenerated(generator, random, optional -> optional.orElse("").equals("one"));
			TestingSupport.checkAtLeastOneGenerated(generator, random, optional -> optional.orElse("").equals("two"));
			TestingSupport.checkAtLeastOneGenerated(generator, random, optional -> !optional.isPresent());
		}

		@Example
		void optionalWithProbability(@ForAll JqwikRandom random) {
			Arbitrary<String> stringArbitrary = Arbitraries.of("one", "two");
			Arbitrary<Optional<String>> optionalArbitrary = stringArbitrary.optional(0.9);

			RandomGenerator<Optional<String>> generator = optionalArbitrary.generator(1);

			TestingSupport.checkAtLeastOneGenerated(generator, random, optional -> optional.orElse("").equals("one"));
			TestingSupport.checkAtLeastOneGenerated(generator, random, optional -> optional.orElse("").equals("two"));
			TestingSupport.checkAtLeastOneGenerated(generator, random, optional -> !optional.isPresent());
		}

		@Example
		void entry(@ForAll JqwikRandom random) {
			Arbitrary<Integer> keys = Arbitraries.integers().between(1, 10);
			Arbitrary<String> values = Arbitraries.strings().alpha().ofLength(5);

			Arbitrary<Map.Entry<Integer, String>> entryArbitrary = Arbitraries.entries(keys, values);

			RandomGenerator<Map.Entry<Integer, String>> generator = entryArbitrary.generator(1);

			assertAllGenerated(generator, random, entry -> {
				assertThat((int) entry.getKey()).isBetween(1, 10);
				assertThat(entry.getValue()).hasSize(5);
			});

			// Generated entries are mutable
			assertAllGenerated(generator, random, entry -> {
				entry.setValue("fortytwo");
				assertThat(entry.getValue()).isEqualTo("fortytwo");
			});
		}

	}

	@Group
	class SubsetOf {
		@Example
		void subsetOf(@ForAll JqwikRandom random) {
			SetArbitrary<String> subsets = Arbitraries.subsetOf("One", "Two", "Three").ofMinSize(1);
			assertAllGenerated(subsets, random, value -> {
				assertThat(value).isInstanceOf(Set.class);
				assertThat(value).hasSizeBetween(1, 3);
			});
		}

		@Example
		void subsetOfListOfValues(@ForAll JqwikRandom random) {
			List<String> values = Arrays.asList("One", "Two", "Three", "One");
			SetArbitrary<String> subsets = Arbitraries.subsetOf(values).ofMinSize(1);
			assertAllGenerated(subsets, random, value -> {
				assertThat(value).isInstanceOf(Set.class);
				assertThat(value).hasSizeBetween(1, 3);
			});
		}
	}

	private void assertGeneratedString(RandomGenerator<String> generator, JqwikRandom random, int minLength, int maxLength) {
		checkAllGenerated(generator, random, value -> value.length() >= minLength && value.length() <= maxLength);
		List<Character> allowedChars = Arrays.asList('a', 'b', 'c', 'd');
		checkAllGenerated(
			generator, random,
			(String value) -> value.chars().allMatch(i -> allowedChars.contains((char) i))
		);
	}
}