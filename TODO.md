- 1.3.0
    - Fix JDK >= 11 bug:
    
    ```
    TypeUsage > TypeUsageImpl.forParameter() > threeGenericParametersAndAnnotation STANDARD_OUT
    
        timestamp = 2020-05-19T14:33:22.581664, TypeUsageImpl.forParameter():threeGenericParametersAndAnnotation = 
    
        org.opentest4j.AssertionFailedError: 
    
        Expecting:
    
         <"@net.jqwik.api.ForAll(value="") Tuple3<BigInteger, BigInteger, BigInteger>">
    
        to be equal to:
    
         <"@net.jqwik.api.ForAll(value=) Tuple3<BigInteger, BigInteger, BigInteger>">
    
        but was not.
    
                                      |-------------------jqwik-------------------
    
        tries = 1                     | # of calls to property
    
        checks = 1                    | # of not rejected calls
    
        generation = RANDOMIZED       | parameters are randomly generated
    
        after-failure = PREVIOUS_SEED | use the previous seed
    
        edge-cases#mode = FIRST       | edge cases are generated first
    
        edge-cases#total = 1          | # of all combined edge cases
    
        edge-cases#tried = 0          | # of edge cases tried in current run
    
        seed = -6442187850587945264   | random seed to reproduce generated values
    
    TypeUsage > TypeUsageImpl.forParameter() > threeGenericParametersAndAnnotation FAILED
    
        org.opentest4j.AssertionFailedError: 
    
        Expecting:
    
         <"@net.jqwik.api.ForAll(value="") Tuple3<BigInteger, BigInteger, BigInteger>">
    
        to be equal to:
    
         <"@net.jqwik.api.ForAll(value=) Tuple3<BigInteger, BigInteger, BigInteger>">
    
        but was not.
    
            at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    
            at java.base/jdk.`internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
    
            at java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    
            at net.jqwik.api.providers.TypeUsageTests$ForParameter.threeGenericParametersAndAnnotation(TypeUsageTests.java:184)
    ``

    - Documentation for lifecycle hooks API in user guide
    
- 1.3.x

    - Improve Sample Reporting
      https://github.com/jlink/jqwik/issues/85
      - Extract description before running the property?
        e.g. to be able to display a generated Stream
        
    - Does exhaustive generation for mutable objects work or will muted objects be injected
      in other parameter sets?

    - Edge Cases
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

    - Change signature Arbitrary.exhaustive() -> ExhaustiveGenerator
    
    - Property runtime statistics (https://github.com/jlink/jqwik/issues/100)

    - ProvideArbitraryHook
        - Let domains use that hook
        - Let ArbitraryProviders use that hook
        
    - AroundPropertyHook
        - Add parameter PropertyConfiguration
            - tries()
            - afterFailureMode()
            - generationMode()
            - shrinkingMode()
            - randomSeed()
        - Allow configuration attributes to be changed
        - Alternative: Introduce PropertyConfigurationHook
    
    - Support more RandomDistribution modes, e.g. Gaussian, Log, PowerLaw
        https://en.wikipedia.org/wiki/Inverse_transform_sampling
        https://en.wikipedia.org/wiki/Ziggurat_algorithm
        https://github.com/jeffhain/jafaran/blob/master/src/main/java/net/jafaran/Ziggurat.java

    - Guided Generation
      https://github.com/jlink/jqwik/issues/84
      - Maybe change AroundTryHook to allow replacement of `Random` source
      - Or: Introduce ProvideGenerationSourceHook
      
    - @ResolveParameter method
        - Returns `Optional<MyType>` | `Optional<ParameterSupplier<MyType>>`
        - Optional Parameters: TypeUsage, LifecycleContext
        - static and non-static

    - PerProperty.Lifecycle
        - void beforeTry(TryLifecycleContext, parameters)
        - void afterTry(TryLifecycleContext, TryExecutionResult)
        - void onSatisfiedTry()
        - TryExecutionResult onFalsifiedTry(TryExecutionResult)

    - `@Report(reportOnlyFailures = false)`

    - @StatisticsReportFormat
        - label=<statistics label> to specify for which statistics to use
        - Make it repeatable
