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
is adding another annotation to a parameter or its parameter types. What if the existing annotations
do not suffice for your needs? 
Is there a way to enhance the set of constraint annotations? Yes, there is!

The mechanism you can plug into is similar to what you do when
[providing your own default arbitrary providers](#providing-default-arbitraries). That means:

1. Create an implementation of an interface, in this case
   [`ArbitraryConfigurator`](/docs/${docsVersion}/javadoc/net/jqwik/api/configurators/ArbitraryConfigurator.html).
2. Register the implementation using using Java’s `java.util.ServiceLoader` mechanism.

jQwik will then call your implementation for every parameter that is annotated with 
`@ForAll` and any **additional annotation**.
That also means that parameters without an additional annotation will not 
and cannot be affected by a configurator.

#### Arbitrary Configuration Example: `@Odd`

To demonstrate the idea let's create an annotation `@Odd` which will constrain any integer
generation to only generate odd numbers. First things first, so here's
the [`@Odd` annotation](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/arbitraryconfigurator/Odd.java):

```java
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Odd {
}
```

On its own this annotation will not have any effect. You also need an arbitrary configurator:

```java
public class OddConfigurator implements ArbitraryConfigurator {

    @Override
    public <T> Arbitrary<T> configure(Arbitrary<T> arbitrary, TypeUsage targetType) {
        if (!targetType.isOfType(Integer.class) && !targetType.isOfType(int.class)) {
            return arbitrary;
        }
        return targetType.findAnnotation(Odd.class)
                         .map(odd -> arbitrary.filter(number -> Math.abs(((Integer) number) % 2) == 1))
                         .orElse(arbitrary);
    }
}
```

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

However, this implementation is rather onerous since it has to check for supported target types 
and annotation; it also has to cast the arbitrary's value to `Integer`.
Let's see if we can do better.

#### Using Arbitrary Configurator Base Class

Deriving your implementation from [`ArbitraryConfiguratorBase`](/docs/${docsVersion}/javadoc/net/jqwik/api/configurators/ArbitraryConfiguratorBase.html) will largely simplify things:


```java
public class OddConfigurator extends ArbitraryConfiguratorBase {

	public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, Odd odd) {
		return arbitrary.filter(number -> Math.abs(number % 2) == 1);
	}
}
```

The nice thing about `ArbitraryConfiguratorBase` is that it will take care of all the
boilerplate code for you. It will check for supported target types and annotations and
will also that the actual arbitrary can be cast to the arbitrary parameter type.
Your subclass can have any number of `configure` methods under the following constraints:

- They must be `public`.
- Their name starts with `configure`.
- Their first parameter must be of type `Arbitrary` or a subtype of `Arbitrary`.
- Their second parameter must have an annotation type.
- Their return type must be `Arbitrary` or a subtype of `Arbitrary`, 
  compatible with the original arbitrary's target type.

With this knowledge we can easily enhance our `OddConfigurator` to also support `BigInteger` values:

```java
public class OddConfigurator extends ArbitraryConfiguratorBase {
    public Arbitrary<Integer> configureInteger(Arbitrary<Integer> arbitrary, Odd odd) {
        return arbitrary.filter(number -> Math.abs(number % 2) == 1);
    }

    public Arbitrary<BigInteger> configureBigInteger(Arbitrary<BigInteger> arbitrary, Odd odd) {
        return arbitrary.filter(number -> {
            return number.remainder(BigInteger.valueOf(2)).abs().equals(BigInteger.ONE);
        });
    }
}
```

You can combine `@Odd` with other annotations like `@Positive` or `@Range` or another
self-made configurator. In this case the order of configurator application might play a role,
which can be influenced by overriding the `order()` method of a configurator.
