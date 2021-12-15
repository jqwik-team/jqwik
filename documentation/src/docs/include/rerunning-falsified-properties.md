When you rerun properties after they failed, they will - by default - use
the previous random seed so that the next run will generate the exact same
sequence of parameter data and thereby expose the same failing behaviour. 
This simplifies debugging and regression testing since it makes a property's falsification
stick until the problem has been fixed.

If you want to, you can change this behaviour for a given property like this:

```java
@Property(afterFailure = AfterFailureMode.PREVIOUS_SEED)
void myProperty() { ... }
```

The `afterFailure` property can have one of four values:

- `AfterFailureMode.PREVIOUS_SEED`: Choose the same seed that provoked the failure in the first place.
  Provided no arbitrary provider code has been changed, this will generate the same
  sequence of generated parameters as the previous test run.

- `AfterFailureMode.RANDOM_SEED`: Choose a new random seed even after failure in the previous run.
  A constant seed will always prevail thought, as in the following example:

  ```java
  @Property(seed = "424242", afterFailure = AfterFailureMode.RANDOM_SEED)
  void myProperty() { ... }
  ```

- `AfterFailureMode.SAMPLE_ONLY`: Only run the property with just the last falsified (and shrunk) generated sample set of parameters. 
  This only works if generation and shrinking will still lead to the same results as in the previous failing run.
  If the previous sample cannot be reproduced the property will restart with the previous run's random seed.

- `AfterFailureMode.SAMPLE_FIRST`: Same as `SAMPLE_ONLY` but generate additional examples if the
  property no longer fails with the previous sample.


You can also determine the default behaviour of all properties by setting
the `jqwik.failures.after.default` parameter in the [configuration file](#jqwik-configuration)
to one of those enum values.

