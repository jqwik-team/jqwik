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
EdgeCases[-2, -1, 0, 2, 1, -2147483648, 2147483647]
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
