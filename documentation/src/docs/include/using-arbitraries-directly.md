Most of the time arbitraries are used indirectly, i.e. _jqwik_ uses them under
the hood to inject generated values as parameters. There are situations, though,
in which you might want to generate values directly.

### Generating a Single Value

Getting a single random value out of an arbitrary is easy and can be done
with [`Arbitrary.sample()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#sample()):

```java
Arbitrary<String> strings = Arbitraries.of("string1", "string2", "string3");
String aString = strings.sample();
assertThat(aString).isIn("string1", "string2", "string3");
```

Among other things, this allows you to use jqwik's generation functionality
with other test engines like Jupiter.
Mind that _jqwik_ uses a default `genSize` of 1000 under the hood and that
the `Random` object will be either taken from the current property's context or
freshly instantiated if used outside a property.

### Generating a Stream of Values

Getting a stream of generated values is just as easy with [`Arbitrary.sampleStream()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#sampleStream()):

```java
List<String> values = Arrays.asList("string1", "string2", "string3");
Arbitrary<String> strings = Arbitraries.of(values);
Stream<String> streamOfStrings = strings.sampleStream().limit(100);

assertThat(streamOfStrings).allMatch(values::contains);
```

### Generating all possible values

There are a few cases when you don't want to generate individual values from an
arbitrary but use all possible values to construct another arbitrary. This can be achieved through
[`Arbitrary.allValues()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#allValues()).

Return type is `Optional<Stream<T>>` because _jqwik_ can only perform this task if
[exhaustive generation](#exhaustive-generation) is doable.


### Iterating through all possible values

You can also use an arbitrary to iterate through all values it specifies.
Use
[`Arbitrary.forEachValue(Consumer action)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#forEachValue(java.util.function.Consumer)).
for that purpose. This only works when [exhaustive generation](#exhaustive-generation) is possible.
In other cases the attempt to iterate will result in an exception.

This is typically useful when your test requires to assert some fact for all
values of a given (sub)set of objects. Here's a contrived example:

```java
@Property
void canPressAnyKeyOnKeyboard(@ForAll Keyboard keyboard, @ForAll Key key) {
    keyboard.press(key);
    assertThat(keyboard.isPressed(key));

    Arbitrary<Key> unpressedKeys = Arbitraries.of(keyboard.allKeys()).filter(k -> !k.equals(key));
    unpressedKeys.forEachValue(k -> assertThat(keyboard.isPressed(k)).isFalse());
}
```

In this example a simple for loop over `allKeys()` would also work. In more complicated scenarios
_jqwik_ will do all the combinations and filtering for you.



