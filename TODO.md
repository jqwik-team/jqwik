- Shrinking:

  ```
    class ShrinkingExampleProperties {

        static <E> List<E> brokenReverse(List<E> aList) {
            if (aList.size() < 4) {
                aList = new ArrayList<>(aList);
                reverse(aList);
            }
            return aList;
        }

        @Property(shrinking = ShrinkingMode.FULL)
        boolean reverseShouldSwapFirstAndLast(@ForAll List<Integer> aList) {
            Assume.that(!aList.isEmpty());
            List<Integer> reversed = brokenReverse(aList);
            return aList.get(0) == reversed.get(aList.size() - 1);
        }
    }
  ```

  Only shrinks to [0, 0, 500, 1]. Bug?

- User Guide
  - Add BOUNDED to shrinking section

- Refactor GenericTypeTests
  - More tests for creation from parameters with wildcards and type variables

- Javadoc:
  - ArbitraryConfigurator
  - Arbitraries methods
  - Arbitrary methods
  - Fluent configurator methods
