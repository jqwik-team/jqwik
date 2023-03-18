package net.jqwik.engine.execution.lifecycle;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

class BeforeTryOnMemberVariableTests {

	//@BeforeTry // will lead to exception since static fields must not use @BeforeTry
	private static final int staticMember = 40;

	private BeforeTryOnMemberVariableTests() {
		memberInitializedInConstructor = 99;
	}

	@BeforeTry
	private int member = 41;

	@BeforeTry
	private int memberInitializedInConstructor;

	@Property(tries = 10)
	void memberInitializedInDeclaration(@ForAll @IntRange(min = 1, max = 100) int addend) {
		Assertions.assertThat(member).isEqualTo(41);
		member = member + addend;
	}

	@Property(tries = 10)
	void memberInitializedInConstructor(@ForAll @IntRange(min = 1, max = 100) int addend) {
		Assertions.assertThat(memberInitializedInConstructor).isEqualTo(99);
		memberInitializedInConstructor = memberInitializedInConstructor + addend;
	}

	@Group
	class InnerTests {

		@BeforeTry
		private int innerMember = 42;

		@Property(tries = 10)
		void beforeTryOnMemberInInnerGroup(@ForAll @IntRange(min = 1, max = 100) int addend) {
			Assertions.assertThat(member).isEqualTo(41);
			member = member + addend;
			Assertions.assertThat(innerMember).isEqualTo(42);
			innerMember = innerMember + addend;
		}
	}
}
