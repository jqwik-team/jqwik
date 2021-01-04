In addition to the usual randomized generation of property parameters you have also
the possibility to feed a property with preconceived or deterministically generated
parameter sets. Why would you want to do that? One reason might be that you are aware
of some problematic test cases but they are rare enough that _jqwik_'s randomization
strategies don't generate them (often enough). Another reason could be that you'd like
to feed some properties with prerecorded data - maybe even from production.
And last but not least there's a chance that you want to check for a concrete result
given a set of input parameters.

Feeding data into a property is quite simple:

```java
@Data
Iterable<Tuple2<Integer, String>> fizzBuzzExamples() {
    return Table.of(
        Tuple.of(1, "1"),
        Tuple.of(3, "Fizz"),
        Tuple.of(5, "Buzz"),
        Tuple.of(15, "FizzBuzz")
    );
}

@Property
@FromData("fizzBuzzExamples")
void fizzBuzzWorks(@ForAll int index, @ForAll String result) {
    Assertions.assertThat(fizzBuzz(index)).isEqualTo(result);
}
```

All you have to do is annotate the property method with
`@FromData("dataProviderReference")`. The method you reference must be
annotated with `@Data` and return an object of type `Iterable<? extends Tuple>`.
The [`Table` class](/docs/${docsVersion}/javadoc/net/jqwik/api/Table.html)
is just a convenient way to create such an object, but you can return
any collection or create an implementation of your own.

Keep in mind that the `Tuple` subtype you choose must conform to the
number of `@ForAll` parameters in your property method, e.g. `Tuple.Tuple3`
for a method with three parameters. Otherwise _jqwik_ will fail the property
and tell you that the provided data is inconsistent with the method's parameters.

Data points are fed to the property in their provided order.
The `tries` parameter of `@Property` will constrain the maximum data points
being tried.
Unlike parameterized tests in JUnit4 or Jupiter, _jqwik_ will report only the
first falsified data point. Thus, fixing the first failure might lead to another
falsified data point later on. There is also _no shrinking_ being done for data-driven
properties since _jqwik_ has no information about the constraints under which
the external data was conceived or generated.

