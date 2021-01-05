package net.jqwik.docs;

import net.jqwik.api.*;
import net.jqwik.web.api.*;

import static org.assertj.core.api.Assertions.*;

class EmailExamples {

	@Property
	void defaultEmailAddresses(@ForAll @Email String email) {
		assertThat(email).contains("@");
	}

	@Property
	void restrictedEmailAddresses(@ForAll @Email(quotedLocalPart = false, ipv4Host = false, ipv6Host = false) String email) {
		assertThat(email).contains("@");
	}
}
