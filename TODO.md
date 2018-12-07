- For version 1.0

  - Remove compile-time dependencies from API to ENGINE module
    - Arbitraries
    - Arbitrary
    - Combinators
    - ExhaustiveGenerator
    - RandomGenerator
    - ShrinkingSequence

  - Move net.jqwik.engine to module engine

  - Extract docs module
    - examples.docs tests -> net.jqwik.docs
    - Change github links in user-guide

  - JavaDoc
    - Statistics

  - `@Disabled("reason")` annotation

  - Review TODOs

  - Move release notes to their own web page
    Alternative: Use asciidoc to generate user guide

  - Use apiguardian annotations (starting version 1.0)

  - Use junit-platform-testkit for engine integration tests