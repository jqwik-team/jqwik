package examples.docs;

import net.jqwik.api.*;

@Label("Labeling")
class LabelingExamples {

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
}
