- Bug
  ```
    @Property(seed = "-6868766892804735822", shrinking = ShrinkingMode.FULL)
    boolean shouldShrinkTo101(@ForAll("numberStrings") String aNumberString) {
        return Integer.parseInt(aNumberString) % 2 == 0;
    }

    @Provide
    Arbitrary<String> numberStrings() {
        return Arbitraries.integers().between(100, 1000).map(String::valueOf);
    }
  ```
  Should shrink but doesn't

- User Guide
  - Add BOUNDED to shrinking section

- Refactor GenericTypeTests
  - More tests for creation from parameters with wildcards and type variables

- Javadoc:
  - ArbitraryConfigurator
  - Arbitraries methods
  - Arbitrary methods
  - Fluent configurator methods
