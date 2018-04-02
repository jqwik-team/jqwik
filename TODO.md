- Add Shrinking.BOUNDED
  - Use maxShrinkingDepth in ValueShrinker
  - Make BOUNDED the default
  - Make configurable in jqwik.properties
  - Document in User Guide

- Bug: Catch wildcard with lower bound
  jqwik should not generate objects for that.

- Javadoc:
  - ArbitraryConfigurator
  - Arbitraries methods
  - Arbitrary methods
  - Fluent configurator methods
