- Arbitraries.forType(Class<T> targetClass) : TypeArbitrary<T>
  - class TypeArbitrary<T>
      TypeArbitrary<T> useConstructors(CTor ...)
      TypeArbitrary<T> useCreators(Executable ...)
      TypeArbitrary<T> useMutators(String setterMethods ...)
      TypeArbitrary<T> useFields(String fieldNames ...)

- Introduce repeatable annotation `@Domain(Class<? extends DomainContext>`
  - Add user guide entry

- Add @API parts to generated javadoc
