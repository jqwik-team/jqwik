> _As of version 1.7.0 jqwik comes with a [new approach to stateful testing](#stateful-testing).
  What is described in this chapter will probably be deprecated in one of the next minor versions._

Despite its bad reputation _state_ is an important concept in object-oriented languages like Java.
We often have to deal with stateful objects or components whose state can be changed through methods.

Thinking in a more formal way we can look at those objects as _state machines_ and the methods as
_actions_ that move the object from one state to another. Some actions have preconditions to constrain
when they can be invoked and some objects have invariants that should never be violated regardless
of the sequence of performed actions.

To make this abstract concept concrete, let's look at a
[simple stack implementation](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/stateful/mystack/MyStringStack.java):

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

### Specify Actions

We can see at least three _actions_ with their preconditions and expected state changes:

- [`Push`](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/stateful/mystack/PushAction.java):
  Push a string onto the stack. The string should be on top afterwards and the size
  should have increased by 1.

  ```java
  import net.jqwik.api.stateful.*;
  import org.assertj.core.api.*;
  
  class PushAction implements Action<MyStringStack> {
  
  	private final String element;
  
  	PushAction(String element) {
  		this.element = element;
  	}
  
  	@Override
  	public MyStringStack run(MyStringStack stack) {
  		int sizeBefore = stack.size();
  		stack.push(element);
  		Assertions.assertThat(stack.isEmpty()).isFalse();
  		Assertions.assertThat(stack.size()).isEqualTo(sizeBefore + 1);
  		return stack;
  	}
  
  	@Override
  	public String toString() { return String.format("push(%s)", element); }
  }
  ``` 

- [`Pop`](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/stateful/mystack/PopAction.java):
  If (and only if) the stack is not empty, pop the element on top off the stack.
  The size of the stack should have decreased by 1.

  ```java
  class PopAction implements Action<MyStringStack> {
    
        @Override
        public boolean precondition(MyStringStack stack) {
            return !stack.isEmpty();
        }
    
        @Override
        public MyStringStack run(MyStringStack stack) {
            int sizeBefore = stack.size();
            String topBefore = stack.top();
    
            String popped = stack.pop();
            Assertions.assertThat(popped).isEqualTo(topBefore);
            Assertions.assertThat(stack.size()).isEqualTo(sizeBefore - 1);
            return stack;
        }
    
        @Override
        public String toString() { return "pop"; }
  }
  ``` 

- [`Clear`](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/stateful/mystack/ClearAction.java):
  Remove all elements from the stack which should be empty afterwards.

  ```java
  class ClearAction implements Action<MyStringStack> {

        @Override
        public MyStringStack run(MyStringStack stack) {
            stack.clear();
            Assertions.assertThat(stack.isEmpty()).isTrue();
            return stack;
        }
    
        @Override
        public String toString() { return "clear"; }
  }
  ``` 

### Check Postconditions

The fundamental property that _jqwik_ should try to falsify is:

    For any valid sequence of actions all required state changes
    (aka postconditions) should be fulfilled.

We can formulate that quite easily as a
[_jqwik_ property](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/stateful/mystack/MyStringStackProperties.java):

```java
class MyStringStackProperties {

	@Property
	void checkMyStack(@ForAll("sequences") ActionSequence<MyStringStack> actions) {
		actions.run(new MyStringStack());
	}

	@Provide
	Arbitrary<ActionSequence<MyStringStack>> sequences() {
		return Arbitraries.sequences(Arbitraries.oneOf(push(), pop(), clear()));
	}

	private Arbitrary<Action<MyStringStack>> push() {
		return Arbitraries.strings().alpha().ofLength(5).map(PushAction::new);
	}

	private Arbitrary<Action<MyStringStack>> clear() {
		return Arbitraries.just(new ClearAction());
	}

	private Arbitrary<Action<MyStringStack>> pop() {
		return Arbitraries.just(new PopAction());
	}
}
```

The interesting API elements are
- [`ActionSequence`](/docs/${docsVersion}/javadoc/net/jqwik/api/stateful/ActionSequence.html):
  A generic collection type especially crafted for holding and shrinking of a list of actions.
  As a convenience it will apply the actions to a state-based object when you call `run(state)`.

- [`Arbitraries.sequences()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Arbitraries.html#sequences(net.jqwik.api.Arbitrary)):
  This method will create the arbitrary for generating an `ActionSequence` given the
  arbitrary for generating actions.

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
org.opentest4j.AssertionFailedError: 
  Run failed after following actions:
      push(AAAAA)
      push(AAAAA)
      push(AAAAA)
      clear
    final state: ["AAAAA", "AAAAA"]
```

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

