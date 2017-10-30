package examples.docs;

import net.jqwik.api.*;

class TestsWithGroups {

	@Property(tries = 5)
	void outer(@ForAll String aString) {
	}

	@Group
	class Group1 {
		@Property(tries = 5)
		void group1Property(@ForAll String aString) {
		}

		@Group
		class Subgroup {
			@Property(tries = 5)
			void subgroupProperty(@ForAll String aString) {
			}
		}
	}

	@Group
	class Group2 {
		@Property(tries = 5)
		void group2Property(@ForAll String aString) {
		}
	}
}
