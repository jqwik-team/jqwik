- 1.2.1

  - Configuration for default generation mode

  - Character|StringArbitrary.excludeChars(char … chars)
  - Character|StringArbitrary.withChars(CharSequence)
  - StringArbitrary.append(Arbitrary<String>)
  - StringArbitrary.repeat(1, 10)
  - StringArbitrary.repeatAndJoin(1, 3, ".")
  - ListArbitrary<T>.reduce(R initial, Function2<R, T, R> reducer)

  - Lifecycle
    - Around container
    - Around try
    - Around engine
    - Storing values
  
  - Spring/Boot Testing in its own module
 

