- 1.2.7

    - Switching off warning when container is not a jqwik container:
    [class net.jqwik.spring.SimpleSpringJupiterTests] has annotation [@org.junit.jupiter.api.extension.ExtendWith(value=[class org.springframework.test.context.junit.jupiter.SpringExtension])] from JUnit which cannot be processed by jqwik

    - Improve Sample Reporting
      https://github.com/jlink/jqwik/issues/85

    - ProvideArbitraryHook
        - Let domains use that hook
        - Let ArbitraryProviders use that hook
    
    - Guided Generation
      https://github.com/jlink/jqwik/issues/84
      - Maybe change AroundTryHook to allow replacement of `Random` source
      
- 1.3.0

    - Introduce Arbitrary.edgeCases() and combinatorial execution of edge cases

    - Remove deprecated APIs
    
    - Make some experimental API "maintained"

    - Documentation for lifecycle hooks API in user guide

    - Allow include/exclude for decimal ranges, e.g.
      - BigDecimalArbitrary.within(Range.from(0.1, true, 10.0, false))
      - `BigRange.min/maxIncluded`
    
    - @ResolveParameter method
        - Returns `Optional<MyType>` | `Optional<ParameterSupplier<MyType>>`
        - Optional Parameters: TypeUsage, LifecycleContext
        - static and non-static

    - PerProperty.Lifecycle
        - void beforeTry(TryLifecycleContext, parameters)
        - void afterTry(TryLifecycleContext, TryExecutionResult)
        - void onSatisfiedTry()
        - TryExecutionResult onFalsifiedTry(TrExecutionResult)

    - Around property hooks
        - Get and set random seed

    - `@Report(reportOnlyFailures = false)`

