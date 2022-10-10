# jqwik Documentation Module

## Requirements (for Mac)

- doctoc:
  `npm install -g doctoc`

## Generate

```bash
./gradlew documentation:generateDocumentation
```


## Update

0. Generate documentation. Mind jqwik version in file `/dependencies.gradle`
1. Move contents of folder `/documentation/build/docs-XXX` to `<jqwik.net root>/docs/<version>`
   - If this is not a snapshot: 
     - Copy `user-guide.md` to `<jqwik.net root>/docs/current`
     - Update "Latest Release: <version>" in `<jqwik.net root>/docs/_layout/default.html`
3. Push project `jqwik.net` to its GitHub repository, which will trigger the documentation generation action