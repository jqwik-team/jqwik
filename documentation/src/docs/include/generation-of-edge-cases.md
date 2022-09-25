It's well-known that many programming bugs and specification gaps happen at the border
of allowed value ranges. For example, in the domain of integer numbers the minimum
(`Integer.MIN_VALUE`) and maximum (`Integer.MAX_VALUE`) belong in the set of those
[_edge cases_](https://en.wikipedia.org/wiki/Edge_case). Many people use the term a bit
more loosely and also include other special values that tend to have a higher chance
of revealing implementation problems, like `0` for numbers or an empty string.

_jqwik_ has special treatment for edge cases. Most base type arbitraries come with
their set of edge cases. You can find out about edge cases by asking an arbitrary
about it. Run the following example

```java
@Example
void printEdgeCases() {
    System.out.println(Arbitraries.integers().edgeCases());
    System.out.println(Arbitraries.strings().withCharRange('a', 'z').edgeCases());
    System.out.println(Arbitraries.floats().list().edgeCases());
}
```

and you will see this output:

```
EdgeCases[-2, -1, 0, 2, 1, -2147483648, 2147483647, 2147483647, 2147483646]
EdgeCases["a", "z", ""]
EdgeCases[[], [0.0], [1.0], [-1.0], [0.01], [-0.01], [-3.4028235E38], [3.4028235E38]]
```

You may notice that edge cases are not just hard-coded values but also make use
of underlying arbitraries' edge cases to arrive at new ones.
That's why a list of floats arbitrary has single element lists of floats as edge cases.
Edge cases are also being combined and permuted when
[`Combinators`](#combining-arbitraries) are used.
Also, most methods from `Arbitrary` - like `map()`, `filter()` and `flatMap()` - provide
sensible edge cases behaviour.
Thus, your self-made domain-specific arbitraries get edge cases automatically.

_jqwik_ makes use of edge cases in two ways:

1. Whenever an arbitrary is asked to produce a value it will mix-in edge cases
   from time to time.
2. By default jqwik will mix the _combination of permutations of edge cases_
   of a property's parameters with purely randomized generation of parameters.
   You can even try all edge case combinations first as the next property shows.

```java
@Property(edgeCases = EdgeCasesMode.FIRST)
void combinedEdgeCasesOfTwoParameters(
    @ForAll List<Integer> intList,
    @ForAll @IntRange(min = -100, max = 0) int anInt
) {
    String parameters = String.format("%s, %s", intList, anInt);
    System.out.println(parameters);
}
```

Run it and have a look at the output.

### Configuring Edge Case Injection

How jqwik handles edge cases generation can be controlled with
[an annotation property](#optional-property-attributes) and
[a configuration parameter](#jqwik-configuration).

To switch it off for a single property, use:

```java
@Property(edgeCases = EdgeCasesMode.NONE)
void combinedEdgeCasesOfTwoParameters(
    @ForAll List<Integer> intList,
    @ForAll @IntRange(min = -100, max = 0) int anInt
) {
    // whatever you do   
}
```

If you want to suppress edge case generation for a single arbitrary that's also possible:
Just use `Arbitrary.withoutEdgeCases()`. 
Running the following property will regularly create empty lists - because - this is one
of the default list edge cases, but it will not create integer values of `0`, `1`, `-1` etc.
with higher probability.

```java
@Property
void noNumberEdgeCases(@ForAll List<@From("withoutEdgeCases") Integer> intList) {
  System.out.println(intList);
}

@Provide
Arbitrary<Integer> withoutEdgeCases() {
  return Arbitraries.integers().withoutEdgeCases();}
```

### Configuring Edge Cases Themselves

Besides switching edge cases completely off, you can also filter some edge cases out,
include only certain ones or add new ones. 
This is done through [`Arbitrary.edgeCases(config)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#edgeCases(java.util.function.Consumer)). 
Here's an example that shows how to add a few "special" strings to a generator:

```java
@Property(edgeCases = EdgeCasesMode.FIRST)
void stringsWithSpecialEdgeCases(@ForAll("withSpecialEdgeCases") String aString) {
  System.out.println(aString);
}

@Provide
Arbitrary<String> withSpecialEdgeCases() {
  return Arbitraries.strings()
          .alpha().ofMinLength(1).ofMaxLength(10)
          .edgeCases(stringConfig -> {
            stringConfig.add("hello", "hallo", "hi");
          });
}
```

The output should start with:
```
A
Z
a
z
hello
hallo
hi
```
followed by a lot of random strings.

Mind that the additional edge cases - in this case `"hello"`, `"hallo"` and `"hi"` - 
are within the range of the underlying arbitrary.
That means that they could also be generated randomly, 
albeit with a much lower probability.

_**Caveat**_: Values that are outside the range of the underlying arbitrary 
_are not allowed_ as edge cases. 
For implementation reasons, arbitraries cannot warn you about forbidden values,
and the resulting behaviour is undefined.

If you want to add values that from outside the range of the underlying arbitrary,
use  `Arbitraries.oneOf()` - and maybe combine it with explicit edge case configuration:

```java
@Provide
Arbitrary<String> withSpecialEdgeCases() {
    StringArbitrary strings = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10);
    return Arbitraries.oneOf(strings, Arbitraries.of("", "0", "1"))
                      .edgeCases(config -> config.add("", "0", "1")); // <-- To really raise the probability of these edge cases
}
```