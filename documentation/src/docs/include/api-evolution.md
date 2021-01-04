In agreement with the JUnit 5 platform _jqwik_ uses the
[@API Guardian project](https://github.com/apiguardian-team/apiguardian)
to communicate version and status of all parts of its API.
The different types of status are:

-`STABLE`: Intended for features that will not be changed in a backwards-incompatible way in the current major version (1.*).

-`MAINTAINED`: Intended for features that will not be changed in a backwards-incompatible way for at least the current minor release of the current major version. If scheduled for removal, it will be demoted to `DEPRECATED` first.

-`EXPERIMENTAL`: Intended for new, experimental features where we are looking for feedback. Use this element with caution; it might be promoted to `MAINTAINED` or `STABLE` in the future, but might also be removed without prior notice, even in a patch.

-`DEPRECATED`: Should no longer be used; might disappear in the next minor release.

-`INTERNAL`: Must not be used by any code other than _jqwik_ itself. Might be removed without prior notice.

Since annotation `@API` has runtime retention you find the actual API status in an element's source code,
its [Javadoc](/docs/${docsVersion}/javadoc) but also through reflection.
If a certain element, e.g. a method, is not annotated itself, then it carries the status of its containing class.


