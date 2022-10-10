Sometimes just mapping a single stream of generated values is not enough to generate
a more complicated domain object.
What you want to do is to create arbitraries for parts of your domain object 
and then mix those parts together into a resulting combined arbitrary.

_Jqwik_ offers provides two main mechanism to do that:
- Combine arbitraries in a functional style using [Combinators.combine(..)](#combining-arbitraries-with-combine)
- Combine arbitraries using [builders](#combining-arbitraries-with-builders)

### Combining Arbitraries with `combine`

[`Combinators.combine()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Combinators.html#combine(net.jqwik.api.Arbitrary,net.jqwik.api.Arbitrary))
allows you to set up a composite arbitrary from up to eight parts.

[The following example](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/MappingAndCombinatorExamples.java#L25)
generates `Person` instances from three arbitraries as inputs.

```java
@Property
void validPeopleHaveIDs(@ForAll("validPeople") Person aPerson) {
    Assertions.assertThat(aPerson.getID()).contains("-");
    Assertions.assertThat(aPerson.getID().length()).isBetween(5, 24);
}

@Provide
Arbitrary<Person> validPeople() {
    Arbitrary<String> names = Arbitraries.strings().withCharRange('a', 'z')
        .ofMinLength(3).ofMaxLength(21);
    Arbitrary<Integer> ages = Arbitraries.integers().between(0, 130);
    return Combinators.combine(names, ages)
        .as((name, age) -> new Person(name, age));
}

class Person {
    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getID() {
        return name + "-" + age;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", name, age);
    }
}
```

The property should fail, thereby shrinking the falsified Person instance to
```
Shrunk Sample (<n> steps)
-------------------------
  aPerson: aaaaaaaaaaaaaaaaaaaaa:100
```

The `Combinators.combine` method accepts up to 8 parameters of type Arbitrary.
If you need more you have a few options:

- Consider to group some parameters into an object of their own and change your design
- Generate inbetween arbitraries e.g. of type `Tuple` and combine those in another step
- Introduce a build for your domain object and combine them
  [in this way](#combining-arbitraries-with-builders)

#### Filtering Combinations

You may run into situations in which you want to combine two or more arbitraries,
but not all combinations of values makes sense.
Consider the example of combining a pair of values from the same domain, 
but the values should never be the same.
Adding a filter step between `combine(..)` and `as(..)` provides you with the
capability to sort out unwanted combinations:

```java
@Property
void pairsCannotBeTwins(@ForAll("digitPairsWithoutTwins") String pair) {
	Assertions.assertThat(pair).hasSize(2);
	Assertions.assertThat(pair.charAt(0)).isNotEqualTo(pair.charAt(1));
}

@Provide
Arbitrary<String> digitPairsWithoutTwins() {
	Arbitrary<Integer> digits = Arbitraries.integers().between(0, 9);
	return Combinators.combine(digits, digits)
					  .filter((first, second) -> first != second)
					  .as((first, second) -> first + "" + second);
}
```

#### Flat Combination

If generating domain values requires to use several generated values to be used
in generating another one, there's the combination of flat mapping and combining:

```java
@Property
boolean fullNameHasTwoParts(@ForAll("fullName") String aName) {
    return aName.split(" ").length == 2;
}

@Provide
Arbitrary<String> fullName() {
    IntegerArbitrary firstNameLength = Arbitraries.integers().between(2, 10);
    IntegerArbitrary lastNameLength = Arbitraries.integers().between(2, 10);
    return Combinators.combine(firstNameLength, lastNameLength).flatAs( (fLength, lLength) -> {
        Arbitrary<String> firstName = Arbitraries.strings().alpha().ofLength(fLength);
        Arbitrary<String> lastName = Arbitraries.strings().alpha().ofLength(fLength);
        return Combinators.combine(firstName, lastName).as((f,l) -> f + " " + l);
    });
}
```

Often, however, there's an easier way to achieve the same goal which
does not require the flat combination of arbitraries:

```java
@Provide
Arbitrary<String> fullName2() {
    Arbitrary<String> firstName = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(10);
    Arbitrary<String> lastName = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(10);
    return Combinators.combine(firstName, lastName).as((f, l) -> f + " " + l);
}
```

This is not only easier to understand but it usually improves shrinking.


### Combining Arbitraries with Builders

There's an alternative way to combine arbitraries to create an aggregated object
by using a builder for the aggregated object. Consider the example from
[above](#combining-arbitraries) and throw a `PersonBuilder` into the mix:

```java
static class PersonBuilder {

    private String name = "A name";
    private int age = 42;

    public PersonBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PersonBuilder withAge(int age) {
        this.age = age;
        return this;
    }

    public Person build() {
        return new Person(name, age);
    }
}
```

Then you can go about generating people in the following way:

```java
@Provide
Arbitrary<Person> validPeopleWithBuilder() {
    Arbitrary<String> names = 
        Arbitraries.strings().withCharRange('a', 'z').ofMinLength(2).ofMaxLength(20);
    Arbitrary<Integer> ages = Arbitraries.integers().between(0, 130);
    
    return Builders.withBuilder(() -> new PersonBuilder())
        .use(names).in((builder, name) -> builder.withName(name))
        .use(ages).withProbability(0.5).in((builder, age)-> builder.withAge(age))
        .build( builder -> builder.build());
}
```

If you don't want to introduce an explicit builder object, 
you can also use a mutable POJO -- e.g. a Java bean -- instead:

```java
@Provide
Arbitrary<Person> validPeopleWithPersonAsBuilder() {
	Arbitrary<String> names =
		Arbitraries.strings().withCharRange('a', 'z').ofMinLength(3).ofMaxLength(21);
	Arbitrary<Integer> ages = Arbitraries.integers().between(0, 130);

	return Builders.withBuilder(() -> new Person(null, -1))
				   .use(names).inSetter(Person::setName)
				   .use(ages).withProbability(0.5).inSetter(Person::setAge)
				   .build();
}
```

Have a look at
[Builders.withBuilder(Supplier)](/docs/${docsVersion}/javadoc/net/jqwik/api/Builders.html#withBuilder(java.util.function.Supplier))
to check the API.

### Uniqueness Constraints

In many problem domains there exist identifying features or attributes 
that must not appear more than once.
In those cases the multiple generation of objects can be restricted by
either [annotating parameters with `@UniqueElements`](#unique-elements)
or by using one of the many `uniqueness(..)` configuration methods for 
collections and collection-like types:

- `ListArbitrary<T>.uniqueElements(Function<T, Object>)`
- `ListArbitrary<T>.uniqueElements()`
- `SetArbitrary<T>.uniqueElements(Function<T, Object>)`
- `StreamArbitrary<T>.uniqueElements(Function<T, Object>)`
- `StreamArbitrary<T>.uniqueElements()`
- `IteratorArbitrary<T>.uniqueElements(Function<T, Object>)`
- `IteratorArbitrary<T>.uniqueElements()`
- `ArrayArbitrary<T, A>.uniqueElements(Function<T, Object>)`
- `ArrayArbitrary<T, A>.uniqueElements()`
- `MapArbitrary<K, V>.uniqueKeys(Function<K, Object>)`
- `MapArbitrary<K, V>.uniqueValues(Function<V, Object>)`
- `MapArbitrary<K, V>.uniqueValues()`

The following examples demonstrates how to generate a list of `Person` objects
whose names must be unique:

```java
@Property
void listOfPeopleWithUniqueNames(@ForAll("people") List<Person> people) {
  List<String> names = people.stream().map(p -> p.name).collect(Collectors.toList());
  Assertions.assertThat(names).doesNotHaveDuplicates();
}

@Provide
Arbitrary<List<Person>> people() {
  Arbitrary<String> names = Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(20);
  Arbitrary<Integer> ages = Arbitraries.integers().between(0, 120);
  
  Arbitrary<Person> persons = Combinators.combine(names, ages).as((name, age) -> new Person(name, age));
  return persons.list().uniqueElements(p -> p.name);
};
```

### Ignoring Exceptions During Generation

Once in a while, usually when [combining generated values](#combining-arbitraries),
it's difficult to figure out in advance all the constraints that make the generation of objects
valid. In a good object-oriented model, however, the objects themselves --
i.e. their constructors or factory methods -- take care that only valid objects
can be created. The attempt to create an invalid value will be rejected with an
exception.

As a good example have a look at JDK's `LocalDate` class, which allows to instantiate dates
using `LocalDate.of(int year, int month, int dayOfMonth)`.
In general `dayOfMonth` can be between `1` and `31` but trying to generate a
"February 31" will throw a `DateTimeException`. Therefore, when you want to randomly
generated dates between "January 1 1900" and "December 31 2099" you have two choices:

- Integrate all rules about valid dates -- including leap years! -- into your generator.
  This will probably require a cascade of flat-mapping `years` to `months` to `days`.
- Rely on the factory method's built-in validation and just ignore thrown
  `DateTimeException` instances:

```java
@Provide
Arbitrary<LocalDate> datesBetween1900and2099() {
  Arbitrary<Integer> years = Arbitraries.integers().between(1900, 2099);
  Arbitrary<Integer> months = Arbitraries.integers().between(1, 12);
  Arbitrary<Integer> days = Arbitraries.integers().between(1, 31);
  
  return Combinators.combine(years, months, days)
  	  .as(LocalDate::of)
  	  .ignoreException(DateTimeException.class);
}
```

### Fix an Arbitrary's `genSize`

Some generators (e.g. most number generators) are sensitive to the
`genSize` value that is used when creating them.
The default value for `genSize` is the number of tries configured for the property
they are used in. If there is a need to influence the behaviour of generators
you can do so by using
[`Arbitrary.fixGenSize(int)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitrary.html#fixGenSize(int)).

