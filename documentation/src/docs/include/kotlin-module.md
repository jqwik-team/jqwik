This module's artefact name is `jqwik-Module`. 
It's supposed to simplify and streamline using _jqwik_ in Kotlin projects. 

This module is _not_ in jqwik's default dependencies. 
It's usually added as a test-implementation dependency.

The module provides:

- [Automatic generation of nullable types](#generation-of-nullable-types)
- [Convenience Functions for Kotlin](#convenience-functions-for-kotlin)

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

- Repeatable annotations do not work (yet) in Kotlin. 
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

  The one important exception is `@ForAll` which must always precede the parameter.

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

#### Convenience Functions for Kotlin

Some parts of the jqwik API are hard to use in Kotlin. 
That's why this module offers a few extension functions and top-level functions
to ease the pain.

##### Kotlin Extension Functions

- `Arbitrary.orNull(probability: Double) : T?` can replace `Arbitrary.injectNull(probabilit)`
  and returns a nullable type.

- `String.any()` can replace `Arbitraries.strings()`

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
      return combine(firstNames, lastNames) {first, last -> "$first $last" }
  }
  ```

- `flatCombine(a1: Arbitrary<T1>, ..., (v1: T1, ...) -> Arbitrary<R>)` can replace all
  variants of `Combinators.combine(a1, ...).flatAs((v1: T1, ...) -> Arbitrary<R>)`.


#### Quirks and Bugs

- Despite our best effort to enrich jqwik's Java API with nullability information,
  the derived Kotlin types are not always correct. 
  That means that you may run into `null` objects despite the type system showing non null types,
  or you may have to ignore Kotlin's warning about nullable types where in practice nulls are impossible.

- As of this writing Kotlin still has a few bugs when it comes to supporting Java annotations.
  That's why in some constellations you'll run into strange behaviour - usually runtime exceptions or ignored constraints - when using predefined jqwik annotations on types.
