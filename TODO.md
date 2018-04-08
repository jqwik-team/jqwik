- Add Shrinking.BOUNDED
  - Hand in ShrinkingMode to ValueShrinker
  - Introduce counter in ValueShrinker
  - Report counter / Shrinking-Cutoff
  - Make BOUNDED the default
  - Document in User Guide

- Bug: Catch wildcard with lower bound
  jqwik should not generate objects for that.

- Javadoc:
  - ArbitraryConfigurator
  - Arbitraries methods
  - Arbitrary methods
  - Fluent configurator methods
