- PackageDescriptor e.g.
  @Group
  @Label("mypackage")
  @AddHook(...)
  in package-info.java

- Arbitrary provider for
  - Iterable: list.map(l -> (Iterable) l)
  - Iterator: list.map(l -> l.iterator())

- Move release notes to their own web page