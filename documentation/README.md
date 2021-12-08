# jqwik Documentation Module

## Requirements (for Mac)

- doctoc:
  `npm install -g doctoc`

## Generate

```bash
./gradlew documentation:generateDocumentation
```


## Update

0. Generate documentation. Mind jqwik version in `/build.gradle`
1. Move contents of folder `docs-XXX` to `/docs/docs/<version>`
   - If this is not a snapshot, copy `user-guide.md` to `/docs/docs/current`
2. Update "Latest Release: <version>" in `/docs/_layout/default.html`