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


- The positioning of constraint annotations can be tricky. Whereas
  ```kotlin
  @Property
  fun test(@ForAll aList: List<@AlphaChars String>) { ... }
  ```
  will use only alphabetic characters for the generated list of Strings,
  ```kotlin
  @Property(tries = 100)
  fun test(@ForAll aString: @AlphaChars String) { ... }
  ```
  just ignores the `@AlphaChars` annotation. 
  Instead you have to do it that way:
  ```kotlin
  @Property(tries = 100)
  fun test(@ForAll @AlphaChars aString: String) { ... }
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
to ease the pain:

- `Arbitrary.orNull(probability: Double) : T?` returns a nullable type