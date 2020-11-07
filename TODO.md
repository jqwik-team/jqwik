- 1.3.8

    - Arbitraries.strings().emails() (https://github.com/jlink/jqwik/issues/127)

    - Give weights/frequency in StringArbitrary.alpha() etc. by number of allowed chars
    
- 1.3.x

    - Add Arbitrary.sampleStream(DomainContext ... contexts) for sampling
      from given contexts.
        - Also Arbitrary.sample(DomainContext ... contexts)

    - Deprecate Arbitrary.unique()
    
      Instead make something like List|Set|ArrayArbitrary.constraint(
        list, element -> !list.contains(element);
      ) 
        - ListArbitrary.uniqueElements()
        - ListArbitrary.uniqueElementsBy(Predicate<E> uniqueCondition)
        - How can that work across collections?

    - Allow to add frequency to chars for String and Character arbitraries
      eg. StringArbitrary.alpha(5).numeric(5).withChars("-", 1)

    - Domains
        - Deprecate AbstractDomainContextBase
            - Introduce DomainContextBase
            - Allow @Provide methods in DomainContextBase subclasses
            - Allow @ForAll parameters in @Provide methods
            - Allow Arbitrary<T> parameters in @Provide methods
            - @Configure method for configurators?

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
      

