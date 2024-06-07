# 1.9.0

    - Fix bug https://github.com/jqwik-team/jqwik/issues/577

    - Fix nullability annotation problem https://github.com/jqwik-team/jqwik/issues/575
      Maybe with 


# 1.9.x

    - Migrate to https://docs.gradle.org/8.7/userguide/jvm_test_suite_plugin.html
      as preparation for Gradle 9

    - Upgrade to Kotlin 2.0
        - Fix type problems from K2 compiler
        - Document differences between K1 and K2 in user guide

    - Summon preconfigured arbitrary. See https://github.com/jqwik-team/jqwik/issues/527

    - Using @UseType for sealed interfaces and classes: https://github.com/jqwik-team/jqwik/issues/523
      Will probably require a new module for java 17

    - Allow parallel test runs with SBT: https://github.com/jqwik-team/jqwik/issues/514

    - Method ordering. See https://github.com/jqwik-team/jqwik/issues/502.

    - State-based Properties (https://github.com/jqwik-team/jqwik/issues/428) : 
      - Do not shrink a single transformation if it is accessing state and any following transformation changes state.
      - ChainArbitrary.startWith(.., ChainConfig.of(isImmutable, hasSideEffects, comparator))

    - PropertyInfo: Provide PropertyInfo hook with info about the porperty's display name, class, method, tags etc.
      See Jupiter's TestInfo as an example.

    - Introduce ModelChain or other mechanism to simplify model-based comparison properties. 
      - Should cover https://github.com/jqwik-team/jqwik/issues/80.
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
      See https://github.com/jqwik-team/jqwik/issues/294 & SharedArbitraryExperiments
      This probably requires major refactoring and change of structure.

        - Allow @ForAll on member variables of test container class

