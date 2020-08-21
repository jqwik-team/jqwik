- 1.3.5

    - LazyOfArbitrary: Suppliers with bound variables should be reevaluated 
      when variables change (see binheap in shrinking challenge)

- 1.3.x

    - Bound shrinking: Use time bound instead of shrinking attempts bound

    - `@Repeat(42)`: Repeat a property 42 times

    - Lifecycle
        - PropertyExecutionResult
            originalSample()
            shrunkSample()

    - Introduce recursive use of Arbitraries.forType(Class<T> targetType)
        - forType(Class<T> targetType, int depth)
        - @UseType(depth = 1)

    - Implement grow() for more shrinkables
        - CombinedShrinkable: grow each leg
        - CollectShrinkable: grow each element

    - Add abstract method DomainContextBase.registrations()
    
    - Allow specification of provider class in `@ForAll` and `@From`
      see https://github.com/jlink/jqwik/issues/91

    - Use derived Random object for generation of each parameter.
      Will that somehow break a random byte provider in guided generation?
      - Remember the random seed in Shrinkable

    - Guided Generation
      https://github.com/jlink/jqwik/issues/84
      - Maybe change AroundTryHook to allow replacement of `Random` source
      - Or: Introduce ProvideGenerationSourceHook
      
    - Edge Cases
        - Stream edge cases on the fly instead of creating all upfront:
           - https://github.com/jlink/jqwik/issues/114
           - examples.bugs.JqwikHeapBust as test case
    
        - Arbitrary.withoutEdgeCases() 
            - should also work for individual generators
            - Maybe introduce ArbitraryDecorator or something like that
        
        - Arbitrary.addEdgeCase(value) 
            - Make shrinkable variants for
                - Numeric Arbitraries
                - CharacterArbitrary
                - Arbitrary.of() arbitraries
                - Collections
                - Combinators
            - Mixin edge cases in random order (https://github.com/jlink/jqwik/issues/101)

    - Arbitrary.uniqueBy(Predicate<T> uniqueCondition)
    

