- More than one fitting registered provider
  - Fix one of all _per check_ of property
  - Fix same type variable to same provider
  - Wildcards and Type Variables: Without borders always use WildcardArbitrary

- Introduce ArbitraryProvider.priority()
  - Default priority = 0
  - Only return arbitraries of highest found priority

- Switch ArbitraryProvider(s) to use non-deprecated method

- Arbitraries.allDefaultsFor(Class<?> targetClass, Class<?> ... typeParameters)

- Javadoc:
  - ArbitraryConfigurator
  - Arbitrary methods
  - Fluent configurator methods
