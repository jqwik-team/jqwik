-  Allow exhaustive generation
   - ExhaustiveGenerator implementations:
     - Combinators.combine
   - User Guide

- Release 0.9.0
    - Update Blog series to 0.9.0

- PackageDescriptor e.g.
  @Label("mypackage")
  @AddHook(...)
  class JqwikPackage {
    @Provide
    Arbitrary<MyType> myType() { ... }
  }
  