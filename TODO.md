- Stateless Testing
    - Let action generation access the model state?
      E.g. to use a name that’s already been added to a store.
    - Special support for FSMs (finite state machines)
    - Parallel execution of action sequences (see Proper book)

- Property(onFailure = FailureMode.SAMPLE_ONLY|LAST_SEED|RANDOM_SEED)
  - jqwik.properties: defaultOnFailure: SAMPLE_ONLY

- Move release notes to their own web page

- Use apiguardian annotations (starting version 1.0)

