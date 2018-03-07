- Arbitrary.filter can result in endless loop if the filter criterion never matches.
  Stop trying after 1000? attempts

- Arbitraries.frequency: pairs of (frequency, value)

- Warn (or skip? or fail?) if jqwik tests are annotated with Jupiter/JUnit4 annotations

- Allow custom names for containers and test methods: @Label?
  - Issue error when @DisplayName is used

- Document configurators

- Arbitrary provider for @Functional interfaces
