Until now you have seen two ways to specify which arbitraries will be created for a given parameter:

- Annotate the parameter with `@ForAll("providerMethod")`.
- [Register a global arbitrary provider](#providing-default-arbitraries)
  that will be triggered by a known parameter signature.

In many cases both approaches can be tedious to set up or require constant repetition of the same
annotation value. There's another way that allows you to collect a number of arbitrary providers
(and also arbitrary configurators) in a single place, called a `DomainContext` and tell
a property method or container to only use providers and configurators from those domain contexts
that are explicitly stated in a `@Domain(Class<? extends DomainContext>)` annotation.

As for ways to implement domain context classes have a look at
[DomainContext](/docs/${docsVersion}/javadoc/net/jqwik/api/domains/DomainContext.html)
and [AbstractDomainContextBase](/docs/${docsVersion}/javadoc/net/jqwik/api/domains/AbstractDomainContextBase.html).


### Domain example: American Addresses

Let's say that US postal addresses play a crucial role in the software that we're developing.
That's why there are a couple of classes that represent important domain concepts:
`Street`, `State`, `City` and `Address`. Since we have to generate instances of those classes
for our properties, we collect all arbitrary provision code in
[AmericanAddresses](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/domains/AmericanAddresses.java).
Now look at
[this example](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/domains/AddressProperties.java):

```java
class AddressProperties {

	@Property
	@Domain(AmericanAddresses.class)
	void anAddressWithAStreetNumber(@ForAll Address anAddress, @ForAll int streetNumber) {
	}

	@Property
	@Domain(AmericanAddresses.class)
	void globalDomainNotPresent(@ForAll Address anAddress, @ForAll String anyString) {
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
Since `AmericanAddresses` does not configure any arbitrary provider for `String` parameters,
property method `globalDomainNotPresent` will fail with a `CannotFindArbitraryException`.
