package net.jqwik.docs;

import net.jqwik.api.*;

@Label("Naming")
class NamingExamples {

	@Property
	@Label("a property")
	void aPropertyWithALabel() { }

	@Group
	@Label("A Group")
	class GroupWithLabel {
		@Example
		@Label("an example äÄöÖüÜ")
		void anExampleWithALabel() { }
	}

	@Group
	class Group_with_spaces {
		@Example
		void example_with_spaces() { }
	}
}
