- Arbitrary.unique()
  - @Unique constraint

- Combinators.flatCombine
    ```java
        private Arbitrary<String> flatCombine(Arbitrary<String> key, Arbitrary<String> value, Combinators.F2<String, String, Arbitrary<String>> combinator) {
            return key.flatMap(k -> value.flatMap( v -> combinator.apply(k, v)));
        }
    ```

- Lifecycle Hooks
  - AroundAllHook
  - AroundContainerHook
  - AroundTryHook

- Lifecycle Tests
