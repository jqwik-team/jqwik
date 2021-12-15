Sometimes it is possible to run a property method with all possible value combinations.
Consider the following example:

```java
@Property
boolean allSquaresOnChessBoardExist(
    @ForAll @CharRange(from = 'a', to = 'h') char column,
    @ForAll @CharRange(from = '1', to = '8') char row
) {
    return new ChessBoard().square(column, row).isOnBoard();
}
```

The property is supposed to check that all valid squares in chess are present
on a new chess board. If _jqwik_ generated the values for `column` and `row`
randomly, 1000 tries might or might not produce all 64 different combinations.
Why not change strategies in cases like that and just iterate through all
possible values?

This is exactly what _jqwik_ will do:
- As long as it can figure out that the maximum number of possible values
  is equal or below a property's `tries` attribute (1000 by default),
  all combinations will be generated.
- You can also enforce an exhaustive or randomized generation mode by using the
  [Property.generation attribute](#optional-property-attributes).
  The default generation mode can be set in the [configuration file](#jqwik-configuration).
- If _jqwik_ cannot figure out how to do exhaustive generation for one of the
  participating arbitraries it will switch to randomized generation if in auto mode
  or throw an exception if in exhaustive mode.

Exhaustive generation is considered for:
- All integral types
- Characters and chars
- Enums
- Booleans
- Strings
- Fixed number of choices given by `Arbitraries.of()`
- Fixed number of choices given by `Arbitraries.shuffle()`
- Lists, sets, streams, optionals and maps of the above
- Combinations of the above using `Combinators.combine()`
- Mapped arbitraries using `Arbitrary.map()`
- Filtered arbitraries using `Arbitrary.filter()`
- Flat mapped arbitraries using `Arbitrary.flatMap()`
- And a few other derived arbitraries...

