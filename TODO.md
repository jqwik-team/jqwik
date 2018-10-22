- PackageDescriptor e.g.
  @Label("mypackage")
  @AddHook(...)
  class Package implements JqwikPackage {
    @Provide
    Arbitrary<MyType> myType() { ... }
  }
  