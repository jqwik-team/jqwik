Some domain classes are mostly data holders. They come with constructors
or factory methods to create them and you might want to create different
instances by "just" filling the constructors' parameters with values
that are themselves generated. Using the building blocks you've seen until
now requires the use of `Arbitrary.map()` or even `Combinators.combine(...).as(...)`
to invoke the relevant constructor(s) and/or factories yourself.
There's a simpler way, though...

Consider a simple `Person` class:

```java
public class Person {

	private final String name;
	private final int age;

	public Person(String name, int age) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException();
		if (age < 0 || age > 130)
			throw new IllegalArgumentException();

		this.name = name;
		this.age = age;
	}

	@Override
	public String toString() {
		return String.format("%s (%d)", name, age);
	}
}
```

A first step to use arbitrarily generated `Person` objects without having
to write a lot of _jqwik_-specific boiler plate code could look like that:

```java
@Property
void aPersonsIsAlwaysValid(@ForAll @UseType Person aPerson) {
    Assertions.assertThat(aPerson.name).isNotBlank();
    Assertions.assertThat(aPerson.age).isBetween(0, 130);
}
```

Notice the annotation `@UseType` which tells _jqwik_ to use the type
information of `Person` to generate it. By default the framework will
use all public constructors and all public, static factory methods in
the class in order to generate instances. Whenever there's an exception during
generation they will be ignored; that way you'll only get valid instances.

While the creation of a `Person` instance requires only basic Java types - 
`String` and `int` - that already have default arbitraries available,
`@UseType` is also applied to nested types without default generators.
That's why class `Party`:

```java
public class Party {

	final String name;
	final Set<Person> people;

	public Party(String name, Set<Person> people) {
		this.name = name;
		this.people = people;
	}
}
```

can also be generated in the same way:

```java
@Property
void aPartyOfPeopleCanBeGenerated(@ForAll @UseType Party aParty) {
	Assertions.assertThat(aParty.name).isNotBlank();
	Assertions.assertThat(aParty.people).allMatch(
		person -> !person.name.isEmpty()
	);
}
```

This _recursive_ application of `@UseType` is switched on by default, 
but can also be switched off: `@UseType(enableRecursion=false)`.

To learn about all configuration options have a look
at the [complete example](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/types/TypeArbitraryExamples.java)
and check the following api entry points:

- [UseType](/docs/${docsVersion}/javadoc/net/jqwik/api/constraints/UseType.html)
- [UseTypeMode](/docs/${docsVersion}/javadoc/net/jqwik/api/constraints/UseTypeMode.html)
- [Arbitraries.forType()](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#forType(java.lang.Class))
- [TypeArbitrary](/docs/${docsVersion}/javadoc/net/jqwik/api/arbitraries/TypeArbitrary.html)

