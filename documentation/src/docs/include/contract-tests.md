When you combine type variables with properties defined in superclasses or interfaces
you can do some kind of _contract testing_. That means that you specify
the properties in a generically typed interface and specify the concrete class to
instantiate in a test container implementing the interface.

The following example was influenced by a similar feature in
[junit-quickcheck](http://pholser.github.io/junit-quickcheck/site/0.8/usage/contract-tests.html).
Here's the contract:

```java
interface ComparatorContract<T> {
	Comparator<T> subject();

	@Property
	default void symmetry(@ForAll("anyT") T x, @ForAll("anyT") T y) {
		Comparator<T> subject = subject();

		Assertions.assertThat(signum(subject.compare(x, y))).isEqualTo(-signum(subject.compare(y, x)));
	}

	@Provide
	Arbitrary<T> anyT();
}
```

And here's the concrete test container that can be run to execute
the property with generated Strings:

```java
class StringCaseInsensitiveProperties implements ComparatorContract<String> {

	@Override public Comparator<String> subject() {
		return String::compareToIgnoreCase;
	}

	@Override
	@Provide
	public Arbitrary<String> anyT() {
		return Arbitraries.strings().alpha().ofMaxLength(20);
	}
}
```

What we can see here is that _jqwik_ is able to figure out the concrete
type of type variables when they are used in subtypes that fill in
the variables.
