- Table-driven properties, similar to what is described in
  http://www.scalatest.org/user_guide/table_driven_property_checks

  Possible syntax:

  ```
  @Provide
  Table<Integer, String> representations() {
      return Table.of(
            Tuple.of(1, "1"),
            Tuple.of(2, "2"),
            Tuple.of(3, "Fizz"),
            Tuple.of(4, "4"),
            Tuple.of(5, "Buzz")
      );
  }
  ```

