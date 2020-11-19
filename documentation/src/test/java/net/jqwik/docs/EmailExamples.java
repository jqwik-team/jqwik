package net.jqwik.docs;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

class EmailExamples {

	@Property
	void defaultEmailAddresses(@ForAll @Email String email) {
		assertThat(email).contains("@");
	}

	@Property
	void restrictedEmailAddresses(@ForAll @Email(quotedLocalPart = false, ipv4Address = false, ipv6Address = false) String email) {
		assertThat(email).contains("@");
	}
}
