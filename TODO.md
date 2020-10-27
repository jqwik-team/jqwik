- 1.3.7

    - Reporting Display Bug:
      Generated Functions with same baseSeed should be equal to suppress
      After Execution reporting. Maybe comparison should only be made
      for objects with explicit implementation of equals()?

- 1.3.8

    - How can generated functions produce constant results even when unique
      arbitraries are involved?

    - Arbitraries.strings().emails() (https://github.com/jlink/jqwik/issues/127)

    - Arbitrary.unique() requires a scope
        - In container
        - In super container
        - In whole try
      Maybe a scope parameter together with Arbitrary.scope(name) can work?

    - Histograms: Make clustering of numerical values easy, e.g. by overriding a method
      called `Histogram.clusterSize()`.
    
    - Domains
        - Deprecate AbstractDomainContextBase
            - Introduce DomainContextBase
            - Allow @Provide methods in DomainContextBase subclasses
            - Allow @ForAll parameters in @Provide methods
            - Allow Arbitrary<T> parameters in @Provide methods
            - @Configure method for configurators?

    
- 1.3.x

    - Edge Cases
    
        - Restrict number of generated edge cases to number of tries
            - For embedded/individual use of generators only use a max of 100 edge cases
        
        - Arbitrary.withoutEdgeCases() 
            - should also work for embedded/individual generators
            - Maybe introduce ArbitraryDecorator or something like that
        
        - Arbitrary.addEdgeCase(value) 
            - Make shrinkable variants for
                - Numeric Arbitraries
                - CharacterArbitrary
                - Arbitrary.of() arbitraries
                - Collections
                - Combinators
            - Mixin edge cases in random order (https://github.com/jlink/jqwik/issues/101)

    - Arbitraries.forType(Class<T> targetType) or Beans.forType/from(...)
      https://github.com/jlink/jqwik/issues/121
        - useBeanProperties()
            - are considered nullable
            - with optional spec: Map<String, Arbitrary> to map
              a property to a certain arbitrary

    - Arbitraries.forType(Class<T> targetType)
        - Recursive use
            - forType(Class<T> targetType, int depth)
            - @UseType(depth = 1)

    - `@Repeat(42)`: Repeat a property 42 times

    - Implement grow() for more shrinkables
        - CombinedShrinkable: grow each leg
        - CollectShrinkable: grow each element

    - Allow specification of provider class in `@ForAll` and `@From`
      see https://github.com/jlink/jqwik/issues/91

    - Use derived Random object for generation of each parameter.
      Will that somehow break a random byte provider in guided generation?
      - Remember the random seed in Shrinkable

    - Guided Generation
      https://github.com/jlink/jqwik/issues/84
      - Maybe change AroundTryHook to allow replacement of `Random` source
      - Or: Introduce ProvideGenerationSourceHook
      
    - Arbitrary.uniqueBy(Predicate<T> uniqueCondition)
    

