# Contributing

## jqwik Contributor Agreement

- You have authored 100% of the contents of your contribution.
  Among other things that means _that you have not used GitHub Copilot or a similar LLM_ to create all or parts of your contribution!
  The reason is that the copyright consequences of training an LLM with mostly public code repositories have not been clarified.
- You have the necessary rights for all your contributions.
  If you act as an employee, you have attained the necessary permissions from your employer to contribute.
- Your contribution will be provided under the project's license.

### Project License

_jqwik_ currently uses the [Eclipse Public License v2.0](./LICENSE.md).

## Pull Requests

Please add the following lines to your pull request description:

```markdown
---

I hereby agree to the terms of the jqwik Contributor Agreement.
```

## Coding Conventions

__Work in progress but nevertheless strictly enforced._

Using [`.editorconfig`](./.editorconfig) will simplify your life when formatting jqwik code.

### Everywhere

Use only wildcard imports.

### Tests

Use _jqwik_ itself for all tests and properties.

Use _AssertJ_ for non-trivial assertions.

Use `@ForAll Random random` parameter if you need a random value. 
