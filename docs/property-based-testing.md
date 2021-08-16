# More about Property-Based Testing

There is a lot of noise (and hype) about Property-Based Testing (PBT) out there.
However, most of the stuff is in the context of functional programming languages.

### Introductory Material

- David MacIver, the author of [Hypothesis](https://hypothesis.works/), a PBT library for Python, 
  tries to answer the question: 
  [What is Property Based Testing?](https://hypothesis.works/articles/what-is-property-based-testing/)
- Also from David MacIver: [In praise of property-based testing](https://increment.com/testing/in-praise-of-property-based-testing/)
- A series of blog entries about 
  [PBT in Java](https://blog.johanneslink.net/2018/03/24/property-based-testing-in-java-introduction/)
  from the developer of jqwik.
- [How to Specify it! In Java!](https://johanneslink.net/how-to-specify-it/)
- [Know for Sure](https://blogs.oracle.com/javamagazine/know-for-sure-with-property-based-testing): 
  An article in Oracle's Java Magazine about Property-based Testing in Java
- [Quickcheck](https://en.wikipedia.org/wiki/QuickCheck) is the original tool for writing property tests.
  The article links to scores of PBT libraries in dozens of programming languages.
- A [nice video](https://www.youtube.com/watch?v=fltD7yrHbUA) from a presentation by Noel Markham
  in which he motivates and introduces ScalaCheck.
- [PropEr Testing](http://propertesting.com/): An online-book about Property-Based Testing.
  The examples use Erlang but most of the contents is generic.

### Patterns for PBT

Some of the common patterns used in PBT are described 
[here](http://blog.ssanj.net/posts/2016-06-26-property-based-testing-patterns.html).

### Alternative Tools for the JVM

There are a few alternatives to _jqwik_ if you want to do PBT on the JVM:

- [JUnit-Quickcheck](http://pholser.github.io/junit-quickcheck): 
  Tightly integrated with JUnit 4, also uses annotations to configure generators.
- [QuickTheories](https://github.com/ncredinburgh/QuickTheories):
  Unlike other systems QuickTheories supports both shrinking and targeted search using coverage data.
- [Vavr](http://www.vavr.io/): The functional library also comes with a 
  [property-based testing module](https://github.com/vavr-io/vavr-test).
- [jetCheck](https://github.com/JetBrains/jetCheck): A property-based testing library for Java 8+. 
  Works with any testing framework.
- [ScalaCheck](http://www.scalacheck.org/): A mature property based testing system with shrinking and all, 
  iff you prefer Scala over Java.
- [test.check for Clojure](https://github.com/clojure/test.check): Inspired by QuickCheck. Since Clojure
  does not have static types generators must always be declared explicitly.
- [KotlinTest](https://github.com/kotlintest/kotlintest): Has some basic support for PBT. 
  Currently no shrinking yet.
- [Frege, a Haskell for the JVM,](https://github.com/Frege/frege)
  comes with a classical QuickCheck implementation.
  [This section](https://dierk.gitbooks.io/fregegoodness/content/src/docs/asciidoc/qc_property.html)
  from Dierk König's Frege book provides a short introduction.

  
Please let me know if you learn about any other _maintained_ library or tool.
