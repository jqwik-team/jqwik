# 1.7.3

    - State-based Properties (https://github.com/jlink/jqwik/issues/428) : 
      - Do not shrink a single transformation if it is accessing state and any following transformation changes state.
      - ChainArbitrary.startWith(.., ChainConfig.of(isImmutable, hasSideEffects, comparator))

    - Fix shrink and grow bug: https://github.com/jlink/jqwik/issues/439

    - Allow annotation @BeforeTry on member variables of tests to reinitialize them before each try.
      - Alternative: New annotation @InitBeforeTry


# 1.7.x

    - PropertyInfo: Provide PropertyInfo hook with info about the porperty's display name, class, method, tags etc.
      See Jupiter's TestInfo as an example.

    - Introduce ModelChain or other mechanism to simplify model-based comparison properties. 
      - Should cover https://github.com/jlink/jqwik/issues/80.
      - Maybe ModelChain can be fully generated before it's provided as parameter? This could enable repeatability of shrinked samples.
      - See example in https://github.com/jlink/model-based-testing/tree/jqwik170/src/test/java/mbt/tecoc/withModelChain

    - JqwikSession:
      - setRandomSessionSeed(), getRandomSessionSeed()

    - Allow state machine / model specification with temporal logic.
      See https://wickstrom.tech/programming/2021/05/03/specifying-state-machines-with-temporal-logic.html

    - NullableArbitraryProvider should always be last to apply.
      This will probably require a new parameter based lifecycle hook, similar to:

      ```
      interface WrapArbitrary extends Lifecycle Hook {
        <T> Arbitrary<T> wrap(ForAllParameterContext parameterContext, Arbitrary<T> arbitrary);
      }
      ```

    - <T,S,U> SetArbitrary<E>.combineEach(Arbitrary<S>).as(BiFunction<T, E, U>): Arbitrary<Set<U>>
      - For all collection arbitraries

    - SharedArbitrary
      See https://github.com/jlink/jqwik/issues/294 & SharedArbitraryExperiments
      This probably requires major refactoring and change of structure.

        - Allow @ForAll on member variables of test container class


# 1.8.0

    - Include compileOnly("org.jetbrains:annotations:23.0.0") into API?
