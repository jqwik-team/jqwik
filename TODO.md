- Shrinking-Bug

  ```
  	private <T> List<T> reverse(List<T> original) {
  		List<T> clone = new ArrayList<>(new HashSet<>(original));
  		Collections.reverse(clone);
  		return clone;
  	}

  	@Property
  	boolean sizeRemainsTheSame(@ForAll List<Integer> original) {
  		List<Integer> reversed = reverse(original);
  		return original.size() == reversed.size();
  	}
  ```

   should shrink to [0,0] but does not.

- PackageDescriptor e.g.
  @Label("mypackage")
  @AddHook(...)
  class Package implements JqwikPackage {
    @Provide
    Arbitrary<MyType> myType() { ... }
  }
  