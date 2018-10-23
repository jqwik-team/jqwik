- PackageDescriptor e.g.
  @Label("mypackage")
  @AddHook(...)
  class Package implements JqwikPackage {
    @Provide
    Arbitrary<MyType> myType() { ... }
  }

- Make reporting configurable
  - Use System.out directly instead of using JUnit 5 reporter
  - jqwik.properties: useJunitPlatformReporter=false