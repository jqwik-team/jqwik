Sometimes the possibilities of adjusting default parameter generation
through annotations is not enough. You want to control the creation
of values programmatically. The means to do that are _provider methods_.

### Arbitrary Provider Methods

Look at the
[following example](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/ProvideMethodExamples.java):

```java
@Property
boolean concatenatingStringWithInt(
    @ForAll("shortStrings") String aShortString,
    @ForAll("10 to 99") int aNumber
) {
    String concatenated = aShortString + aNumber;
    return concatenated.length() > 2 && concatenated.length() < 11;
}

@Provide
Arbitrary<String> shortStrings() {
    return Arbitraries.strings().withCharRange('a', 'z')
        .ofMinLength(1).ofMaxLength(8);
}

@Provide("10 to 99")
Arbitrary<Integer> numbers() {
    return Arbitraries.integers().between(10, 99);
}
```

The String value of the [`@ForAll`](/docs/${docsVersion}/javadoc/net/jqwik/api/ForAll.html)
annotation serves as a reference to a
method within the same class (or one of its superclasses or owning classes).
This reference refers to either the method's name or the String value
of the method's `@Provide` annotation.

The providing method has to return an object of type
[`@Arbitrary<T>`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html)
where `T` is the static type of the parameter to be provided.

Arbitrary provision usually starts with a
[static method call to `Arbitraries`](#static-arbitraries-methods), maybe followed
by one or more [filtering](#filtering), [mapping](#mapping) or
[combining](#combining-arbitraries) actions.

#### Provider Methods with Parameters

The examples of [provider methods](#parameter-provider-methods) you've seen so far
had no parameters. In more complicated scenarios, however, you may want to tune
an arbitrary depending on the concrete parameter to be generated.

The provider method can have a few optional parameters:

- a parameter of type `TypeUsage`
  that describes the details of the target parameter to be provided,
  like annotations and type variables.

- any parameter annotated with `@ForAll`: This parameter will be generated using
  the current context and then be used to create or configure the arbitrary to return.
  We call this [implicit flat mapping](#implicit-flat-mapping).

The following example uses a `TypeUsage` parameter to unify two provider methods.  
Imagine you want to randomly choose one of your favourite primes; that's easy:

```java
@Property
void favouritePrimes(@ForAll("favouritePrimes") int aFavourite) {
}

@Provide
Arbitrary<Integer> favouritePrimes() {
	return Arbitraries.of(3, 5, 7, 13, 17, 23, 41, 101);
}
```

From time to time, though, you need it as a `BigInteger` instead of an `int`. 
You can kill both types with a single method:

```java
@Property
void favouritePrimesAsInts(@ForAll("favouritePrimes") int aFavourite) { ... }

@Property
void favouritePrimesAsBigInts(@ForAll("favouritePrimes") BigInteger aFavourite) { ... }

@Provide
Arbitrary<?> favouritePrimes(TypeUsage targetType) {
	Arbitrary<Integer> ints = Arbitraries.of(3, 5, 7, 13, 17, 23, 41);
	if (targetType.getRawType().equals(BigInteger.class)) {
		return ints.map(BigInteger::valueOf);
	}
	return ints;
}
```

Mind that Java's type system now forces you to use a wildcard in the return type.


### Arbitrary Suppliers

Similar to [provider methods](#arbitrary-provider-methods) you can specify an
`ArbitrarySupplier` implementation in the `@ForAll` annotation:

```java
@Property
boolean concatenatingStringWithInt(
	@ForAll(supplier = ShortStrings.class) String aShortString,
	@ForAll(supplier = TenTo99.class) int aNumber
) {
	String concatenated = aShortString + aNumber;
	return concatenated.length() > 2 && concatenated.length() < 11;
}

class ShortStrings implements ArbitrarySupplier<String> {
	@Override
	public Arbitrary<String> get() {
		return Arbitraries.strings().withCharRange('a', 'z')
						  .ofMinLength(1).ofMaxLength(8);
	}
}

class TenTo99 implements ArbitrarySupplier<Integer> {
	@Override
	public Arbitrary<Integer> get() {
		return Arbitraries.integers().between(10, 99);
	}
}
```

Although this is a bit more verbose than using a provider method, it has two advantages:
- The IDE let's you directly navigate from the supplier attribute to the implementing class
- `ArbitrarySupplier` implementations can be shared across test container classes.


The [`ArbitrarySupplier`](/docs/${docsVersion}/javadoc/net/jqwik/api/ArbitrarySupplier.html) 
interface requires to override exactly one of two methods:

- `get()` as you have seen above
- `supplyFor(TypeUsage targeType)` if you need more information about the parameter,
  e.g. about annotations or type parameters. 

### Providing Arbitraries for Embedded Types

There is an alternative syntax to `@ForAll("methodRef")` using a `From` annotation:

```java
@Property
boolean concatenatingStringWithInt(
    @ForAll @From("shortStrings") String aShortString,
    @ForAll @From("10 to 99") int aNumber
) { ... }
```

Why this redundancy? Well, `@From` becomes a necessity when you want to provide
the arbitrary of an embedded type parameter. Consider this example:

```java
@Property
boolean joiningListOfStrings(@ForAll List<@From("shortStrings") String> listOfStrings) {
    String concatenated = String.join("", listOfStrings);
    return concatenated.length() <= 8 * listOfStrings.size();
}
```

Here, the list is created using the default list arbitrary, but the
String elements are generated using the arbitrary from the method `shortStrings`.

Alternatively, you can also use a `supplier` attribute in `@From`:

```java
@Property
boolean joiningListOfStrings(@ForAll List<@From(supplier=ShortStrings.class) String> listOfStrings) {
    String concatenated = String.join("", listOfStrings);
    return concatenated.length() <= 8 * listOfStrings.size();
}
```


### Static `Arbitraries` methods

The starting point for generation usually is a static method call on class
[`Arbitraries`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html).

#### Generate values yourself

- [`Arbitrary<T> randomValue(Function<Random, T> generator)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#randomValue(java.util.function.Function)):
  Take a `random` instance and create an object from it.
  Those values cannot be [shrunk](#result-shrinking), though.

  Generating prime numbers might look like that:
  ```java
  @Provide
  Arbitrary<Integer> primesGenerated() {
      return Arbitraries.randomValue(random -> generatePrime(random));
  }

  private Integer generatePrime(Random random) {
      int candidate;
      do {
          candidate = random.nextInt(10000) + 2;
      } while (!isPrime(candidate));
      return candidate;
  }
  ```

- [`Arbitrary<T> fromGenerator(RandomGenerator<T> generator)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#fromGenerator(net.jqwik.api.RandomGenerator)):
  If the number of _tries_ influences value generation or if you want
  to allow for [shrinking](#result-shrinking) you have to provide
  your own `RandomGenerator` implementation.

#### Select or generate values randomly

- [`Arbitrary<U> of(U... values)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#of(U...)):
  Choose randomly from a list of values. Shrink towards the first one.

- [`Arbitrary<U> ofSuppliers(Supplier<U>... valueSuppliers)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#ofSuppliers(java.util.function.Supplier...)):
  Choose randomly from a list of value suppliers and get the object from this supplier.
  This is useful when dealing with mutable objects where `Arbitrary.of(..)` would reuse a potentially changed object.

- [`Arbitrary<T> just(T constantValue)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#just(T)):
  Always provide the same constant value in each try. Mostly useful to combine with other arbitraries.

- [`Arbitrary<T> of(Class<T  extends Enum> enumClass)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#of(java.lang.Class)):
  Choose randomly from all values of an `enum`. Shrink towards first enum value.

- [`Arbitrary<T> create(Supplier<T> supplier)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#create(java.util.function.Supplier)):
  In each try use a new unshrinkable instance of type `T` using `supplier` to freshly create it.
  This is useful when dealing with mutable objects where `Arbitrary.just()` may reuse a changed object.

#### Select randomly with Weights

If you have a set of values to choose from with weighted probabilities, use
[`Arbitraries.frequency(...)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#frequency(net.jqwik.api.Tuple.Tuple2...)):

```java
@Property
void abcdWithFrequencies(@ForAll("abcdWeighted") String aString) {
    Statistics.collect(aString);
}

@Provide
Arbitrary<String> abcdWeighted() {
    return Arbitraries.frequency(
        Tuple.of(1, "a"),
        Tuple.of(5, "b"),
        Tuple.of(10, "c"),
        Tuple.of(20, "d")
    );
}
```

The first value of the tuple specifies the frequency of a particular value in relation to the
sum of all frequencies. In
[the given example](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/ChoosingExamples.java#L17)
the sum is 36, thus `"a"` will be generated with a probability of `1/36`
whereas `"d"` has a generation probability of `20/36` (= `5/9`).

Shrinking moves towards the start of the frequency list.

#### Characters and Strings

You can browse the API for generating strings and chars here:

- [`CharacterArbitrary chars()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#chars())
- [`StringArbitrary strings()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#strings())


When it comes to defining the base set of possible chars to choose from 
Character and String arbitraries work very similarly, e.g.

```java
CharacterArbitrary chars = Arbitraries.chars()
                              .numeric()
                              .alpha()
                              .with('.', ',', ';', '!', '?');
```

creates a generator for alphanumeric chars plus the most common punctuation
(but no spaces!). For strings combined of this letters, the code is:

```java
StringArbitrary strings = Arbitraries.strings()
                            .numeric()
                            .alpha()
                            .withChars('.', ',', ';', '!', '?');
```

#### String Size

Without any additional configuration, the size of generated strings
is between 0 and 255. 
To change this `StringArbitrary` comes with additional capabilities to set the minimal
and maximal length of a string:

- `ofLength(int)`: To fix the length of a generated string
- `ofMinLength(int)`: To set the lower bound for string length
- `ofMaxLength(int)`: To set the upper bound for string length

You can also influence the
[random distribution](#random-numeric-distribution) of the length.
If you want, for example, a uniform distribution of string length between
5 and 25 characters, this is how you do it:

```java
Arbitraries.strings().ofMinLength(5).ofMaxLength(25)
		   .withLengthDistribution(RandomDistribution.uniform());
```

#### java.util.Random

- [`Arbitrary<Random> randoms()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#randoms()):
  Random instances will never be shrunk

#### Shuffling Permutations

- [`Arbitrary<List<T>> shuffle(T ... values)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#shuffle(T...)):
  Return unshrinkable permutations of the `values` handed in.

- [`Arbitrary<List<T>> shuffle(List<T> values)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#shuffle(java.util.List)):
  Return unshrinkable permutations of the `values` handed in.

#### Default Types

- [`Arbitrary<T> defaultFor(Class<T> type, Class<?> ... parameterTypes)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#defaultFor-java.lang.Class(java.lang.Class...)):
  Return the default arbitrary available for type `type` [if one is provided](#providing-default-arbitraries)
  by default. For parameterized types you can also specify the parameter types.

  Keep in mind, though, that the parameter types are lost in the type signature and therefore
  cannot be used in the respective [`@ForAll`](/docs/${docsVersion}/javadoc/net/jqwik/api/ForAll.html) property method parameter. Raw types and wildcards,
  however, match; thus the following example will work:

  ```java
  @Property
  boolean listWithWildcard(@ForAll("stringLists") List<?> stringList) {
      return stringList.isEmpty() || stringList.get(0) instanceof String;
  }
   
  @Provide
  Arbitrary<List> stringLists() {
      return Arbitraries.defaultFor(List.class, String.class);
  }
  ```

### Numeric Arbitrary Types

Creating an arbitrary for numeric values also starts by calling a static method
on class `Arbitraries`. There are two fundamental types of numbers: _integral_ numbers
and _decimal_ numbers. _jqwik_ supports all of Java's built-in number types.

Each type has its own [fluent interface](https://en.wikipedia.org/wiki/Fluent_interface)
but all numeric arbitrary types share some things:

- You can constrain their minimum and maximum values using `between(min, max)`,
  `greaterOrEqual(min)` and `lessOrEqual(max)`.
- You can determine the _target value_ through `shrinkTowards(target)`.
  This value is supposed to be the "center" of all possible values used for shrinking
  and as a mean for [random distributions](random-numeric-distribution).

#### Integrals

- [`ByteArbitrary bytes()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#bytes())
- [`ShortArbitrary shorts()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#shorts())
- [`IntegerArbitrary integers()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#integers())
- [`LongArbitrary longs()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#longs())
- [`BigIntegerArbitrary bigIntegers()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#bigIntegers())

#### Decimals

- [`FloatArbitrary floats()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#floats())
- [`DoubleArbitrary doubles()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#doubles())
- [`BigDecimalArbitrary bigDecimals()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#bigDecimals())

Decimal arbitrary types come with a few additional capabilities:

- You can include or exclude the borders using `between(min, minIncluded, max, maxIncluded)`,
  `greaterThan(minExcluded)` and `lessThan(maxExclude)`.
- You can set the _scale_, i.e. number of significant decimal places with `ofScale(scale)`.
  The default scale is `2`.

#### Special Decimal Values

Since the generation of decimal values is constrained by the significant decimal places,
some special values, like `MIN_NORMAL` and `MIN_VALUE`, will never be generated,
although they are attractors of bugs in some cases.
That's why `DecimalArbitrary` and `FloatArbitrary` provide you with the capability 
to add special values into the possible generation scope:

- `DoubleArbitrary.withSpecialValue(double)`
- `DoubleArbitrary.withStandardSpecialValues()`
- `FloatArbitrary.withSpecialValue(float)`
- `FloatArbitrary.withStandardSpecialValues()`

Special values are also considered to be edge cases and they are used in exhaustive generation.
_Standard special values_ are: `MIN_VALUE`, `MIN_NORMAL`, `NaN`, `POSITIV_INFINITY` and `NEGATIVE_INFINITY`.

#### Random Numeric Distribution

With release `1.3.0` jqwik provides you with a means to influence the probability distribution
of randomly generated numbers. The way to do that is by calling
[`withDistribution(distribution)`](https://jqwik.net/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/NumericalArbitrary.html#withDistribution(net.jqwik.api.RandomDistribution)).
Currently three different distributions are supported:

- [`RandomDistribution.biased()`](https://jqwik.net/docs/${docsVersion}/javadoc/net/jqwik/api/RandomDistribution.html#biased()):
  This is the default.
  It generates values closer to the center of a numerical range with a higher probability.
  The bigger the range the stronger the bias.

- [`RandomDistribution.uniform()`](https://jqwik.net/docs/${docsVersion}/javadoc/net/jqwik/api/RandomDistribution.html#uniform()):
  This distribution will generate values across the allowed range
  with a uniform probability distribution.

- [`RandomDistribution.gaussian(borderSigma)`](https://jqwik.net/docs/${docsVersion}/javadoc/net/jqwik/api/RandomDistribution.html#gaussian(double)):
  A (potentially asymmetric) gaussian distribution --
  aka "normal distribution" () the mean of which is the specified center
  and the probability at the borders is `borderSigma` times _standard deviation_.
  Gaussian generation is approximately 10 times slower than biased or uniform generation.

- [`RandomDistribution.gaussian()`](https://jqwik.net/docs/${docsVersion}/javadoc/net/jqwik/api/RandomDistribution.html#gaussian()):
  A gaussian distribution with `borderSigma` of 3, i.e. approximately 99.7% of values are within the borders.

The specified distribution does not influence the generation of [edge cases](#generation-of-edge-cases).

The following example generates numbers between 0 and 20 using a gaussian probability distribution
with its mean at 10 and a standard deviation of about 3.3:

```java
@Property(generation = GenerationMode.RANDOMIZED)
@StatisticsReport(format = Histogram.class)
void gaussianDistributedIntegers(@ForAll("gaussians") int aNumber) {
    Statistics.collect(aNumber);
}

@Provide
Arbitrary<Integer> gaussians() {
    return Arbitraries
               .integers()
               .between(0, 20)
               .shrinkTowards(10)
               .withDistribution(RandomDistribution.gaussian());
}
```

Look at the statistics to see if it fits your expectation:
```
[RandomDistributionExamples:gaussianDistributedIntegers] (1000) statistics = 
       # | label | count | 
    -----|-------|-------|---------------------------------------------------------------------------------
       0 |     0 |    15 | ■■■■■
       1 |     1 |     8 | ■■
       2 |     2 |    12 | ■■■■
       3 |     3 |     9 | ■■■
       4 |     4 |    14 | ■■■■
       5 |     5 |    28 | ■■■■■■■■■
       6 |     6 |    38 | ■■■■■■■■■■■■■
       7 |     7 |    67 | ■■■■■■■■■■■■■■■■■■■■■■■
       8 |     8 |    77 | ■■■■■■■■■■■■■■■■■■■■■■■■■■
       9 |     9 |   116 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      10 |    10 |   231 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      11 |    11 |   101 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      12 |    12 |    91 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      13 |    13 |    60 | ■■■■■■■■■■■■■■■■■■■■
      14 |    14 |    45 | ■■■■■■■■■■■■■■■
      15 |    15 |    36 | ■■■■■■■■■■■■
      16 |    16 |    19 | ■■■■■■
      17 |    17 |    10 | ■■■
      18 |    18 |     7 | ■■
      19 |    19 |     1 | 
      20 |    20 |    15 | ■■■■■
```

You can notice that values `0` and `20` should have the lowest probability but they do not.
This is because they will be generated a few times as edge cases.


### Collections, Streams, Iterators and Arrays

Arbitraries for multi value types require to start with an `Arbitrary` instance for the element type.
You can then create the corresponding multi value arbitrary from there:

- [`ListArbitrary<T> Arbitrary.list()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#list())
- [`SetArbitrary<T> Arbitrary.set()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#set())
- [`StreamArbitrary<T> Arbitrary.streamOf()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#stream())
- [`IteratorArbitrary<T> Arbitrary.iterator()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#iterator())
- [`ArrayArbitrary<T, A> Arbitrary.array(Class<A> arrayClass)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#array(java.lang.Class))


#### Size of Multi-value Containers

Without any additional configuration, the size of generated containers (lists, sets, arrays etc.)
is between 0 and 255. To change this all the arbitraries from above support
- `ofSize(int)`: To fix the size of a generated container
- `ofMinSize(int)`: To set the lower bound for container size
- `ofMaxSize(int)`: To set the upper bound for container size

Usually the distribution of generated container size is heavily distorted 
towards the allowed minimum. 
If you want to influence the random distribution you can use
`withSizeDistribution(RandomDistribution)`. For example:

```java
Arbitraries.integers().list().ofMaxSize(100)
    .withSizeDistribution(RandomDistribution.uniform());
```

See the section on [random numeric distribution](#random-numeric-distribution)
to check out the available distribution implementations.


### Collecting Values in a List

If you do not want any random combination of values in your list - as
can be done with `Arbitrary.list()` - you have the possibility to collect random values
in a list until a certain condition is fulfilled.
[`Arbitrary.collect(Predicate condition)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#collect(java.util.function.Predicate))
is what you need in those cases.

Imagine you need a list of integers the sum of which should be at least `1000`.
Here's how you could do that:

```java
Arbitrary<Integer> integers = Arbitraries.integers().between(1, 100);
Arbitrary<List<Integer>> collected = integers.collect(list -> sum(list) >= 1000);
```

### Optional

Using [`Arbitrary.optional(double presenceProbability)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#optional(double))
allows to generate an optional of any type.
`Optional.empty()` values are injected with a probability of `1 - presenceProbability`.

Just using [`Arbitrary.optional()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#optional())
uses a `presenceProbability` of `0.95`, i.e. 1 in 20 generates is empty.

### Tuples of same base type

If you want to generate tuples of the same base types that also use the same generator, that's how you can do it:

```java
Arbitrary<Tuple.Tuple2> integerPair = Arbitrary.integers().between(1, 25).tuple2();
```

There's a method for tuples of length 1 to 5:

- [`Arbitrary.tuple1()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#tuple1())
- [`Arbitrary.tuple2()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#tuple2())
- [`Arbitrary.tuple3()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#tuple3())
- [`Arbitrary.tuple4()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#tuple4())
- [`Arbitrary.tuple5()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#tuple5())

### Maps

Generating instances of type `Map` is a bit different since two arbitraries
are needed, one for the key and one for the value. Therefore you have to use
[`Arbitraries.maps(...)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#maps-net.jqwik.api.Arbitrary(net.jqwik.api.Arbitrary)) like this:

```java
@Property
void mapsFromNumberToString(@ForAll("numberMaps")  Map<Integer, String> map) {
    Assertions.assertThat(map.keySet()).allMatch(key -> key >= 0 && key <= 1000);
    Assertions.assertThat(map.values()).allMatch(value -> value.length() == 5);
}

@Provide
Arbitrary<Map<Integer, String>> numberMaps() {
    Arbitrary<Integer> keys = Arbitraries.integers().between(1, 100);
    Arbitrary<String> values = Arbitraries.strings().alpha().ofLength(5);
    return Arbitraries.maps(keys, values);
}
```

#### Map Size

Influencing the size of a generated map works exactly like 
[in other multi-value containers](#size-of-multi-value-containers).

#### Map Entries

For generating individual `Map.Entry` instances there is
[`Arbitraries.entries(...)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#maps(net.jqwik.api.Arbitrary,net.jqwik.api.Arbitrary)).

### Functional Types

Interfaces that have a single (non default) method are considered to be
_Functional types_; they are sometimes called _SAM_ types for "single abstract method".
If a functional type is used as a `@ForAll`-parameter _jqwik_ will automatically
generate instances of those functions. The generated functions have the following
characteristics:

- Given the input parameters they will produce the same return values.
- The return values are generated using the type information and constraints
  in the parameter.
- Given different input parameters they will _usually_ produce different
  return values.
- Shrinking of generated functions will try constant functions, i.e. functions
  that always return the same value.

Let's look at an example:

```java
@Property
void fromIntToString(@ForAll Function<Integer, @StringLength(5) String> function) {
    assertThat(function.apply(42)).hasSize(5);
    assertThat(function.apply(1)).isEqualTo(function.apply(1));
}
```

This works for any _interface-based_ functional types, even your own.
If you [register a default provider](#providing-default-arbitraries) for
a functional type with a priority of 0 or above, it will take precedence.

If the functions need some specialized arbitrary for return values or if you
want to fix the function's behaviour for some range of values, you can define
the arbitrary manually:

```java
@Property
void emptyStringsTestFalse(@ForAll("predicates") Predicate<String> predicate) {
    assertThat(predicate.test("")).isFalse();
}

@Provide
Arbitrary<Predicate<String>> predicates() {
    return Functions
        .function(Predicate.class)
        .returns(Arbitraries.of(true, false))
        .when(parameters -> parameters.get(0).equals(""), parameters -> false);
}
```

In this example the generated predicate will always return `false` when
given an empty String and randomly choose between `true` and `false` in
all other cases.

### Fluent Configuration Interfaces

Most specialized arbitrary interfaces provide special methods to configure things
like size, length, boundaries etc. Have a look at the Java doc for the following types,
which are organized in a flat hierarchy:

- [NumericalArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/BigDecimalArbitrary.html)
    - [BigDecimalArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/BigDecimalArbitrary.html)
    - [BigIntegerArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/BigIntegerArbitrary.html)
    - [ByteArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/ByteArbitrary.html)
    - [CharacterArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/CharacterArbitrary.html)
    - [DoubleArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/DoubleArbitrary.html)
    - [FloatArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/FloatArbitrary.html)
    - [IntegerArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/IntegerArbitrary.html)
    - [LongArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/LongArbitrary.html)
    - [ShortArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/ShortArbitrary.html)
- [SizableArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/SizableArbitrary.html)
    - [MapArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/MapArbitrary.html)
    - [StreamableArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/StreamableArbitrary.html)
        - [SetArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/SetArbitrary.html)
        - [ListArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/ListArbitrary.html)
        - [StreamArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/StreamArbitrary.html)
        - [IteratorArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/IteratorArbitrary.html)
        - [ArrayArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/ArrayArbitrary.html)
- [StringArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/StringArbitrary.html)
- [FunctionArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/FunctionArbitrary.html)
- [TypeArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/TypeArbitrary.html)
- [ActionSequenceArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/stateful/ActionSequenceArbitrary.html)


Here are a
[two examples](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/FluentConfigurationExamples.java)
to give you a hint of what you can do:

```java
@Provide
Arbitrary<String> alphaNumericStringsWithMinLength5() {
    return Arbitraries.strings().ofMinLength(5).alpha().numeric();
}

@Provide
Arbitrary<List<Integer>> fixedSizedListOfPositiveIntegers() {
    return Arbitraries.integers().greaterOrEqual(0).list().ofSize(17);
}
```

### Generate `null` values

Predefined generators will never create `null` values. If you want to allow that,
call [`Arbitrary.injectNull(double probability)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#injectNull(double)).
The following provider method creates an arbitrary that will return a `null` String
in about 1 of 100 generated values.

```java
@Provide 
Arbitrary<String> stringsWithNull() {
  return Arbitraries.strings(0, 10).injectNull(0.01);
}
```

### Inject duplicate values

Sometimes it is important that your generator will create _a previous value_
again in order to trigger certain scenarios or branches in your code.
Imagine you want to check if your carefully hand-crafted String comparator really
is as symmetric as it's supposed to be:

```java
Comparator<String> comparator = (s1, s2) -> {
    if (s1.length() + s2.length() == 0) return 0;
    if (s1.compareTo(s2) > 0) {
        return 1;
    } else {
        return -1;
    }
};

@Property
boolean comparing_strings_is_symmetric(@ForAll String first, @ForAll String second) {
    int comparison = comparator.compare(first, second);
    return comparator.compare(second, first) == -comparison;
}
```

The property (most probably) succeeds and will give you confidence in your code.
Or does it? Natural scepticism makes you check some statistics:

```java
@Property(edgeCases = EdgeCasesMode.NONE)
boolean comparing_strings_is_symmetric(@ForAll String first, @ForAll String second) {
    int comparison = comparator.compare(first, second);
    String comparisonRange = comparison < 0 ? "<0" : comparison > 0 ? ">0" : "=0";
    String empty = first.isEmpty() || second.isEmpty() ? "empty" : "not empty";
    Statistics.collect(comparisonRange, empty);
    return comparator.compare(second, first) == -comparison;
}
```

The following output

```
[comparing strings is symmetric] (1000) statistics = 
    <0 not empty (471) : 47,10 %
    >0 not empty (456) : 45,60 %
    <0 empty     ( 37) :  3,70 %
    >0 empty     ( 35) :  3,50 %
    =0 empty     (  1) :  0,10 %
```

reveals that our generated test data is missing one combination:
Comparison value of 0 for non-empty strings. In theory a generic String arbitrary
could generate the same non-empty string but it's highly unlikely.
This is where we have to think about raising the probability of the same
value being generated more often:

```
@Property
boolean comparing_strings_is_symmetric(@ForAll("pair") Tuple2<String, String> pair) {
    String first = pair.get1();
    String second = pair.get2();
    int comparison = comparator.compare(first, second);
    return comparator.compare(second, first) == -comparison;
}

@Provide
Arbitrary<Tuple2<String, String>> pair() {
    return Arbitraries.strings().injectDuplicates(0.1).tuple2();
}
```

This will cover the missing case and will reveal a bug in the comparator.
Mind that you have to make sure that the _same generator instance_ is being used
for the two String values - using `tuple2()` does that.


### Filtering

If you want to include only part of all the values generated by an arbitrary,
use
[`Arbitrary.filter(Predicate<T> filterPredicate)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#filter(java.util.function.Predicate)).
The following arbitrary will filter out all
even numbers from the stream of generated integers:

```java
@Provide 
Arbitrary<Integer> oddNumbers() {
  return Arbitraries.integers().filter(aNumber -> aNumber % 2 != 0);
}
```

Keep in mind that your filter condition should not be too restrictive.
If the generator fails to find a suitable value after 10000 trials,
the current property will be abandoned by throwing an exception.

### Mapping

Sometimes it's easier to start with an existing arbitrary and use its generated values to
build other objects from them. In that case, use
[`Arbitrary.map(Function<T, U> mapper)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#map(java.util.function.Function)).
The following example uses generated integers to create numerical Strings:

```java
@Provide 
Arbitrary<String> fiveDigitStrings() {
  return Arbitraries.integers(10000, 99999).map(aNumber -> String.valueOf(aNumber));
}
```

You could generate the same kind of values by constraining and filtering a generated String.
However, the [shrinking](#result-shrinking) target would probably be different. In the example above, shrinking
will move towards the lowest allowed number, that is `10000`.

#### Mapping over Elements of Collection

`ListArbitrary` and `SetArbitrary` provide you with a convenient way to map over each element 
of a collection and still keep the generated collection. 
This is useful when the mapping function needs access to all elements of the list to do its job:

- [`ListArbitrary.mapEach`](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/ListArbitrary.html#mapEach(java.util.function.BiFunction))

- [`SetArbitrary.mapEach`](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/SetArbitrary.html#mapEach(java.util.function.BiFunction))


The following example will generate a list of integers and enrich the elements with
the number of occurrences of the element within the list:

```java
@Property
void elementsAreCorrectlyCounted(@ForAll("elementsWithOccurrence") List<Tuple2<Integer, Long>> list) {
	Assertions.assertThat(list).allMatch(t -> t.get2() <= list.size());
}

@Provide
Arbitrary<List<Tuple2<Integer, Long>>> elementsWithOccurrence() {
	return Arbitraries.integers().between(10000, 99999).list()
					  .mapEach((all, i) -> {
						  long count = all.stream().filter(e -> e.equals(i)).count();
						  return Tuple.of(i, count);
					  });
}
```


### Flat Mapping

Similar as in the case of `Arbitrary.map(..)` there are situations in which you want to use
a generated value in order to create another Arbitrary from it. Sounds complicated?
Have a look at the
[following example](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/FlatMappingExamples.java#L26):

```java
@Property
boolean fixedSizedStrings(@ForAll("listsOfEqualSizedStrings")List<String> lists) {
    return lists.stream().distinct().count() == 1;
}

@Provide
Arbitrary<List<String>> listsOfEqualSizedStrings() {
    Arbitrary<Integer> integers2to5 = Arbitraries.integers().between(2, 5);
    return integers2to5.flatMap(stringSize -> {
        Arbitrary<String> strings = Arbitraries.strings() 
                .withCharRange('a', 'z') 
                .ofMinLength(stringSize).ofMaxLength(stringSize);
        return strings.list();
    });
}
```
The provider method will create random lists of strings, but in each list the size of the contained strings
will always be the same - between 2 and 5.

#### Flat Mapping with Tuple Types

In the example above you used a generated value in order to create another arbitrary.
In those situations you often want to also provide the original values to your property test.

Imagine, for instance, that you'd like to test properties of `String.substring(begin, end)`.
To randomize the method call, you not only need a string but also the `begin` and `end` indices.
However, both have dependencies:
- `end` must not be larger than the string size
- `begin` must not be larger than `end`
  You can make _jqwik_ create all three values by using
  [`flatMap`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#flatMap(java.util.function.Function))
  combined with a tuple type
  [like this](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/FlatMappingExamples.java#L32):


```java
@Property
void substringLength(@ForAll("stringWithBeginEnd") Tuple3<String, Integer, Integer> stringBeginEnd) {
    String aString = stringBeginEnd.get1();
    int begin = stringBeginEnd.get2();
    int end = stringBeginEnd.get3();
    assertThat(aString.substring(begin, end).length()).isEqualTo(end - begin);
}

@Provide
Arbitrary<Tuple3<String, Integer, Integer>> stringWithBeginEnd() {
    Arbitrary<String> stringArbitrary = Arbitraries.strings() 
            .withCharRange('a', 'z') 
            .ofMinLength(2).ofMaxLength(20);
    return stringArbitrary 
            .flatMap(aString -> Arbitraries.integers().between(0, aString.length()) 
                    .flatMap(end -> Arbitraries.integers().between(0, end) 
                            .map(begin -> Tuple.of(aString, begin, end))));
}
```

Mind the nested flat mapping, which is an aesthetic nuisance but nevertheless
very useful.

#### Flat Mapping over Elements of Collection

Just like [mapping over elements of a collection](#mapping-over-elements-of-collection) 
`ListArbitrary` and `SetArbitrary` provide you with a mechanism to flat-map over each element
of a collection and still keep the generated collection:

- [`ListArbitrary.flatMapEach`](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/ListArbitrary.html#flatMapEach(java.util.function.BiFunction))

- [`SetArbitrary.flatMapEach`](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/SetArbitrary.html#flatMapEach(java.util.function.BiFunction))


#### Implicit Flat Mapping

Flat mapping syntax - especially when it's nested - is a bit cumbersome to read.
Starting with version `1.5.2` _jqwik_ allows to use flat mapping implicitly.
You simply add a `@ForAll` parameter to your provider method, 
the value of which will be generated using standard parameter generation.
Under the hood this uses this parameter's arbitrary and call `flatMap` on it.

Here's the example from above with no explicit flat mapping:

```java
@Property
@Report(Reporting.GENERATED)
void substringLength(@ForAll("stringWithBeginEnd") Tuple3<String, Integer, Integer> stringBeginEnd) {
	String aString = stringBeginEnd.get1();
	int begin = stringBeginEnd.get2();
	int end = stringBeginEnd.get3();
	assertThat(aString.substring(begin, end).length()).isEqualTo(end - begin);
}

@Provide
Arbitrary<String> simpleStrings() {
	return Arbitraries.strings()
					  .withCharRange('a', 'z')
					  .ofMinLength(2).ofMaxLength(20);
}

@Provide
Arbitrary<Tuple2<String, Integer>> stringWithEnd(@ForAll("simpleStrings") String aString) {
	return Arbitraries.integers().between(0, aString.length())
					  .map(end -> Tuple.of(aString, end));
}

@Provide
Arbitrary<Tuple3<String, Integer, Integer>> stringWithBeginEnd(@ForAll("stringWithEnd") Tuple2<String, Integer> stringWithEnd) {
	String aString = stringWithEnd.get1();
	int end = stringWithEnd.get2();
	return Arbitraries.integers().between(0, end)
					  .map(begin -> Tuple.of(aString, begin, end));
}
```

### Randomly Choosing among Arbitraries

If you have several arbitraries of the same type, you can create a new arbitrary of
the same type which will choose randomly one of those arbitraries before generating
a value:

```java
@Property
boolean intsAreCreatedFromOneOfThreeArbitraries(@ForAll("oneOfThree") int anInt) {
    String classifier = anInt < -1000 ? "below" : anInt > 1000 ? "above" : "one";
    Statistics.collect(classifier);
    
    return anInt < -1000 //
            || Math.abs(anInt) == 1 //
            || anInt > 1000;
}

@Provide
Arbitrary<Integer> oneOfThree() {
    IntegerArbitrary below1000 = Arbitraries.integers().between(-2000, -1001);
    IntegerArbitrary above1000 = Arbitraries.integers().between(1001, 2000);
    Arbitrary<Integer> oneOrMinusOne = Arbitraries.samples(-1, 1);
    
    return Arbitraries.oneOf(below1000, above1000, oneOrMinusOne);
}
```

[In this example](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/OneOfExamples.java)
the statistics should also give you an equal distribution between
the three types of integers.

If you don't want to choose with equal probability - but with differing frequency -
you can do that in a similar way:

```java
@Property(tries = 100)
@Report(Reporting.GENERATED)
boolean intsAreCreatedFromOneOfThreeArbitraries(@ForAll("oneOfThree") int anInt) {
    return anInt < -1000 //
               || Math.abs(anInt) == 1 //
               || anInt > 1000;
}

@Provide
Arbitrary<Integer> oneOfThree() {
    IntegerArbitrary below1000 = Arbitraries.integers().between(-1050, -1001);
    IntegerArbitrary above1000 = Arbitraries.integers().between(1001, 1050);
    Arbitrary<Integer> oneOrMinusOne = Arbitraries.samples(-1, 1);

    return Arbitraries.frequencyOf(
        Tuple.of(1, below1000),
        Tuple.of(3, above1000),
        Tuple.of(6, oneOrMinusOne)
    );
}
```

### Combining Arbitraries

Sometimes just mapping a single stream of generated values is not enough to generate
a more complicated domain object. In those cases you can combine several arbitraries to
a single result arbitrary using
[`Combinators.combine()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Combinators.html#combine(net.jqwik.api.Arbitrary,net.jqwik.api.Arbitrary))
with up to eight arbitraries.
[Create an issue on github](https://github.com/jlink/jqwik/issues) if you need more than eight.

[The following example](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/MappingAndCombinatorExamples.java#L25)
generates `Person` instances from three arbitraries as inputs.

```java
@Property
void validPeopleHaveIDs(@ForAll("validPeople") Person aPerson) {
    Assertions.assertThat(aPerson.getID()).contains("-");
    Assertions.assertThat(aPerson.getID().length()).isBetween(5, 24);
}

@Provide
Arbitrary<Person> validPeople() {
    Arbitrary<String> names = Arbitraries.strings().withCharRange('a', 'z')
        .ofMinLength(3).ofMaxLength(21);
    Arbitrary<Integer> ages = Arbitraries.integers().between(0, 130);
    return Combinators.combine(names, ages)
        .as((name, age) -> new Person(name, age));
}

class Person {
    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getID() {
        return name + "-" + age;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", name, age);
    }
}
```

The property should fail, thereby shrinking the falsified Person instance to
```
Shrunk Sample (<n> steps)
-------------------------
  aPerson: aaaaaaaaaaaaaaaaaaaaa:100
```

The `Combinators.combine` method accepts up to 8 parameters of type Arbitrary.
If you need more you have a few options:

- Consider to group some parameters into an object of their own and change your design
- Generate inbetween arbitraries e.g. of type `Tuple` and combine those in another step
- Introduce a build for your domain object and combine them
  [in this way](#combining-arbitraries-with-builders)

#### Flat Combination

If generating domain values requires to use several generated values to be used
in generating another one, there's the combination of flat mapping and combining:

```java
@Property
boolean fullNameHasTwoParts(@ForAll("fullName") String aName) {
    return aName.split(" ").length == 2;
}

@Provide
Arbitrary<String> fullName() {
    IntegerArbitrary firstNameLength = Arbitraries.integers().between(2, 10);
    IntegerArbitrary lastNameLength = Arbitraries.integers().between(2, 10);
    return Combinators.combine(firstNameLength, lastNameLength).flatAs( (fLength, lLength) -> {
        Arbitrary<String> firstName = Arbitraries.strings().alpha().ofLength(fLength);
        Arbitrary<String> lastName = Arbitraries.strings().alpha().ofLength(fLength);
        return Combinators.combine(firstName, lastName).as((f,l) -> f + " " + l);
    });
}
```

Often, however, there's an easier way to achieve the same goal which
does not require the flat combination of arbitraries:

```java
@Provide
Arbitrary<String> fullName2() {
    Arbitrary<String> firstName = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(10);
    Arbitrary<String> lastName = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(10);
    return Combinators.combine(firstName, lastName).as((f, l) -> f + " " + l);
}
```

This is not only easier to understand but it usually improves shrinking.


### Combining Arbitraries with Builders

There's an alternative way to combine arbitraries to create an aggregated object
by using a builder for the aggregated object. Consider the example from
[above](#combining-arbitraries) and throw a `PersonBuilder` into the mix:

```java
static class PersonBuilder {

    private String name = "A name";
    private int age = 42;

    public PersonBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PersonBuilder withAge(int age) {
        this.age = age;
        return this;
    }

    public Person build() {
        return new Person(name, age);
    }
}
```

Then you can go about generating people in the following way:

```java
@Provide
Arbitrary<Person> validPeopleWithBuilder() {
    Arbitrary<String> names = 
        Arbitraries.strings().withCharRange('a', 'z').ofMinLength(2).ofMaxLength(20);
    Arbitrary<Integer> ages = Arbitraries.integers().between(0, 130);
    
    return Builders.withBuilder(() -> new PersonBuilder())
        .use(names).in((builder, name) -> builder.withName(name))
        .use(ages).withProbability(0.5).in((builder, age)-> builder.withAge(age))
        .build( builder -> builder.build());
}
```

If you don't want to introduce an explicit builder object, 
you can also use a mutable POJO -- e.g. a Java bean -- instead:

```java
@Provide
Arbitrary<Person> validPeopleWithPersonAsBuilder() {
	Arbitrary<String> names =
		Arbitraries.strings().withCharRange('a', 'z').ofMinLength(3).ofMaxLength(21);
	Arbitrary<Integer> ages = Arbitraries.integers().between(0, 130);

	return Builders.withBuilder(() -> new Person(null, -1))
				   .use(names).inSetter(Person::setName)
				   .use(ages).withProbability(0.5).inSetter(Person::setAge)
				   .build();
}
```

Have a look at
[Builders.withBuilder(Supplier)](/docs/${docsVersion}/javadoc/net/jqwik/api/Builders.html#withBuilder(java.util.function.Supplier))
to check the API.

### Uniqueness Constraints

In many problem domains there exist identifying features or attributes 
that must not appear more than once.
In those cases the multiple generation of objects can be restricted by
either [annotating parameters with `@UniqueElements`](#unique-elements)
or by using one of the many `uniqueness(..)` configuration methods for 
collections and collection-like types:

- `ListArbitrary<T>.uniqueElements(Function<T, Object>)`
- `ListArbitrary<T>.uniqueElements()`
- `SetArbitrary<T>.uniqueElements(Function<T, Object>)`
- `StreamArbitrary<T>.uniqueElements(Function<T, Object>)`
- `StreamArbitrary<T>.uniqueElements()`
- `IteratorArbitrary<T>.uniqueElements(Function<T, Object>)`
- `IteratorArbitrary<T>.uniqueElements()`
- `ArrayArbitrary<T, A>.uniqueElements(Function<T, Object>)`
- `ArrayArbitrary<T, A>.uniqueElements()`
- `MapArbitrary<K, V>.uniqueKeys(Function<K, Object>)`
- `MapArbitrary<K, V>.uniqueValues(Function<V, Object>)`
- `MapArbitrary<K, V>.uniqueValues()`

The following examples demonstrates how to generate a list of `Person` objects
whose names must be unique:

```java
@Property
void listOfPeopleWithUniqueNames(@ForAll("people") List<Person> people) {
  List<String> names = people.stream().map(p -> p.name).collect(Collectors.toList());
  Assertions.assertThat(names).doesNotHaveDuplicates();
}

@Provide
Arbitrary<List<Person>> people() {
  Arbitrary<String> names = Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(20);
  Arbitrary<Integer> ages = Arbitraries.integers().between(0, 120);
  
  Arbitrary<Person> persons = Combinators.combine(names, ages).as((name, age) -> new Person(name, age));
  return persons.list().uniqueElements(p -> p.name);
};
```

### Ignoring Exceptions During Generation

Once in a while, usually when [combining generated values](#combining-arbitraries),
it's difficult to figure out in advance all the constraints that make the generation of objects
valid. In a good object-oriented model, however, the objects themselves --
i.e. their constructors or factory methods -- take care that only valid objects
can be created. The attempt to create an invalid value will be rejected with an
exception.

As a good example have a look at JDK's `LocalDate` class, which allows to instantiate dates
using `LocalDate.of(int year, int month, int dayOfMonth)`.
In general `dayOfMonth` can be between `1` and `31` but trying to generate a
"February 31" will throw a `DateTimeException`. Therefore, when you want to randomly
generated dates between "January 1 1900" and "December 31 2099" you have two choices:

- Integrate all rules about valid dates -- including leap years! -- into your generator.
  This will probably require a cascade of flat-mapping `years` to `months` to `days`.
- Rely on the factory method's built-in validation and just ignore thrown
  `DateTimeException` instances:

```java
@Provide
Arbitrary<LocalDate> datesBetween1900and2099() {
  Arbitrary<Integer> years = Arbitraries.integers().between(1900, 2099);
  Arbitrary<Integer> months = Arbitraries.integers().between(1, 12);
  Arbitrary<Integer> days = Arbitraries.integers().between(1, 31);
  
  return Combinators.combine(years, months, days)
  	  .as(LocalDate::of)
  	  .ignoreException(DateTimeException.class);
}
```

### Fix an Arbitrary's `genSize`

Some generators (e.g. most number generators) are sensitive to the
`genSize` value that is used when creating them.
The default value for `genSize` is the number of tries configured for the property
they are used in. If there is a need to influence the behaviour of generators
you can do so by using
[`Arbitrary.fixGenSize(int)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#fixGenSize(int)).

