### Implement your own Arbitraries and Generators

Looking at _jqwik_'s most prominent interfaces -- `Arbitrary` and `RandomGenerator` -- you might
think that rolling your own implementations of these is a reasonable thing to do.
I'd like to tell you that it _never_ is, but I've learned that "never" is a word you should never use.
There's just too many things to consider when implementing a new type of `Arbitrary`
to make it work smoothly with the rest of the framework.

Therefore, use the innumerable features to combine existing arbitraries into your special one.
If your domain arbitrary must implement another interface - e.g. for configuration -,
subclassing `net.jqwik.api.arbitraries.ArbitraryDecorator` is the way to go.
An example would come in handy now...

Imagine you have to roll your own complex number type: 

```java
public class ComplexNumber {
    ...
	public ComplexNumber(double rational, double imaginary) {
		this.rational = rational;
		this.imaginary = imaginary;
	}
}
```

And you want to be able to tell the arbitrary whether or not the generated number
should have an imaginary part:

```java
@Property
void rationalNumbers(@ForAll("rationalOnly") ComplexNumber number) {
    ...
}

@Provide
Arbitrary<ComplexNumber> rationalOnly() {
	return new ComplexNumberArbitrary().withoutImaginaryPart();
}
```

Here's how you could implement a configurable `ComplexNumberArbitrary` type:

```java
public class ComplexNumberArbitrary extends ArbitraryDecorator<ComplexNumber> {

	private boolean withImaginaryPart = true;

	@Override
	protected Arbitrary<ComplexNumber> arbitrary() {
		Arbitrary<Double> rationalPart = Arbitraries.doubles();
		Arbitrary<Double> imaginaryPart = withImaginaryPart ? Arbitraries.doubles() : Arbitraries.just(0.0);
		return Combinators.combine(rationalPart, imaginaryPart).as(ComplexNumber::new);
	}

	public ComplexNumberArbitrary withoutImaginaryPart() {
		this.withImaginaryPart = false;
		return this;
	}
}
```

Overriding `ArbitraryDecorator.arbitrary()` is where you can apply the knowledge
of the previous chapters.
If you get stuck figuring out how to create an arbitrary with the desired behaviour
either [ask on stack overflow](https://stackoverflow.com/questions/tagged/jqwik)
or [open a Github issue](https://github.com/jqwik-team/jqwik/issues).
