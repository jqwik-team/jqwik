_**The approach described here has been freshly introduced in version 1.7.0.
It is still marked "experimental" but will probably be promoted to "maintained" in
one of the next minor versions of jqwik.
You can also read about [the old way of stateful testing](#stateful-testing-old-approach).
Since both approaches have an interface called `Action`, 
be careful to import the right one!**_

Despite its bad reputation _state_ is an important concept in object-oriented languages like Java.
We often have to deal with stateful objects or components whose state can be changed through methods.
Applying the concept of properties to stateful objects or data is not a new idea.
Jqwik provides the tools for you to explore and implement these ideas.
Those tools are available in package [net.jqwik.api.state](/docs/${docsVersion}/javadoc/net/jqwik/api/state/package-summary.html).

### State Machines

One, slightly formal, way to look at stateful objects are _state machines_.
A state machine has an internal state and _actions_ that change the internal state.
Some actions have preconditions to constrain when they can be invoked. 
Also, state machines can have invariants that should never be violated regardless
of the sequence of performed actions.

To make this abstract concept concrete, let's look at a
[simple stack implementation](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/state/mystack/MyStringStack.java):

```java
public class MyStringStack {
	public void push(String element) { ... }
	public String pop() { ... }
	public void clear() { ... }
	public boolean isEmpty() { ... }
	public int size() { ... }
	public String top() { ... }
}
```

### Specifying Actions

Jqwik's [new `Action` type](/docs/${docsVersion}/javadoc/net/jqwik/api/state/Action.html)
covers the idea that an action can be represented by "arbitrary" 
[transformers](/docs/${docsVersion}/javadoc/net/jqwik/api/state/Transformer.html);
arbitrary in the sense that in the context of property-based testing there's some variation to it,
e.g. a push onto a string stack can have any `String` as parameter.
And as mentioned above, actions can be restricted by preconditions.

```java
package net.jqwik.api.state;
interface Action<S> {
    default boolean precondition(S state) {
        return true;
    }

    interface Independent<S> extends Action<S> {
        Arbitrary<Transformer<S>> transformer();
    }

    interface Dependent<S> extends Action<S> {
        Arbitrary<Transformer<S>> transformer(S state);
    }
}

interface Transformer<S> extends Function<S, S> {}
```

What makes the abstraction a bit more complicated than desirable is the fact that 
the range of possible transformers may or may not depend on the previous state.
That's why there exist the two subtypes of `Action`: `Independent` and `Dependent`.
Both types of actions can have a precondition to state when they can be applied.
Leaving the precondition out means that the action can be applied at any time.

Given that the precondition is fulfilled,
- the transforming behaviour of an _independent_ action is defined by the `transformer()` method
  and does not rely on the previous state.
- the transforming behaviour of an _dependent_ action, however, 
  is defined by the `transformer(S state)` method and can use the previous state
  in order to constrain how a transformation should be done.

Both `transformer` methods return an `Arbitrary` of `Transformer` instances,
which means that they describe a range of possible transformations; 
jqwik will pick one of them at random as it does with any other `Arbitrary`.
Mind that a transformer will not only invoke the transformation 
but will often check the correct state changes and postcondition(s) as well.

In our simple stack example at least three _actions_ can be identified:

- __Push__ a string onto the stack. 
  The string should be on top afterwards and the size should have increased by 1.

  ```java
  class PushAction implements Action.Independent<MyStringStack> {
    @Override
    public Arbitrary<Transformer<MyStringStack>> transformer() {
      Arbitrary<String> pushElements = Arbitraries.strings().alpha().ofLength(5);
      return pushElements.map(element -> Transformer.mutate(
        String.format("push(%s)", element),
        stack -> {
          int sizeBefore = stack.size();
          stack.push(element);
          assertThat(stack.isEmpty()).isFalse();
          assertThat(stack.size()).isEqualTo(sizeBefore + 1);
        }
      ));
    }
  }
  ``` 
  
  In this case I've decided to directly implement the `Action.Independent` interface.
  In order to facilitate the creation of transformers, the `Transformer` class comes 
  with a few convenience methods; here `Transformer.mutate()` was a good fit.

- __Pop__ the last element from the stack.
  If (and only if) the stack is not empty, pop the element on top off the stack.
  The size of the stack should have decreased by 1.

  ```java
  private Action<MyStringStack> pop() {
    return Action.<MyStringStack>when(stack -> !stack.isEmpty())
                 .describeAs("pop")
                 .justMutate(stack -> {
                   int sizeBefore = stack.size();
                   String topBefore = stack.top();
                   
                   String popped = stack.pop();
                   assertThat(popped).isEqualTo(topBefore);
                   assertThat(stack.size()).isEqualTo(sizeBefore - 1);
                 });
  }
  ```
  
  For _pop_ using an action builder through `Action.when(..)` seemed simpler 
  than implementing the `Action.Independent` interface.

- __Clear__ the stack, which should be empty afterwards.

  ```java
  static class ClearAction extends Action.JustMutate<MyStringStack> {
    @Override
    public void mutate(MyStringStack stack) {
    	stack.clear();
    	assertThat(stack.isEmpty()).describedAs("stack is empty").isTrue();
    }
    
    @Override
    public String description() {
    	return "clear";
    }
  }
  ``` 

  Here you can see yet another implementation option for actions that have no variation in their
  transforming behaviour: Just subclass `Action.JustTransform` or `Action.JustMutate`.

There are different ways to implement actions. 
Sometimes one is obviously simpler than the other.
In other cases it's a matter of taste - e.g. about preferring functions over classes, 
or the other way round.

### Formulating Stateful Properties

Now that we have a set of actions, we can formulate the 
_fundamental property of stateful systems_:

> For any valid sequence of actions all required state changes
> (aka postconditions) should be fulfilled.

Let's translate that into jqwik's language:

```java
@Property
void checkMyStack(@ForAll("myStackActions") ActionChain<MyStringStack> chain) {
  chain.run();
}

@Provide
Arbitrary<ActionChain<MyStringStack>> myStackActions() {
  return ActionChain.startWith(MyStringStack::new)
                    .addAction(new PushAction())
                    .addAction(pop())
                    .addAction(new ClearAction());
}
```

The interesting API elements are
- [`ActionChain`](/docs/${docsVersion}/javadoc/net/jqwik/api/state/ActionChain.html):
  This interface provides the entry point for running a stateful object through
  a sequence of actions through its `run()` method, which returns the final state
  as a convenience.

- [`Action.Chain.startWith()`](/docs/${docsVersion}/javadoc/net/jqwik/api/state/ActionChain.html#startWith(java.util.function.Supplier)):
  This method will create an arbitrary for generating an `ActionChain`.
  This [`ActionChainArbitrary`](/docs/${docsVersion}/javadoc/net/jqwik/api/state/ActionChainArbitrary.html)
  has methods to add potential actions and to further configure chain generation.

### Running Stateful Properties

To give _jqwik_ something to falsify, we broke the implementation of `clear()` so that
it won't clear everything if there are more than two elements on the stack:

```java
public void clear() {
    // Wrong implementation to provoke falsification for stacks with more than 2 elements
    if (elements.size() > 2) {
        elements.remove(0);
    } else {
        elements.clear();
    }
}
```

Running the property should now produce a result similar to:

```
MyStringStackExamples:checkMyStack = 
  org.opentest4j.AssertionFailedError:
    Run failed after the following actions: [
        push(AAAAA)
        push(AAAAA)
        push(AAAAA)
        clear  
    ]
    final state: [AAAAA, AAAAA]
    [stack is empty] 
    Expecting value to be true but was false
```

The error message shows the sequence of actions that led to the failing postcondition.
Moreover, you can notice that the sequence of actions has been shrunk to the minimal failing sequence.

_**TO BE CONTINUED...**_

<!--

### Number of actions

_jqwik_ will vary the number of generated actions according to the number
of `tries` of your property. For the default of 1000 tries a sequence will
have 32 actions. If need be you can specify the number of actions
to generate using either the fluent interface or the `@Size` annotation:

```java
@Property
// check stack with sequences of 7 actions:
void checkMyStack(@ForAll("sequences") @Size(7) ActionSequence<MyStringStack> actions) {
    actions.run(new MyStringStack());
}
```

The minimum number of generated actions in a sequence is 1 since checking
an empty sequence does not make sense.

### Check Invariants

We can also add invariants to our sequence checking property:

```java
@Property
void checkMyStackWithInvariant(@ForAll("sequences") ActionSequence<MyStringStack> actions) {
    actions
        .withInvariant(stack -> Assertions.assertThat(stack.size()).isGreaterThanOrEqualTo(0))
        .withInvariant(stack -> Assertions.assertThat(stack.size()).isLessThan(5))
        .run(new MyStringStack());
}
```

If we first fix the bug in `MyStringStack.clear()` our property should eventually fail
with the following result:

```
org.opentest4j.AssertionFailedError: 
  Run failed after following actions:
      push(AAAAA)
      push(AAAAA)
      push(AAAAA)
      push(AAAAA)
      push(AAAAA)
    final state: ["AAAAA", "AAAAA", "AAAAA", "AAAAA", "AAAAA"]
```

-->

## Rerunning Falsified Action Chains

As described in the [chapter about rerunning falsified properties](#rerunning-falsified-properties)
_jqwik_ has different options for rerunning falsified properties.

Due to the fact that action chains are generated one action after the other,
recreating the exact same sample of a chain is usually not possible.
That's why `AfterFailureMode.SAMPLE_ONLY` and `AfterFailureMode.SAMPLE_FIRST`
will just start with the same random seed, which leads to the same sequence of chains,
but not start with the last failing sample chain.
A warning will be logged in such cases.
