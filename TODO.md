- Add Shrinking.BOUNDED
  - Configuration.MAX_SHRINKING_DEPTH
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
