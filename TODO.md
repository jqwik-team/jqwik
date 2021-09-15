- 1.5.6

    - Use AnnotationSupport.findAnnotation(Class,Class,SearchOption) in JqwikAnnotationSupport
  
- 1.5.x

    - Verify size settings for all Collection Arbitraries

    - Arbitraries.forType(Class<T> targetType)
      - Recursive use
        - forType(Class<T> targetType, int depth)
          - @UseType(depth = 1)
      - See https://github.com/jlink/jqwik/issues/191

    - Add capability to easily generate java beans with Builders 
      (if that really makes sense).
      ```
      Beans.ofType(...)
           .excludeProperties(...)
           .use().forProperties(...)
      ```
      
    - Time Module:
        - <timebased>Arbitrary.shrinkTowards(date|time|dateTime)
        
    - Web Module:
        - Web.ipv4Addresses()|ipv6Addresses()|urls()

    - EdgeCases.Configuration.withProbability(double injectProbability)

    - `@Repeat(42)`: Repeat a property 42 times

    - Allow specification of provider class in `@ForAll` and `@From`
      see https://github.com/jlink/jqwik/issues/91

    - Allow state machine / model specification with temporal logic.
      See https://wickstrom.tech/programming/2021/05/03/specifying-state-machines-with-temporal-logic.html

    - Guided Generation
      https://github.com/jlink/jqwik/issues/84
        - Maybe change AroundTryHook to allow replacement of `Random` source
        - Or: Introduce ProvideGenerationSourceHook

    - Allow to add frequency to chars for String and Character arbitraries eg.
      StringArbitrary.alpha(5).numeric(5).withChars("-", 1)
    

