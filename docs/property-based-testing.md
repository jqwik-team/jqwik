# More about Property-Based Testing

There is a lot of noise (and hype) about Property-Based Testing (PBT) out there.
However, most of the stuff is in the context of functional programming languages.

### Introductory Material

- Here is an [overview article](https://en.wikipedia.org/wiki/Property_testing) on Wikipedia.
- [Quickcheck](https://en.wikipedia.org/wiki/QuickCheck) is the original tool for writing property tests.
- A [nice video](https://www.youtube.com/watch?v=fltD7yrHbUA) from a presentation by Noel Markham
  in which he motivates and introduces ScalaCheck.

### Patterns for PBT

### Alternative Tools for the JVM

There are a few alternatives to _jqwik_ if you want to do PBT on the JVM:

- [JUnit-Quickcheck](http://pholser.github.io/junit-quickcheck): 
  Tightly integrated with JUnit 4, also annotations to configure generators.
- [QuickTheories](https://github.com/ncredinburgh/QuickTheories):
  Unlike other systems QuickTheories supports both shrinking and targeted search using coverage data.
- [Vavr](http://www.vavr.io/): The functional library also comes with a 
  [property-based testing module](https://github.com/vavr-io/vavr/tree/master/vavr-test).
- [ScalaCheck](http://www.scalacheck.org/): A mature property based testing system with shrinking and all, 
  iff you prefer Scala over Java.
- [test.check for Clojure](https://github.com/clojure/test.check): Inspired by QuickCheck. Since Clojure
  does not have static types generators must always be declared explicitly.
  
Please let me know if you learn about any other _maintained_ library or tool.
