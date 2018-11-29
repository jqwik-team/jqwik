- Property(afterFailure = AfterFailureMode.SAMPLE_ONLY|PREVIOUS_SEED|RANDOM_SEED)
  - User guide entry for PREVIOUS_SEED
  - report afterFailureMode
  - If property succeeds with sample -> Run again with new seed

- For version 1.0

  - Divide deliverables into two artifacts:
    - jqwik.api
    - jqwik.engine

  - `@Disabled("reason")` annotation

  - Move release notes to their own web page
    Alternative: Use asciidoc to generate user guide

  - Use apiguardian annotations (starting version 1.0)

  - Use junit-platform-testkit for engine integration tests