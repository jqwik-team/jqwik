- ShrinkingSequence.andThen
  Change parameter to Function<FalsificationResult<T>, ShrinkingSequence<T>> createFollowupSequence

- Configuration:
  reportOnlyFailures = false

- PackageDescriptor e.g.
  @Group
  @Label("mypackage")
  @AddHook(...)
  in package-info.java

- Move release notes to their own web page

- ArchitectureTests: Use @ArchTest annotations