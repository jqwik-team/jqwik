- Combinators.combine(List<Arbitrary<T>> arbitraries)
    .as(List<T> values -> Something): Arbitrary<Something>

- Reimplement Shrinking
  - Consider Filtering and assumptions in all shrink attempts
    see: examples.doc.ShrinkingFilterExamples.shouldShrinkToBAH_butDoesNot
    see: examples.doc.ShrinkingFilterExamples.withAssumption_shouldShrinkToCCH_butDoesNotShrinkAtAll
  - ShrinkingMode.BOUNDED should also stop in case of deep search while filtered shrinking

- Refactor GenericTypeTests
  - More tests for creation from parameters with wildcards and type variables

- Javadoc:
  - ArbitraryConfigurator
  - Arbitraries methods
  - Arbitrary methods
  - Fluent configurator methods
