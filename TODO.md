-  Allow exhaustive generation
   - User Guide

- Release 0.9.0
    - Update Blog series to 0.9.0

- PackageDescriptor e.g.
  @Label("mypackage")
  @AddHook(...)
  class Package implements JqwikPackage {
    @Provide
    Arbitrary<MyType> myType() { ... }
  }
  