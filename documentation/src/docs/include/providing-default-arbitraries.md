Sometimes you want to use a certain, self-made `Arbitrary` for one of your own domain
classes, in all of your properties, and without having to add `@Provide` method
to all test classes. _jqwik_ enables this feature by using
Java’s `java.util.ServiceLoader` mechanism. All you have to do is:

- Implement the interface [`ArbitraryProvider`](/docs/${docsVersion}/javadoc/net/jqwik/api/providers/ArbitraryProvider.html).<br/>
  The implementing class _must_ have a default constructor without parameters.
- Register the implementation class in file

  ```
  META-INF/services/net.jqwik.api.providers.ArbitraryProvider
  ```

_jqwik_ will then add an instance of your arbitrary provider into the list of
its default providers. Those default providers are considered for every test parameter annotated
with [`@ForAll`](/docs/${docsVersion}/javadoc/net/jqwik/api/ForAll.html) that has no explicit `value`.
By using this mechanism you can also replace the default providers
packaged into _jqwik_.

### Simple Arbitrary Providers

A simple provider is one that delivers arbitraries for types without type variables.
Consider the class [`Money`](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/defaultprovider/Money.java):

```java
public class Money {
	public BigDecimal getAmount() {
		return amount;
	}

	public String getCurrency() {
		return currency;
	}

	public Money(BigDecimal amount, String currency) {
		this.amount = amount;
		this.currency = currency;
	}

	public Money times(int factor) {
		return new Money(amount.multiply(new BigDecimal(factor)), currency);
	}
}
``` 

If you register the following class
[`MoneyArbitraryProvider`](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/defaultprovider/MoneyArbitraryProvider.java):

```java
package my.own.provider;

public class MoneyArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isOfType(Money.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		Arbitrary<BigDecimal> amount = Arbitraries.bigDecimals()
				  .between(BigDecimal.ZERO, new BigDecimal(1_000_000_000))
				  .ofScale(2);
		Arbitrary<String> currency = Arbitraries.of("EUR", "USD", "CHF");
		return Collections.singleton(Combinators.combine(amount, currency).as(Money::new));
	}
}
```

in file
[`META-INF/services/net.jqwik.api.providers.ArbitraryProvider`](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/resources/META-INF/services/net.jqwik.api.providers.ArbitraryProvider)
with such an entry:

```
my.own.provider.MoneyArbitraryProvider
```

The
[following property](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/defaultprovider/MoneyProperties.java)
will run without further ado - regardless the class you put it in:

```java
@Property
void moneyCanBeMultiplied(@ForAll Money money) {
    Money times2 = money.times(2);
    Assertions.assertThat(times2.getCurrency()).isEqualTo(money.getCurrency());
    Assertions.assertThat(times2.getAmount())
        .isEqualTo(money.getAmount().multiply(new BigDecimal(2)));
}
```

### Arbitrary Providers for Parameterized Types

Providing arbitraries for generic types requires a little bit more effort
since you have to create arbitraries for the "inner" types as well.
Let's have a look at the default provider for `java.util.Optional<T>`:

```java
public class OptionalArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isOfType(Optional.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		TypeUsage innerType = targetType.getTypeArguments().get(0);
		return subtypeProvider.apply(innerType).stream()
			.map(Arbitrary::optional)
			.collect(Collectors.toSet());
	}
}
```

Mind that `provideFor` returns a set of potential arbitraries.
That's necessary because the `subtypeProvider` might also deliver a choice of
subtype arbitraries. Not too difficult, is it?


### Arbitrary Provider Priority

When more than one provider is suitable for a given type, _jqwik_ will randomly
choose between all available options. That's why you'll have to take additional
measures if you want to replace an already registered provider. The trick
is to override a provider's `priority()` method that returns `0` by default:

```java
public class AlternativeStringArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(String.class);
	}

	@Override
	public int priority() {
		return 1;
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		return Collections.singleton(Arbitraries.just("A String"));
	}
}
```

If you register this class as arbitrary provider any `@ForAll String` will
be resolved to `"A String"`.

### Create your own Annotations for Arbitrary Configuration

All you can do [to constrain default parameter generation](#constraining-default-generation)
is adding another annotation to a parameter or its parameter types. What if the existing parameters
do not suffice your needs? Is there a way to enhance the set of constraint annotations? Yes, there is!

The mechanism you can plug into is similar to what you do when
[providing your own default arbitrary providers](#providing-default-arbitraries). That means:

1. Create an implementation of an interface, in this case
   [`ArbitraryConfigurator`](/docs/${docsVersion}/javadoc/net/jqwik/api/configurators/ArbitraryConfigurator.html).
2. Register the implementation using using Java’s `java.util.ServiceLoader` mechanism.

#### Arbitrary Configuration Example: `@Odd`

To demonstrate the idea let's create an annotation `@Odd` which will constrain any integer
generation to only generate odd numbers. First things first, so here's
the [`@Odd` annotation](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/arbitraryconfigurator/Odd.java)
together with the
[configurator implementation](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/arbitraryconfigurator/OddConfigurator.java):

```java
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Odd {
}

public class OddConfigurator extends ArbitraryConfiguratorBase {
	public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, Odd odd) {
		return arbitrary.filter(number -> Math.abs(number % 2) == 1);
	}
}
```

Mind that the implementation uses an abstract base class - instead of the interface itself -
which simplifies implementation if you're only interested in a single annotation.

If you now
[register the implementation](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/resources/META-INF/services/net.jqwik.api.configurators.ArbitraryConfigurator),
the [following example](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/arbitraryconfigurator/OddProperties.java)
will work:

```java
@Property
boolean oddIntegersOnly(@ForAll @Odd int aNumber) {
    return Math.abs(aNumber % 2) == 1;
}
```

There are a few catches, though:

- Currently `OddConfigurator` will accept any target type since type erasure
  will get rid of `<Integer>` in configure-method's signature at runtime.
  Therefore, using `@Odd` together with e.g. `BigInteger` will lead to a runtime
  exception. You can prevent that by explicitly accepting only some target types:

  ```java
  public class OddConfigurator extends ArbitraryConfiguratorBase {

  	@Override
  	protected boolean acceptTargetType(TypeUsage targetType) {
  		return targetType.isAssignableFrom(Integer.class);
  	}

  	public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, Odd odd) {
  		return arbitrary.filter(number -> Math.abs(number % 2) == 1);
  	}
  }
  ```

  Alternatively, you can check for an object's type directly and use different
  filter algorithms:

  ```java
  public Arbitrary<Number> configure(Arbitrary<Number> arbitrary, Odd odd) {
      return arbitrary.filter(number -> {
          if (number instanceof Integer)
              return Math.abs((int) number % 2) == 1;
          if (number instanceof BigInteger)
              return ((BigInteger) number).remainder(BigInteger.valueOf(2))
                                          .abs().equals(BigInteger.ONE);
          return false;
      });
  }
  ```

- You can combine `@Odd` with other annotations like `@Positive` or `@Range` or another
  self-made configurator. In this case the order of configurator application might play a role,
  which can be influenced by overriding the `order()` method of a configurator.
