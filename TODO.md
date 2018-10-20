-  Allow exhaustive generation
   - ExhaustiveGenerator implementations:
     - Combinators.combine4
     - Combinators.combine5
     - Combinators.combine6
     - Combinators.combine7
     - Combinators.combine8
     - Combinators.combine(List)
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
  