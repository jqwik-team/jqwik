- 1.3.6

    - Shrink nullable generators to null if possible
    
    - Does ComposableBuilder require flatMapping at all?
      Why not use Combinators.combine(List<Arbitrary<T>> listOfArbitraries)
      which would get rid of the combinatorial shrinking and edge case explosion. 
    
    - Edge Cases
    
        - Warning "WARNING: Combinatorial explosion of edge case generation. Stopped creating more after 10000 generated cases."
          - should only appear ONCE per property run
          - if possible contain more info about arbitrary/generator for which it occurs
    
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
    

- 1.3.x

    - Add abstract method DomainContextBase.registrations()
    
    - Arbitraries.forType(Class<T> targetType)
        - useBeanProperties()
            - with optional spec: Map<String, Arbitrary> to map
              a property to a certain arbitrary
        - Preconfigure certain params (by type, by name)
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
    

