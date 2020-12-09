- 1.4.0

    - Review EXPERIMENTAL APIs and promote some to MAINTAINED    

    - Fill in some gaps in Javadoc (e.g. Combinators)
    
    - Email generation
        - Arbitraries.emails() -> net.jqwik.api.web.Emails.emails() 
        - @Email -> net.jqwik.api.web.@Email
        - Emails.localPart(String) 
        - Emails.host(String) 

    - Add code of conduct. Examples:
        - https://github.com/apache/groovy/blob/master/CODE_OF_CONDUCT.md
        - https://github.com/junit-team/junit5/blob/main/CODE_OF_CONDUCT.md

    - Edge Cases

        - Restrict number of generated edge cases to number of tries
          - For embedded/individual use of generators only use a max of 100 edge cases
        
        - Arbitrary.edgeCases(Consumer<Config>):
          - Special handling possible? for:
              - Numeric Arbitraries
              - CharacterArbitrary
              - Arbitrary.of() arbitraries
              - Collections
              - Combinators
        
        - Mixin edge cases in random order (https://github.com/jlink/jqwik/issues/101)

    - Deprecate Arbitrary.unique()
    
      Instead make something like List|Set|ArrayArbitrary.constraint(
        list, element -> !list.contains(element);
      ) 
        - ListArbitrary.uniqueElements()
        - ListArbitrary.uniqueElementsBy(Predicate<E> uniqueCondition)
        - How can that work across collections?

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

    - Use JUnit Configuration Parameters Mechanism
      https://github.com/jlink/jqwik/issues/139

- 1.4.x

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
      
    - Allow to add frequency to chars for String and Character arbitraries
      eg. StringArbitrary.alpha(5).numeric(5).withChars("-", 1)


