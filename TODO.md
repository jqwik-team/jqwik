- Bug in test resolution: Every property method appears twice in
  PropertyMethodResolver.createTestDescriptor

- Property(afterFailure = AfterFailureMode.SAMPLE_ONLY|PREVIOUS_SEED|RANDOM_SEED)
  - User guide entry for PREVIOUS_SEED
  - report afterFailureMode

- For version 1.0

  - Divide deliverables into two artifacts:
    - jqwik.api
    - jqwik.engine

  - `@Disabled("reason")` annotation

  - Move release notes to their own web page
    Alternative: Use asciidoc to generate user guide

  - Use apiguardian annotations (starting version 1.0)

