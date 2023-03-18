package net.jqwik.docs.lifecycle;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

class BeforeTryMemberExample {

	@BeforeTry
	int theAnswer = 42;

	@Property
	void theAnswerIsAlways42(@ForAll int addend) {
		Assertions.assertThat(theAnswer).isEqualTo(42);
		theAnswer += addend;
	}
}
