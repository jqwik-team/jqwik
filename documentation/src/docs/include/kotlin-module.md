This module's artefact name is `jqwik-Module`. 
It's supposed to simplify and streamline using _jqwik_ in Kotlin projects. 

This module is _not_ in jqwik's default dependencies. 
It's usually added as a test-implementation dependency.

__Table of contents:__

- [Build Configuration for Kotlin](#build-configuration-for-kotlin)
- [Differences to Java Usage](#differences-to-java-usage)
- [Support for Kotlin Collection Types](#support-for-kotlin-collection-types)
- [Support for Kotlin-only Types](#supported-kotlin-only-types)
- [Automatic generation of nullable types](#generation-of-nullable-types)
- [Convenience Functions for Kotlin](#convenience-functions-for-kotlin)
  - [Extensions for Built-In Kotlin Types](#extensions-for-built-in-kotlin-types)
  - [Arbitrary Extensions](#arbitrary-extensions)
  - [Kotlin Top-Level Functions](#kotlin-top-level-functions)
  - [Jqwik Tuples in Kotlin](#jqwik-tuples-in-kotlin)
- [Quirks and Bugs](#quirks-and-bugs)

#### Build Configuration for Kotlin

Apart from adding this module to the dependencies in test scope,
there's a few other things you should configure for a seamless jqwik experience in Kotlin:

- As of this writing the current kotlin version (1.5.31) does not generate byte code 
  for Java annotations by default. 
  It must be switched on through compiler argument `-Xemit-jvm-type-annotations`. 

- In order to have nullability information for jqwik's API available in Kotlin
  _JSR305_ compatibility should be switched on with compiler argument `-Xjsr305=strict`.

Here's the jqwik-related part of the Gradle build file for a Kotlin project:

```kotlin
dependencies {
    ...
    testImplementation("net.jqwik:jqwik-kotlin:${version}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
			"-Xjsr305=strict", // Required for strict interpretation of
			"-Xemit-jvm-type-annotations" // Required for annotations on type variables
		)
        jvmTarget = "11" // 1.8 or above
        javaParameters = true // Required to get correct parameter names in reporting
    }
}
```

#### Differences to Java Usage

Kotlin is very compatible with Java, but a few things do not work or do not work as expected.
Here are a few of those which I noticed to be relevant for jqwik:

- Repeatable annotations do not exist (yet) in Kotlin. 
  That's why the container annotation must be used explicitly if you need for example more than one tag:
  
  ```kotlin
  @TagList(
      Tag("tag1"), Tag("tag2")
  )
  @Property
  fun myProperty() { ... }
  ```
  That's also necessary for multiple `@Domain`, `@StatisticsReport` etc.


- The positioning of constraint annotations can be confusing since 
  Kotlin allows annotations at the parameter and at the parameter's type. 
  So both of these will constrain generation of Strings to use only alphabetic characters:

  ```kotlin
  @Property
  fun test(@ForAll @AlphaChars aString: String) { ... }
  ```
  
  ```kotlin
  @Property
  fun test(@ForAll aString: @AlphaChars String) { ... }
  ```

  Mind that `@ForAll` must always precede the parameter, though!

- Grouping - aka nesting - of test container classes requires the `inner` modifier.
  The reason is that nested classes without `inner` are considered to be `static`
  in the Java reflection API. 

  ```kotlin
  class GroupingExamples {
  
      @Property
      fun plainProperty(@ForAll anInt: Int) {}
  
      @Group
      inner class OuterGroup {
  
          @Property
          fun propertyInOuterGroup(@ForAll anInt: Int) {}
  
          @Group
          inner class InnerGroup {
              @Property
              fun propertyInInnerGroup(@ForAll anInt: Int) {}
          }
      }
  }  
  ```

#### Generation of Nullable Types

Top-level nullable Kotlin types are recognized, i.e., `null`'s will automatically be
generated with a probability of 5%. 
If you want a different probability you have to use `@WithNull(probability)`.

```kotlin
@Property
fun alsoGenerateNulls(@ForAll nullOrString: String?) {
    println(nullOrString)
}
```

Note that the detection of nullable types only works for top-level types.
Using `@ForAll list: List<String?>` will __not__ result in `null` values within the list.
Nullability for type parameters must be explicitly set through `@WithNull`:

```kotlin
@Property(tries = 100)
fun generateNullsInList(@ForAll list: List<@WithNull String>) {
    println(list)
}
```

#### Support for Kotlin Collection Types

Kotlin has its own variations of collection types, e.g. (`kotlin.collections.List` and `kotlin.collections.MutableList`) 
that are - under the hood - instances of their corresponding, mutable Java type.
Using those types in ForAll-parameters works as expected.
This is also true for Kotlin's notation of arrays, e.g. `Array<Int>`, 
and Kotlin's unsigned integer types: `UByte`, `UShort`, `UInt` and `ULong`.

#### Supported Kotlin-only Types

The Kotlin standard library comes with a lot of types that don't have an equivalent in the JDK.
Some of them are already supported directly:

##### `IntRange`

- Create an `IntRangeArbitrary` through `IntRange.any()` or `IntRange.any(range)`

- Using `IntRange` as type in a for-all-parameter will auto-generate it.
  You can use annotations `@JqwikIntRange` and `@Size` in order to
  constrain the possible ranges.


##### `Sequence<T>`

- Create a `SequenceArbitrary` by using `.sequence()` on any other arbitrary,
  which will be used to generate the elements for the the sequence.
  `SequenceArbitrary` offers similar configurability as most other multi-value arbitraries in jqwik.

- Using `Sequence` as type in a for-all-parameter will auto-generate it.
  You can use annotations @Size` in order to
  constrain number of values produced by the sequence.

jqwik will never create infinite sequences by itself.

##### `Pair<A, B>`

- Create an instance of `Arbitrary<Pair<A, B>>` by using the global function
  `anyPair(a: Arbitrary<A>, b: Arbitrary<B>)`.

- Create an instance of `Arbitrary<Pair<T, T>>` by calling `arbitraryForT.pair()`.

- Using `Pair` as type in a for-all-parameter will auto-generate,
  thereby using the type parameters with their annotations to create the
  component arbitraries.

##### `Triple<A, B, C>`

- Create an instance of `Arbitrary<Triple<A, B, C>>` by using the global function
  `anyTriple(a: Arbitrary<A>, b: Arbitrary<B>, c: Arbitrary<C>)`.

- Create an instance of `Arbitrary<Triple<T, T, T>>` by calling `arbitraryForT.triple()`.

- Using `Triple` as type in a for-all-parameter will auto-generate,
  thereby using the type parameters with their annotations to create the
  component arbitraries.

#### Convenience Functions for Kotlin

Some parts of the jqwik API are hard to use in Kotlin. 
That's why this module offers a few extension functions and top-level functions
to ease the pain.

##### Extensions for Built-in Kotlin Types

- `String.any() : StringArbitrary` can replace `Arbitraries.strings()`

- `Char.any() : CharacterArbitrary` can replace `Arbitraries.chars()`

- `Char.any(range: CharRange) : CharacterArbitrary` can replace `Arbitraries.chars().range(..)`

- `Boolean.any() : Arbitrary<Boolean>` can replace `Arbitraries.of(false, true)`

- `Byte.any() : ByteArbitrary` can replace `Arbitraries.bytes()`

- `Byte.any(range: IntRange) : ByteArbitrary` can replace `Arbitraries.bytes().between(..)`

- `Short.any() : ShortArbitrary` can replace `Arbitraries.shorts()`

- `Short.any(range: IntRange) : ShortArbitrary` can replace `Arbitraries.shorts().between(..)`

- `Int.any() : IntegerArbitrary` can replace `Arbitraries.integers()`

- `Int.any(range: IntRange) : IntegerArbitrary` can replace `Arbitraries.integers().between(..)`

- `Long.any() : LongArbitrary` can replace `Arbitraries.longs()`

- `Long.any(range: LongRange) : LongArbitrary` can replace `Arbitraries.longs().between(..)`

- `Float.any() : FloatArbitrary` can replace `Arbitraries.floats()`

- `Float.any(range: ClosedFloatingPointRange<Float>) : FloatArbitrary` can replace `Arbitraries.floats().between(..)`

- `Double.any() : DoubleArbitrary` can replace `Arbitraries.doubles()`

- `Double.any(range: ClosedFloatingPointRange<Float>) : DoubleArbitrary` can replace `Arbitraries.doubles().between(..)`


##### Arbitrary Extensions

- `Arbitrary.orNull(probability: Double) : T?` can replace `Arbitrary.injectNull(probabilit)`
  and returns a nullable type.

##### Kotlin Top-Level Functions

- `combine(a1: Arbitrary<T1>, ..., (v1: T1, ...) -> R)` can replace all
  variants of `Combinators.combine(a1, ...).as((v1: T1, ...) -> R)`. 
  Here's an example:

  ```kotlin
  @Property
  fun `full names have a space`(@ForAll("fullNames") fullName: String) {
      Assertions.assertThat(fullName).contains(" ")
  }

  @Provide
  fun fullNames() : Arbitrary<String> {
      val firstNames = String.any().alpha().ofMinLength(1)
      val lastNames = String.any().alpha().ofMinLength(1)
      return combine(firstNames, lastNames) {first, last -> first + " " + last }
  }
  ```

- `flatCombine(a1: Arbitrary<T1>, ..., (v1: T1, ...) -> Arbitrary<R>)` can replace all
  variants of `Combinators.combine(a1, ...).flatAs((v1: T1, ...) -> Arbitrary<R>)`.

##### Jqwik Tuples in Kotlin

Jqwik's `Tuple` types can be used to assign multi values, e.g.

`val (a, b) = Tuple.of(1, 2)`

will assign `1` to variable `a` and `2` to variable `b`.

#### Quirks and Bugs

- Despite our best effort to enrich jqwik's Java API with nullability information,
  the derived Kotlin types are not always correct. 
  That means that you may run into `null` objects despite the type system showing non null types,
  or you may have to ignore Kotlin's warning about nullable types where in practice nulls are impossible.

- As of this writing Kotlin still has a few bugs when it comes to supporting Java annotations.
  That's why in some constellations you'll run into strange behaviour - usually runtime exceptions or ignored constraints - when using predefined jqwik annotations on types.

- Some prominent types in jqwik's API have a counterpart with the same name in 
  Kotlin's default namespace and must therefore be either fully qualified or 
  be imported manually (since the IDE assumes Kotlin's default type)
  or, even better, use the predefined type alias:
  - `net.jqwik.api.constraints.ShortRange` : `JqwikIntRange`
  - `net.jqwik.api.constraints.IntRange` : `JqwikShortRange`
  - `net.jqwik.api.constraints.LongRange` : `JqwikLongRange`
  - `net.jqwik.api.constraints.CharRange` : `JqwikCharRange`

- Some types, e.g. `UByte`, are not visible during runtime. 
  That means that jqwik cannot determine if an `int` value is really a `UByte`,
  which will lead to confusing value reporting, e.g. a UByte value of `254` is reported
  as `-2` because that's the internal representation.

- Kotlin's unsigned integer types (`UByte`, `UShort`, `UInt` and `ULong`) look like their
  signed counter parts to the JVM. Default generation works but range constraints do not.
  If you build your own arbitraries for unsigned types you have to generate 
  `Byte` instead of `UByte` and so on.
  One day _jqwik_ may be able to handle the intricacies of hidden Kotlin types
  better. 
  [Create an issue](https://github.com/jlink/jqwik/issues/new) if that's important for you.
  
  
