package net.jqwik.engine.execution.lifecycle;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

class BeforeTryOnMemberVariableTests {

	@BeforeTry
	private int member = 41;

	@BeforeTry
	void testBefore() {
		System.out.println("before try outer");
	}

	@Property(tries = 10)
	void beforeTryOnMember(@ForAll @IntRange(min = 1, max = 100) int addend) {
		Assertions.assertThat(member).isEqualTo(41);
		member = member + addend;
	}

	@Group
	class InnerTests {

		@BeforeTry
		private int innerMember = 42;

		@BeforeTry
		void testBeforeInner() {
			System.out.println("before try inner");
		}

		@Property(tries = 10)
		void beforeTryOnMemberInInnerGroup(@ForAll @IntRange(min = 1, max = 100) int addend) {
			Assertions.assertThat(member).isEqualTo(41);
			member = member + addend;
			Assertions.assertThat(innerMember).isEqualTo(42);
			innerMember = innerMember + addend;
		}
	}
}
