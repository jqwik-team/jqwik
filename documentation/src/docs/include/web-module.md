This module's artefact name is `jqwik-web`. It's supposed to provide arbitraries,
default generation and annotations for web related types. Currently only
[email generation](#email-address-generation) is supported.

This module is part of jqwik's default dependencies.


#### Email Address Generation

To generate email addresses you can either

- call up the static method [`Web.emails()`](/docs/${docsVersion}/javadoc/net/jqwik/web/api/Web.html#emails()).
  The return type is [`EmailArbitrary`](/docs/${docsVersion}/javadoc/net/jqwik/web/api/EmailArbitrary.html)
  which provides a few configuration methods.

- or use the [`@Email`](/docs/${docsVersion}/javadoc/net/jqwik/web/api/Email.html)
  annotation on `@ForAll` parameters as in the examples below.

An email address consists of two parts: `local-part` and `host`.
The complete email address is therefore `local-part@host`.
The `local-part` can be `unquoted` or `quoted` (in double quotes), which allows for more characters to be used.
The `host` can be a standard domain name, but also an IP (v4 or v6) address, surrounded by square brackets `[]`.

For example, valid email addresses are:
```
abc@example
abc@example.com
" "@example.example
"admin@server"@[192.168.201.0]
admin@[32::FF:aBc:79a:83B:FFFF:345]
```

You can use the following restrictions in `@Email` annotation:
- `unquotedLocalPart` to decide whether unquoted local parts are generated
- `quotedLocalPart` to decide whether quoted local parts are generated
- `domainHost` to decide whether domains are generated in the host part
- `ipv4Host` to decide whether ipv4 addresses are generated in the host part
- `ipv6Host` to decide whether ipv6 addresses are generated in the host part 
  
By default, only addresses with unquoted local part and domain hosts are 
generated (e.g. `me@myhost.com`), because many - if not most - applications 
and web forms only accept those.

You can use it as follows:

```java
@Property
void defaultEmailAddresses(@ForAll @Email String email) {
    assertThat(email).contains("@");
}

@Property
void restrictedEmailAddresses(@ForAll @Email(quotedLocalPart = true, ipv4Host = true, domainHost = false) String email) {
    assertThat(email).contains("@");
}
```
