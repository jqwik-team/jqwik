- Additional edges cases for integral generation:
  -5 .. +5, -10, +10

- Stateless Testing
    - Change Action Interface to split state change and change to system under tests:
      This would allow sequence generation to consider preconditions.
      Probably requires a new class like Model<T> with T is type of SUT.
      In that case shrinking must consider preconditions!
    - Let action generation access the model state?
      E.g. to use a name that’s already been added to a store.
    - Special support for FSMs (finite state machines)
    - Parallel execution of action sequences (see Proper book)
    - Make ActionSequenceArbitrary a SizableArbitrary to enable annotation @Size
      iff that still makes sense after the changes from above

- Property(onFailure = FailureMode.SAMPLE_ONLY|LAST_SEED|RANDOM_SEED)
  - jqwik.properties: defaultOnFailure: SAMPLE_ONLY

- Move release notes to their own web page

- Use apiguardian annotations (starting version 1.0)

