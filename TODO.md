- Arbitraries.forType(Class<T> targetClass) : TypeArbitrary<T>
  - class TypeArbitrary<T>
      TypeArbitrary<T> useConstructors(CTor ...)
      TypeArbitrary<T> useCreators(Executable ...)
      TypeArbitrary<T> useMutators(String setterMethods ...)
      TypeArbitrary<T> useFields(String setterMethods ...)

- Introduce repeatable annotation `@Domain(Class<? extends DomainContext>`
  `DomainContext` classes should collect all arbitrary providers and arbitrary configurators
  used in a property method. Default is a global context.