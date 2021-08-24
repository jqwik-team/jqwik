package net.jqwik.docs.web;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.web.api.*;

class WebDomainExamples {

	@Property
	@Report(Reporting.GENERATED)
	void topLevelDomainCannotHaveSingleLetter(@ForAll @WebDomain String domain) {
		int lastDot = domain.lastIndexOf('.');
		String tld = domain.substring(lastDot + 1);
		Assertions.assertThat(tld).hasSizeGreaterThan(1);
	}
}
