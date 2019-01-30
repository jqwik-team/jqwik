- Arbitraries.forType(Class<T> targetClass) : TypeArbitrary<T>

  - Shrinking does not work!

  - class TypeArbitrary<T>
      TypeArbitrary<T> useMutators(String setterMethods ...)
      TypeArbitrary<T> useFields(String fieldNames ...)

  - User guide entry

  - Annotation @Use({PUBLIC_CONSTRUCTORS, ALL_CONSTRUCTORS, PUBLIC_FACTORIES, ALL_FACTORIES}
