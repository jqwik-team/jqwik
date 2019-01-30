- Arbitraries.forType(Class<T> targetClass) : TypeArbitrary<T>
  - class TypeArbitrary<T>
      TypeArbitrary<T> useConstructors(CTor ...)
      TypeArbitrary<T> useCreators(Executable ...)
      TypeArbitrary<T> useMutators(String setterMethods ...)
      TypeArbitrary<T> useFields(String fieldNames ...)

  - Annotation @Use({PUBLIC_CONSTRUCTORS, ALL_CONSTRUCTORS, PUBLIC_FACTORIES, ALL_FACTORIES}
