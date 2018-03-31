- Bug:
  ```
  @IntRange(min = 1, max = Integer.MAX_VALUE - 10) @ForAll int anInt
  ```
  will generate Integer.MAX_VALUE although it should not

- Bug:
  ```java
    @Property
    boolean rootOfSquareShouldBeOriginalValue(@Positive @ForAll int anInt) {
        int square = anInt * anInt;
        return Math.sqrt(square) == anInt;
    }
  ```
  results in stack overflow when shrinking

- Bug: Catch wildcard with lower bound
  jqwik should not generate objects for that.

- Javadoc:
  - ArbitraryConfigurator
  - Arbitraries methods
  - Arbitrary methods
  - Fluent configurator methods
