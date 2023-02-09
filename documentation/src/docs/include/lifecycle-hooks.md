### Lifecycle Hooks

Similar to [Jupiter's Extension Model](https://junit.org/junit5/docs/current/user-guide/#extensions)
_jqwik_ provides a means to extend and change the way how properties and containers are being
configured, run and reported on. The API -- interfaces, classes and annotations -- for accessing
those _lifecycle hooks_ lives in the package `net.jqwik.api.lifecycle` and is -- as of this release --
are now mostly in the [API evolution status](#api-evolution) `MAINTAINED`.

#### Principles of Lifecycle Hooks

There are a few fundamental principles that determine and constrain the lifecycle hook API:

1. There are several [types of lifecycle hooks](#lifecycle-hook-types),
   each of which is an interface that extends
   [`net.jqwik.api.lifecycle.LifecycleHook`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/LifecycleHook.html).
2. A concrete lifecycle hook is an implementation of one or more lifecycle hook interfaces.
3. You can add a concrete lifecycle hook to a container class or a property method with the annotation
   [`@AddLifecycleHook`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/AddLifecycleHook.html).
   By default, a lifecycle hook is only added to the annotated element, not to its children.
   However, you can override this behaviour by either:
    - Override `LifecycleHook.propagateTo()`
    - Use the annotation attribute `@AddLifecycleHook.propagateTo()`
4. To add a global lifecycle use Java’s `java.util.ServiceLoader` mechanism and add the concrete lifecylcle hook
   class to file `META-INF/services/net.jqwik.api.lifecycle.LifecycleHook`.
   Do not forget to override `LifecycleHook.propagateTo()` if the global hook should be applied to all test elements.
5. In a single test run there will only be a single instance of each concrete lifecycle hook implementation.
   That's why you have to use jqwik's [lifecycle storage](#lifecycle-storage) mechanism if shared state
   across several calls to lifecycle methods is necessary.
6. Since all instances of lifecycle hooks are created before the whole test run is started,
   you cannot use non-static inner classes of test containers to implement lifecycle interfaces.
7. If relevant, the order in which hook methods are being applied is determined by dedicated methods
   in the hook interface, e.g.
   [`BeforeContainerHook.beforeContainerProximity()`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/BeforeContainerHook.html#beforeContainerProximity()).

Mind that much of what you can do with hooks can also be done using the simpler
mechanisms of [annotated lifecycle methods](#annotated-lifecycle-methods) or
a [property lifecycle class](#single-property-lifecycle).
You usually start to consider using lifecycle hooks when you want to
reuse generic behaviour in many places or even across projects.


#### Lifecycle Hook Types

All lifecycle hook interfaces extend `net.jqwik.api.lifecycle.LifecycleHook` which
has two methods that may be overridden:

- [`propagateTo()`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/LifecycleHook.html#propagateTo()):
  Determine if and how a hook will be propagated to an element's children.

- [`appliesTo(Optional<AnnotatedElement>)`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/LifecycleHook.html#appliesTo(java.util.Optional)):
  Determine if a hook will be applied to a concrete element. For example, you might want to constrain a certain hook
  to apply only to property methods and not to containers:

  ```java
  @Override
  public boolean appliesTo(final Optional<AnnotatedElement> element) {
      return element
          .map(annotatedElement -> annotatedElement instanceof Method)
          .orElse(false);
  }
  ```

_jqwik_ currently supports eight types of lifecycle hooks:

- [Lifecycle execution hooks](#lifecycle-execution-hooks):
    - `SkipExecutionHook`
    - `BeforeContainerHook`
    - `AfterContainerHook`
    - `AroundContainerHook`
    - `AroundPropertyHook`
    - `AroundTryHook`
    - `InvokePropertyMethodHook`
    - `ProvidePropertyInstanceHook`

- [Other hooks](#other-hooks)
    - `ResolveParameterHook`
    - `RegistrarHook`

#### Lifecycle Execution Hooks

With these hooks you can determine if a test element will be run at all,
and what potential actions should be done before or after running it.

##### SkipExecutionHook


Implement [`SkipExecutionHook`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/SkipExecutionHook.html)
to filter out a test container or property method depending on some runtime condition.

Given this hook implementation:

```java
public class OnMacOnly implements SkipExecutionHook {
    @Override
    public SkipResult shouldBeSkipped(final LifecycleContext context) {
        if (System.getProperty("os.name").equals("Mac OS X")) {
            return SkipResult.doNotSkip();
        }
        return SkipResult.skip("Only on Mac");
    }
}
```

The following property will only run on a Mac:

```java
@Property
@AddLifecycleHook(OnMacOnly.class)
void macSpecificProperty(@ForAll int anInt) {
}
```

##### BeforeContainerHook

Implement [`BeforeContainerHook`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/BeforeContainerHook.html)
for a hook that's supposed to do some work exactly once before any of its property methods and child containers
will be run.
This is typically used to set up a resource to share among all properties within this container.

##### AfterContainerHook

Implement [`AfterContainerHook`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/AfterContainerHook.html)
for a hook that's supposed to do some work exactly once after all of its property methods and child containers
have been run.
This is typically used to tear down a resource that has been shared among all properties within this container.

##### AroundContainerHook

[`AroundContainerHook`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/AroundContainerHook.html)
is a convenience interface to implement both [`BeforeContainerHook`](#beforecontainerhook) and
[`AfterContainerHook`](#aftercontainerhook) in one go.
This is typically used to set up and tear down a resource that is intended to be shared across all the container's children.

Here's an example that shows how to start and stop an external server once for all
properties of a test container:

```java
@AddLifecycleHook(ExternalServerResource.class)
class AroundContainerHookExamples {
    @Example
    void example1() {
        System.out.println("Running example 1");
    }
    @Example
    void example2() {
        System.out.println("Running example 2");
    }
}

class ExternalServerResource implements AroundContainerHook {
    @Override
    public void beforeContainer(final ContainerLifecycleContext context) {
        System.out.println("Starting server...");
    }
  
    @Override
    public void afterContainer(final ContainerLifecycleContext context) {
        System.out.println("Stopping server...");
    }
}
```

Running this example should output

```
Starting server...

Running example 1

Running example 2

Stopping server...
```

If you wanted to do something before and/or after _the whole jqwik test run_,
using a container hook and registering it globally is probably the easiest way.

##### AroundPropertyHook

[`AroundPropertyHook`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/AroundPropertyHook.html)
comes in handy if you need to define behaviour that should "wrap" the execution of a property,
i.e., do something directly before or after running a property - or both.
Since you have access to an object that describes the final result of a property
you can also change the result, e.g. make a failed property successful or vice versa.

Here is a hook implementation that will measure the time spent on running a property
and publish the result using a [`Reporter`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/Reporter.html):

```java
@Property(tries = 100)
@AddLifecycleHook(MeasureTime.class)
void measureTimeSpent(@ForAll Random random) throws InterruptedException {
    Thread.sleep(random.nextInt(50));
}

class MeasureTime implements AroundPropertyHook {
    @Override
    public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
        long before = System.currentTimeMillis();
        PropertyExecutionResult executionResult = property.execute();
        long after = System.currentTimeMillis();
        context.reporter().publish("time", String.format("%d ms", after - before));
        return executionResult;
    }
}
```

The additional output from reporting is concise:

```
timestamp = ..., time = 2804 ms
```

##### AroundTryHook

Wrapping the execution of a single try can be achieved by implementing
[`AroundTryHook`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/AroundTryHook.html).
This hook can be used for a lot of things. An incomplete list:

- Closely watch each execution of a property method
- Reset a resource for each call
- Swallow certain exceptions
- Filter out tricky (and invalid) parameter constellations
- Let a try fail depending on external circumstances

The following example shows how to fail if a single try will take longer than 100 ms:

```java
@Property(tries = 10)
@AddLifecycleHook(FailIfTooSlow.class)
void sleepingProperty(@ForAll Random random) throws InterruptedException {
    Thread.sleep(random.nextInt(101));
}

class FailIfTooSlow implements AroundTryHook {
    @Override
    public TryExecutionResult aroundTry(
        final TryLifecycleContext context,
        final TryExecutor aTry,
        final List<Object> parameters
    ) {
        long before = System.currentTimeMillis();
        TryExecutionResult result = aTry.execute(parameters);
        long after = System.currentTimeMillis();
        long time = after - before;
        if (time >= 100) {
            String message = String.format("%s was too slow: %s ms", context.label(), time);
            return TryExecutionResult.falsified(new AssertionFailedError(message));
        }
        return result;
    }
}
```

Since the sleep time is chosen randomly the property will fail from time to time
with the following error:

```
org.opentest4j.AssertionFailedError: sleepingProperty was too slow: 100 ms
```

##### InvokePropertyMethodHook

This is an experimental hook, which allows to change the way how a method -
represented by a `java.lang.reflect.Method` object - is being invoked 
through reflection mechanisms.

##### ProvidePropertyInstanceHook

This is an experimental hook, which allows to change the way how the instance
of a container class is created or retrieved.

#### Other Hooks

##### ResolveParameterHook

Besides the well-known `@ForAll`-parameters, property methods and [annotated lifecycle methods](#annotated-lifecycle-methods)
can take other parameters as well. These can be injected by concrete implementations of
[`ResolveParameterHook`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/ResolveParameterHook.html).

Consider this stateful `Calculator`:

```java
public class Calculator {
    private int result = 0;
  
    public int result() {
        return result;
    }
  
    public void plus(int addend) {
        result += addend;
    }
}
```

When going to check its behaviour with properties you'll need a fresh calculator instance
in each try. This can be achieved by adding a resolver hook that creates a freshly
instantiated calculator per try.

```java
@AddLifecycleHook(CalculatorResolver.class)
class CalculatorProperties {
    @Property
    void addingANumberTwice(@ForAll int aNumber, Calculator calculator) {
        calculator.plus(aNumber);
        calculator.plus(aNumber);
        Assertions.assertThat(calculator.result()).isEqualTo(aNumber * 2);
    }
}

class CalculatorResolver implements ResolveParameterHook {
    @Override
    public Optional<ParameterSupplier> resolve(
        final ParameterResolutionContext parameterContext,
        final LifecycleContext lifecycleContext
    ) {
        return Optional.of(optionalTry -> new Calculator());
    }
    @Override
    public PropagationMode propagateTo() {
        // Allow annotation on container level
        return PropagationMode.ALL_DESCENDANTS;
    }
}
```

There are a few constraints regarding parameter resolution of which you should be aware:

- Parameters annotated with `@ForAll` or with `@ForAll` present as a meta annotation
  (see [Self-Made Annotations](#self-made-annotations)) cannot be resolved;
  they are fully controlled by jqwik's arbitrary-based generation mechanism.
- If more than one applicable hook returns a non-empty instance of `Optional<ParameterSupplier>`
  the property will throw an instance of `CannotResolveParameterException`.
- If you want to keep the same object around to inject it in more than a single method invocation,
  e.g. for setting it up in a `@BeforeTry`-method, you are supposed to use jqwik's
  [lifecycle storage mechanism](#lifecycle-storage).


##### RegistrarHook

Use [`RegistrarHook`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/RegistrarHook.html)
if you need to apply several hook implementations that implement the desired behaviour together
but cannot be implemented in a single class.
For example, more than one implementation of the same hook type is needed,
but those implementations have a different proximity or require a different propagation mode.

This is really advanced stuff, the mechanism of which will probably evolve or change in the future.
If you really really want to see an example, look at
[`JqwikSpringExtension`](https://github.com/jqwik-team/jqwik-spring/blob/main/src/main/java/net/jqwik/spring/JqwikSpringExtension.java)

#### Lifecycle Storage

As [described above](#principles-of-lifecycle-hooks) one of the fundamental principles
is that there will be only a single instance of any lifecycle hook implementation
during runtime.
Since -- depending on configuration and previous rung -- containers and properties are
not run in a strict sequential order this guarantee comes with a drawback:
You cannot use a hook instance's member variables to hold state that should be shared
across all tries of a property or across all properties of a container or across
different lifecycle phases of a single try.
That's when lifecycle storage management enters the stage in the form of type
[`net.jqwik.api.lifecycle.Store`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/Store.html).

A `Store` object...

- holds a single piece of shared state
- has a _globally unique identifier_ of your choice.
  The identifier can be just a string or you compose whatever you deem necessary to make it unique.
- has a [`Lifespan`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/Lifespan.html).
  The lifespan determines when the initializer of a store will be called:
    - `Lifespan.RUN`: Only on first access
    - `Lifespan.PROPERTY`: On first access of each single property method (or one of its lifecycle hook methods)
    - `Lifespan.TRY`: On first access of each single try (or one of its lifecycle hook methods)

You create a store like this:

```java
Store<MyObject> myObjectStore = Store.create("myObjectStore", Lifespan.PROPERTY, () -> new MyObject());
```

And you retrieve a store similarly:

```java
Store<MyObject> myObjectStore = Store.get("myObjectStore");
```

A store with the same identifier can only be created once, that's why there are also convenience
methods for creating or retrieving it:

```java
Store<MyObject> myObjectStore = Store.getOrCreate("myObjectStore", Lifespan.PROPERTY, () -> new MyObject());
```

You now have the choice to use or update the shared state:

```java
Store<MyObject> myObjectStore = ...;

myObjectStore.get().doSomethingWithMyObject();
myObjectStore.update(old -> {
    old.changeState();
    return old;
});
```

Let's look at an example...

##### TemporaryFileHook

The following hook implementation gives you the capability to access _one_ (and only one)
temporary file per try using [parameter resolution](#resolveparameterhook):

```java
class TemporaryFileHook implements ResolveParameterHook {

    public static final Tuple.Tuple2 STORE_IDENTIFIER = Tuple.of(TemporaryFileHook.class, "temporary files");

    @Override
    public Optional<ParameterSupplier> resolve(
        ParameterResolutionContext parameterContext,
        LifecycleContext lifecycleContext
    ) {
        if (parameterContext.typeUsage().isOfType(File.class)) {
            return Optional.of(ignoreTry -> getTemporaryFileForTry());
        }
        return Optional.empty();
    }

    private File getTemporaryFileForTry() {
        Store<ClosingFile> tempFileStore =
            Store.getOrCreate(
                STORE_IDENTIFIER, Lifespan.TRY,
                () -> new ClosingFile(createTempFile())
            );
        return tempFileStore.get().file();
    }

    private File createTempFile() {
        try {
            return File.createTempFile("temp", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private record ClosingFile(File file) implements Store.CloseOnReset {
        @Override
        public void close() {
            file.delete();
        }
    }
}
```

There are a few interesting things going on:

- The identifier is a tuple of the `TemporaryFileHook.class` object and a string.
  This makes sure that no other hook will use the same identifier accidentally.
- The temporary file is created only once per try.
  That means that all parameters in the scope of this try will contain _the same file_.
- The file is wrapped in a class that implements `Store.CloseOnReset`.
  We do this to make sure that the temporary file will be deleted
  as soon as the store is going out of scope - in this case after each try.

With this information you can probably figure out how the following test container works --
especially why the assertion in `@AfterTry`-method `assertFileNotEmpty()` succeeds.

```java
@AddLifecycleHook(value = TemporaryFileHook.class, propagateTo = PropagationMode.ALL_DESCENDANTS)
class TemporaryFilesExample {
    @Property(tries = 10)
    void canWriteToFile(File anyFile, @ForAll @AlphaChars @StringLength(min = 1) String fileContents) throws Exception {
        assertThat(anyFile).isEmpty();
        writeToFile(anyFile, fileContents);
        assertThat(anyFile).isNotEmpty();
    }
  
    @AfterTry
    void assertFileNotEmpty(File anyFile) {
        assertThat(anyFile).isNotEmpty();
    }
  
    private void writeToFile(File anyFile, String contents) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(anyFile));
        writer.write(contents);
        writer.close();
    }
}
```

