- 1.3.9

    - Can values of Arbitraries.create() been cached so that they are not
      regenerated for reporting?

    - Adapt edge cases of email addresses to only generate the most important ones.
      See https://github.com/jlink/jqwik/issues/133

    - XRange constraints use filter when specialized arbitraries are not available

    - StringLength constraints uses filter when StringArbitrary not available

- 1.3.x

    - Add code of conduct. Examples:
        - https://github.com/apache/groovy/blob/master/CODE_OF_CONDUCT.md
        - https://github.com/junit-team/junit5/blob/main/CODE_OF_CONDUCT.md

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
        - Hand in property execution context to domains when being created.
          E.g. to get annotation values from method
          DomainContext.prepare(PropertyExecutionContext context)


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
      

