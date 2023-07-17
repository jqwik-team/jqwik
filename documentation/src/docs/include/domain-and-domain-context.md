Until now you have seen two ways to specify which arbitraries will be created for a given parameter:

- Annotate the parameter with `@ForAll("providerMethod")`.
- [Register a global arbitrary provider](#providing-default-arbitraries)
  that will be triggered by a known parameter signature.

In many cases both approaches can be tedious to set up or require constant repetition of the same
annotation value. There's another way that allows you to group a number of arbitrary providers
(and also arbitrary configurators) in a single place, called a `DomainContext` and tell
a property method or container to only use providers and configurators from those domain contexts
that are explicitly stated in a `@Domain(Class<? extends DomainContext>)` annotation.

As for ways to implement domain context classes have a look at
[DomainContext](/docs/${docsVersion}/javadoc/net/jqwik/api/domains/DomainContext.html)
and [DomainContextBase](/docs/${docsVersion}/javadoc/net/jqwik/api/domains/DomainContextBase.html).

In subclasses of `DomainContextBase` you have several options to specify 
arbitrary providers, arbitrary configurators and reporting formats:

- Add methods annotated with `Provide` and a return type of `Arbitrary<TParam>`.
  The result of an annotated method will then be used as an arbitrary provider
  for `@ForAll` parameters of type `TParam`.
  
  Those methods follow the same rules as 
  [provider methods in container classes](#parameter-provider-methods),
  i.e. they have [_optional_ parameters](#provider-methods-with-parameters) 
  of type `TypeUsage` or `ArbitraryProvider.SubtypeProvider` 
  and can do [implicit flat-mapping](#implicit-flat-mapping) over `@ForAll` arguments. 

- Add inner classes (static or not static, but not private) that implement `ArbitraryProvider`.
  An instance of this class will then be created and used as arbitrary provider.

- Additionally, implement `ArbitraryProvider` and the domain context instance
  itself will be used as arbitrary provider.

- Add inner classes (static or not static, but not private) that implement `ArbitraryConfigurator`.
  An instance of this class will then be created and used as configurator.

- Additionally, implement `ArbitraryConfigurator` and the domain context instance
  itself will be used as configurator.

- Add inner classes (static or not static, but not private) that implement `SampleReportingFormat`.
  An instance of this class will then be created and used for reporting values of your domain object.

- Additionally, implement `SampleReportingFormat` and the domain context instance
  itself will be used for reporting values of your domain object.

A `DomainContext` implementation class can itself have `@Domain` annotations,
which are then used to add to the property's set of domains.

You can override method `DomainContext.initialize(PropertyLifecycleContext context)`,
which will be called once for each property to which this context is applied.
Since the lifecycle of `DomainContext` instances is not specified,
do not rely on storing or caching any information in member variables.
Instead, use jqwik's [Storage Mechanism](#lifecycle-storage) to persist data if needed.


### Domain example: American Addresses

Let's say that US postal addresses play a crucial role in the software that we're developing.
That's why there are a couple of classes that represent important domain concepts:
`Street`, `State`, `City` and `Address`. Since we have to generate instances of those classes
for our properties, we collect all arbitrary provision code 
[in one place](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/domains/AmericanAddresses.java):

```java
public class AmericanAddresses extends DomainContextBase {

	@Provide
	Arbitrary<Street> streets() {
		Arbitrary<String> streetName = capitalizedWord(30);
		Arbitrary<String> streetType = Arbitraries.of("Street", "Avenue", "Road", "Boulevard");
		return Combinators.combine(streetName, streetType).as((n, t) -> n + " " + t).map(Street::new);
	}

	@Provide
	Arbitrary<Integer> streetNumbers() {
		return Arbitraries.integers().between(1, 999);
	}

	@Provide
	Arbitrary<State> states() {
		return Arbitraries.of(State.class);
	}

	@Provide
	Arbitrary<City> cities() {
		Arbitrary<String> name = capitalizedWord(25);
		Arbitrary<State> state = Arbitraries.defaultFor(State.class);
		Arbitrary<String> zip = Arbitraries.strings().numeric().ofLength(5);
		return Combinators.combine(name, state, zip).as(City::new);
	}

	@Provide
	Arbitrary<Address> addresses() {
		Arbitrary<Street> streets = Arbitraries.defaultFor(Street.class);
		Arbitrary<City> cities = Arbitraries.defaultFor(City.class);
		return Combinators.combine(streets, streetNumbers(), cities).as(Address::new);
	}

	private Arbitrary<String> capitalizedWord(int maxLength) {
		Arbitrary<Character> capital = Arbitraries.chars().range('A', 'Z');
		Arbitrary<String> rest = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1).ofMaxLength(maxLength - 1);
		return Combinators.combine(capital, rest).as((c, r) -> c + r);
	}
}
```

Now it's rather easy to use the arbitraries provided therein for your properties:

```java
class AddressProperties {

	@Property
	@Domain(AmericanAddresses.class)
	void anAddressWithAStreetNumber(@ForAll Address anAddress, @ForAll int streetNumber) {
	}

	@Property
	@Domain(AmericanAddresses.class)
	void globalDomainIsNotPresent(@ForAll Address anAddress, @ForAll String anyString) {
	}

	@Property
	@Domain(DomainContext.Global.class)
	@Domain(AmericanAddresses.class)
	void globalDomainCanBeAdded(@ForAll Address anAddress, @ForAll String anyString) {
	}
}
```

The first two properties above will resolve their arbitraries solely through providers
specified in `AmericanAddresses`, whereas the last one also uses the default (global) context.
Keep in mind that the inner part of the return type `Arbitrary<InnerPart>`
is used to determine the applicability of a provider method.
It's being used in a covariant way, i.e., `Arbitrary<String>` is also applicable
for parameter `@ForAll CharSequence charSequence`.

Since `AmericanAddresses` does not configure any arbitrary provider for `String` parameters,
the property method `globalDomainIsNotPresent(..)` will fail,
whereas `globalDomainCanBeAdded(..)` will succeed because it has the additional `@Domain(DomainContext.Global.class)` annotation.
You could also add this line to class `AmericanAddresses` itself,
which would then automatically bring the global context to all users of this domain class:

```java
@Domain(DomainContext.Global.class)
public class AmericanAddresses extends DomainContextBase {
   ...
}
```

The reason that you have to jump through these hoops is that 
domains are conceived to give you perfect control about how objects of
a certain application domain are being created.
That means, that by default they _do not inherit the global context_.


