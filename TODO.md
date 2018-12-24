- For version 1.0

  - Warn if @Property method contains any direct annotation from package "org.junit.*"

  - *Range(min = ..) ohne max

  - `@Disable(value = "reason", until = "2020-12-24")` annotation
    Implement as new LifecycleHook

  - Review TODOs

  - Use apiguardian annotations

  - Use junit-platform-testkit for engine integration tests
    (iff junit 5.4 is out by then)