- Bug: Does not shrink when exception is other than AssertionError

- Configuration:
  reportOnlyFailures = false

- PackageDescriptor e.g.
  @Group
  @Label("mypackage")
  @AddHook(...)
  in package-info.java

- Move release notes to their own web page

- ArchitectureTests: Use @ArchTest annotations