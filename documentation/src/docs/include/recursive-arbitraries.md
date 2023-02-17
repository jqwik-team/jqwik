Sometimes it seems like a good idea to compose arbitraries and thereby
recursively calling an arbitrary creation method. Generating recursive data types
is one application field but you can also use it for other stuff.

### Probabilistic Recursion

Look at the
[following example](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/RecursiveExamples.java)
which generates sentences by recursively adding words to a sentence:

```java
@Property
@Report(Reporting.GENERATED)
boolean sentencesEndWithAPoint(@ForAll("sentences") String aSentence) {
	return aSentence.endsWith(".");
	// return !aSentence.contains("x"); // using this condition instead 
	                                    // should shrink to "AAAAx."
}

@Provide
Arbitrary<String> sentences() {
	return Arbitraries.lazyOf(
		() -> word().map(w -> w + "."),
		this::sentence,
		this::sentence,
		this::sentence
	);
}

private Arbitrary<String> sentence() {
	return Combinators.combine(sentences(), word())
					  .as((s, w) -> w + " " + s);
}

private StringArbitrary word() {
    return Arbitraries.strings().alpha().ofLength(5);
}
```

There are two things to which you must pay attention:

- It is important to use 
  [`lazyOf(suppliers)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#lazyOf(java.util.function.Supplier,java.util.function.Supplier...))
  instead of the seemingly simpler 
  [`oneOf(arbitraries)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#oneOf(net.jqwik.api.Arbitrary,net.jqwik.api.Arbitrary...)).
  Otherwise _jqwik_'s attempt to build the arbitrary would result in a stack overflow.

- Every recursion needs one or more base cases in order to stop recursion at some point.
  Here, the base case is `() -> word().map(w -> w + ".")`.
  Base cases must have a high enough probability,
  otherwise a stack overflow will get you during value generation.

- The supplier `() -> sentence` is used three times to raise its probability
  and thus create longer sentences.

There is also a caveat of which you should be aware:
Never use this construct if suppliers make use of variable state
like method parameters or changing instance members.
In those cases use [`lazy()`](#using-lazy-instead-of-lazyof) as explained below.

#### Using lazy() instead of lazyOf()

There is an _almost equivalent_ variant to the example above:

```java
@Property
boolean sentencesEndWithAPoint(@ForAll("sentences") String aSentence) {
    return aSentence.endsWith(".");
}

@Provide
Arbitrary<String> sentences() {
    Arbitrary<String> sentence = Combinators.combine(
        Arbitraries.lazy(this::sentences),
        word()
    ).as((s, w) -> w + " " + s);

    return Arbitraries.oneOf(
        word().map(w -> w + "."),
        sentence,
        sentence,
        sentence
    );
}

private StringArbitrary word() {
    return Arbitraries.strings().alpha().ofLength(5);
}
``` 

The disadvantage of `lazy()` combined with `oneOf()` or `frequencyOf()`
is its worse shrinking behaviour compared to `lazyOf()`.
Therefore, choose `lazyOf()` whenever you can.

### Deterministic Recursion

An alternative to probabilistic recursion shown above, is to use deterministic
recursion with a counter to determine the base case. If you then use an arbitrary value
for the counter, the generated sentences will be very similar, and you can often forgo
using `Arbitraries.lazyOf()` or `Arbitraries.lazy()`:

```java
@Property
boolean sentencesEndWithAPoint(@ForAll("deterministic") String aSentence) {
    return aSentence.endsWith(".");
}

@Provide
Arbitrary<String> deterministic() {
    Arbitrary<Integer> length = Arbitraries.integers().between(0, 10);
    Arbitrary<String> lastWord = word().map(w -> w + ".");
    return length.flatMap(l -> deterministic(l, lastWord));
}

@Provide
Arbitrary<String> deterministic(int length, Arbitrary<String> sentence) {
    if (length == 0) {
        return sentence;
    }
    Arbitrary<String> more = Combinators.combine(word(), sentence).as((w, s) -> w + " " + s);
    return deterministic(length - 1, more);
}
```

### Deterministic Recursion with `recursive()`

To further simplify this _jqwik_ provides two helper functions:
- [`Arbitraries.recursive(..., depth)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#recursive(java.util.function.Supplier,java.util.function.Function,int)).
- [`Arbitraries.recursive(..., minDepth, maxDepth)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#recursive(java.util.function.Supplier,java.util.function.Function,int,int)).
Using the latter further simplifies the example:

```java
@Property
boolean sentencesEndWithAPoint(@ForAll("deterministic") String aSentence) {
    return aSentence.endsWith(".");
}

@Provide
Arbitrary<String> deterministic() {
	Arbitrary<String> lastWord = word().map(w -> w + ".");

	return Arbitraries.recursive(
		() -> lastWord,
		this::prependWord,
		0, 10
	);
}

private Arbitrary<String> prependWord(Arbitrary<String> sentence) {
    return Combinators.combine(word(), sentence).as((w, s) -> w + " " + s);
}
```
