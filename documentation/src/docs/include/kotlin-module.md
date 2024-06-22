This module's artefact name is `jqwik-kotlin`. 
It's supposed to simplify and streamline using _jqwik_ in Kotlin projects. 

This module is _not_ in jqwik's default dependencies. 
It's usually added as a test-implementation dependency.

Adding this module will add dependencies on:
- `org.jetbrains.kotlin:kotlin-stdlib`
- `org.jetbrains.kotlin:kotlin-reflect`
- `org.jetbrains.kotlinx:kotlinx-coroutines-core`

__Table of contents:__

- [Build Configuration for Kotlin](#build-configuration-for-kotlin)
- [Differences to Java Usage](#differences-to-java-usage)
- [Automatic generation of nullable types](#generation-of-nullable-types)
- [Support for Coroutines and Asynchronous Code](#support-for-coroutines)
- [Support for Kotlin Collection Types](#support-for-kotlin-collection-types)
- [Support for Kotlin Functions](#support-for-kotlin-functions)
- [Support for Kotlin-only Types](#supported-kotlin-only-types)
- [Kotlin Singletons as Test Containers](#kotlin-singletons)
- [Convenience Functions for Kotlin](#convenience-functions-for-kotlin)
  - [Extensions for Built-In Kotlin Types](#extensions-for-built-in-kotlin-types)
  - [Arbitrary Extensions](#arbitrary-extensions)
  - [SizableArbitrary Extensions](#sizablearbitrary-extensions)
  - [StringArbitrary Extensions](#stringarbitrary-extensions)
  - [Jqwik Tuples in Kotlin](#jqwik-tuples-in-kotlin)
  - [Type-based Arbitraries](#type-based-arbitraries)
  - [Diverse Convenience Functions](#diverse-convenience-functions)
  - [Combinator DSL](#combinator-dsl)
- [Quirks and Bugs](#quirks-and-bugs)

#### Build Configuration for Kotlin

Apart from adding this module to the dependencies in test scope,
there's a few other things you should configure for a seamless jqwik experience in Kotlin:

- As of this writing the current kotlin version (1.5.31) does not generate byte code 
  for Java annotations by default. 
  It must be switched on through compiler argument `-Xemit-jvm-type-annotations`.

Here's the jqwik-related part of the Gradle build file for a Kotlin project:

```kotlin
dependencies {
    ...
    testImplementation("net.jqwik:jqwik-kotlin:${version}")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs = listOf(
            "-Xnullability-annotations=@org.jspecify.annotations:strict",
            "-Xemit-jvm-type-annotations" // Enable annotations on type variables
        )
        apiVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
        languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
        javaParameters = true // Get correct parameter names in jqwik reporting
    }
}
```

#### Differences to Java Usage

Kotlin is very compatible with Java, but a few things do not work or do not work as expected.
Here are a few of those which I noticed to be relevant for jqwik:

- Before Kotlin 1.6.0 repeatable annotations with runtime retention did not work. 
  That's why with Kotlin 1.5 the container annotation must be used explicitly 
  if you need for example more than one tag:
  
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

- Just like abstract classes and interfaces Kotlin's _sealed_ classes
  can give shelter to property methods and other lifecycle relevant behaviour. 
  Sealed classes cannot be "run" themselves, but their subclasses inherit
  all property methods, lifecycle methods and lifecycle hooks from them. 

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

#### Support for Coroutines

In order to test regular suspend functions or coroutines this module offers two options:

- Use the global function `runBlockingProperty`.
- Just add the `suspend` modifier to the property method.

```kotlin
suspend fun echo(string: String): String {
    delay(100)
    return string
}

@Property(tries = 10)
fun useRunBlocking(@ForAll s: String) = runBlockingProperty {
    assertThat(echo(s)).isEqualTo(s)
}

@Property(tries = 10)
fun useRunBlockingWithContext(@ForAll s: String) = runBlockingProperty(EmptyCoroutineContext) {
    assertThat(echo(s)).isEqualTo(s)
}

@Property(tries = 10)
suspend fun useSuspend(@ForAll s: String) {
    assertThat(echo(s)).isEqualTo(s)
}
```

Both variants do nothing more than starting the body of the property method asynchronously
and waiting for all coroutines to finish.
That means e.g. that delays will require the full amount of specified delay time. 

If you need more control over the dispatchers to use or the handling of delays,
you should consider using 
[`kotlinx.coroutines` testing support](https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-test).
This will require to add a dependency on `org.jetbrains.kotlinx:kotlinx-coroutines-test`.


#### Support for Kotlin Collection Types

Kotlin has its own variations of collection types, e.g. (`kotlin.collections.List` and `kotlin.collections.MutableList`) 
that are - under the hood - instances of their corresponding, mutable Java type.
Using those types in ForAll-parameters works as expected.

This is also true for 
- Kotlin's notation of arrays, e.g. `Array<Int>`, 
- Kotlin's unsigned integer types: `UByte`, `UShort`, `UInt` and `ULong`,
- and Kotlin's inline classes which are handled by jqwik like the class they inline.

#### Support for Kotlin Functions

Kotlin provides a generic way to specify functional types, 
e.g. `(String, String) -> Int` specifies a function with two `String` parameters 
that returns an `Int`.
You can use those function types as `ForAll` parameters:

```kotlin
@Property
fun generatedFunctionsAreStable(@ForAll aFunc: (String) -> Int) {
    assertThat(aFunc("hello")).isEqualTo(aFunc("hello"))
}
```

Moreover, there are a few top-level functions to create arbitraries for functional
types: `anyFunction0(Arbitrary<R>)` ... `anyFunction4(Arbitrary<R>)`.
Look at this code example:

```kotlin
@Property
fun providedFunctionsAreAlsoStable(@ForAll("myIntFuncs") aFunc: (String, String) -> Int) {
    assertThat(aFunc("a", "b")).isBetween(10, 1000)
}

@Provide
fun myIntFuncs(): Arbitrary<(String, String) -> Int> = anyFunction2(Int.any(10..1000))
```

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
  which will be used to generate the elements for the sequence.
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


#### Kotlin Singletons

Since Kotlin `object <name> {...}` definitions are compiled to Java classes there is no
reason that they cannot be used as test containers.
As a matter of fact, this module adds a special feature on top:
Using `object` instead of class will always use the same instance for running all properties and examples:

```kotlin
// One of the examples will fail
object KotlinSingletonExample {

    var lastExample: String = ""

    @Example
    fun example1() {
        Assertions.assertThat(lastExample).isEmpty()
        lastExample = "example1"
    }

    @Example
    fun example2() {
        Assertions.assertThat(lastExample).isEmpty()
        lastExample = "example2"
    }
}
```

Mind that IntelliJ in its current version does not mark functions in object definitions
as runnable test functions.
You can, however, run an object-based test container from the project view and the test runner itself.

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

- `Enum.any<EnumType> : Arbitrary<EnumType>` can replace `Arbitraries.of(EnumType::class.java)`


##### Arbitrary Extensions

- `Arbitrary.orNull(probability: Double) : T?` can replace `Arbitrary.injectNull(probabilit)`
  and returns a nullable type.

- `Arbitrary.array<T, A>()` can replace `Arbitrary.array(javaClass: Class<A>)`.

##### SizableArbitrary Extensions

In addition to `ofMinSize(..)` and `ofMaxSize(..)` all sizable
arbitraries can now be configured using `ofSize(min..max)`.

##### StringArbitrary Extensions

In addition to `ofMinLength(..)` and `ofMaxLength(..)` a `StringArbitrary`
can now be configured using `ofLength(min..max)`.

##### Jqwik Tuples in Kotlin

Jqwik's `Tuple` types can be used to assign multi values, e.g.

`val (a, b) = Tuple.of(1, 2)`

will assign `1` to variable `a` and `2` to variable `b`.

##### Type-based Arbitraries

Getting a type-based generator using the Java API looks a bit awkward in Kotlin:
`Arbitraries.forType(MyType::class.java)`.
There's a more Kotlinish way to do the same: `anyForType<MyType>()`.

`anyForType<MyType>()` is limited to concrete classes. For example, it cannot
handle sealed class or interface by looking for sealed subtypes.
`anyForSubtypeOf<MyInterface>()` exists for such situation.

```kotlin
sealed interface Character
sealed interface Hero : Character
class Knight(val name: String) : Hero
class Wizard(val name: String) : Character

val arbitrary =  anyForSubtypeOf<Character>()
```

In the previous example, the created arbitrary provides arbitrarily any instances of `Knight` or `Wizard`.
The arbitrary is recursively based on any sealed subtype.
Under the hood, it uses `anyForType<Subtype>()` for each subtype.
However, this can be customized subtype by subtype, by providing a custom arbitrary:

```kotlin
anyForSubtypeOf<Character> {
    provide<Wizard> { Arbitraries.of(Wizard("Merlin"),Wizard("Élias de Kelliwic’h")) }
}
```

More over, like `anyForType<>()`, `anyForSubtypeOf<>()` can be applied recursively (default is false):
`anyForSubtypeOf<SealedClass>(enableArbitraryRecursion = true)`.


##### Diverse Convenience Functions

- `combine(a1: Arbitrary<T1>, ..., (v1: T1, ...) -> R)` can replace all
  variants of `Combinators.combine(a1, ...).as((v1: T1, ...) -> R)`
  and `Combinators.combine(a1, ...).filter(a1, ...).as((v1: T1, ...) -> R)`.
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
      return combine(
          firstNames, lastNames,
          filter = { firstName, lastName -> firstName != lastName } // optional parameter
      ) { first, last -> first + " " + last }
  }
  ```

- `flatCombine(a1: Arbitrary<T1>, ..., (v1: T1, ...) -> Arbitrary<R>)` can replace all
  variants of `Combinators.combine(a1, ...).flatAs((v1: T1, ...) -> Arbitrary<R>)`.

- `anyFunction(kKlass: KClass<*>)` can replace `Functions.function(kKlass.java)`

- `runBlockingProperty(context: CoroutineContext, block: suspend CoroutineScope.()` to allow
  [testing of coroutines and suspend functions](#support-for-coroutines).

- `Builders.BuilderCombinator.use(arbitrary, combinator)` to simplify Java API call
  `Builders.BuilderCombinator.use(arbitrary).in(combinator)` which requires backticks
  in Kotlin because "in" is a keyword.

- `frequency(vararg frequencies: Pair<Int, T>)` can replace `Arbitraries.frequency(vararg Tuple.Tuple2<Int, T>)`

- `frequencyOf(vararg frequencies: Pair<Int, Arbitrary<out T>>)` can replace 
  `Arbitraries.frequencyOf(vararg Tuple.Tuple2<Int, Arbitrary<out T>>)`

- `Collection<T>.anyValue()` can replace 
  `Arbitraries.of(collection: Collection<T>)`

- `Collection<T>.anySubset()` can replace 
  `Arbitraries.subsetOf(collection: Collection<T>)`

##### Combinator DSL

The combinator DSL provides another, potentially more convenient, wrapper for
`Combinators.combine`.

Instead of a list of arguments, it uses kotlin's property delegates to refer to
the arbitraries that are being combined:

```kotlin
combine {
    val first by Arbitraries.strings()
    val second by Arbitraries.strings()
    // ...

    combineAs {
        "first: ${questionMark}first, second: ${questionMark}second"
    }
}
```

Note that accessing the `first` or `second` properties in the example above 
_outside_ the `combineAs` block would result in an error.

In the background, this is equivalent to:

```kt
combine(listOf(Arbitraries.strings(), Arbitraries.strings())) { values ->
    "first: ${questionMark}{values[0]}, second: ${questionMark}{values[1]}"
}
```

It is also possible to use filters within the combinator DSL:

```kotlin
combine {
    val first by Arbitraries.strings()
    val second by Arbitraries.strings()

    filter { first.isNotEmpty() }
    filter { first != second }

    combineAs {
        // 'first' will never be empty or equal to 'second'

        "first" + "second"
    }
}
```

And you can also achieve the `flatCombine` behaviour, by using `flatCombineAs`:

```kotlin
combine {
    val first by Arbitraries.strings()
    val second by Arbitraries.strings()
    // ...

    flatCombineAs {
        Arbitraries.just("first: ${questionMark}first, second: ${questionMark}second")
    }
}
```

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
  [Create an issue](https://github.com/jqwik-team/jqwik/issues/new) if that's important for you.

- Inline classes are handled like the class they inline.
  Default generation works and you can also use constraint annotations for the inlined class:

  ```kotlin
  @Property
  fun test2(@ForAll password: @StringLength(51) SecurePassword) {
      assert(password.length() == 51)
  }

  @JvmInline
  value class SecurePassword(private val s: String) {
      fun length() = s.length
  }
  ```
  
  However, if you build your own arbitraries for inline classes 
  you have to generate values of the _inlined class_ instead, 
  which would be `String` in the example above.
  [Create an issue](https://github.com/jqwik-team/jqwik/issues/new) if that bothers you too much.

- `anyForSubtypeOf<>()` does not work as expected when a sealed subtype requires 
  a concrete class to be created, which requires a sealed class or interface.
  The following example demonstrate the issue:

  ```kotlin
  sealed interface Character
  class Knight(val kingdom: Kingdom) : Character
  class Kingdom(val army: Army)
  sealed interface Army

  val arbitrary =  anyForSubtypeOf<Character>() // this arbitrary will fail during generation
  ```

  However, the workaround consist on the registration of an arbitrary dedicated
  to involved sealed class or interface, `Army` in the example above.