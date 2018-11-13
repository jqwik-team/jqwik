- PackageDescriptor e.g.
  @Group
  @Label("mypackage")
  @AddHook(...)
  in package-info.java

- Move release notes to their own web page

- Expose failing sample as sub test

- Automatically generate nulls for types annotated as nullable
  See https://github.com/pholser/junit-quickcheck/pull/210
  
- Applying constraint annotations to varargs
  void myProp(@ForAll @StringLength(5) String ... strings)