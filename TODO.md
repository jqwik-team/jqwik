- Bug: Stack overflow in
  ```
  	@Property
  	<T extends Comparable<T>> void aListOfIntegersCanBeSorted(@ForAll List<T> aList) {
  		Assertions.assertThat(sort(aList)).isNotNull();
  	}

  	private <T extends Comparable<? super T>> List<T> sort(List<T> original) {
  		List<T> clone = new ArrayList<>(original);
  		Collections.sort(clone);
  		return clone;
  	}
  ```

- PackageDescriptor e.g.
  @Label("mypackage")
  @AddHook(...)
  class Package implements JqwikPackage {
    @Provide
    Arbitrary<MyType> myType() { ... }
  }

- Make reporting configurable
  - Use System.out directly instead of using JUnit 5 reporter
  - jqwik.properties: useJunitPlatformReporter=false