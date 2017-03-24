
package experiments;

import static experiments.Grouping.*;

@Spec("A Stack")
public class UnnestedStackSpec {

	@Fact("can be created with constructor")
	void canBeCreatedWithNew() {

		//		assertAll:
		//			assertThat(new Stack()).isInstanceOf(Stack.class);
		//			assertThat(new Stack()).isInstanceOf(Stack.class);
	}

	@Fact("a new stack") void newStack() {
		group("is empty", (Stack aNewStack) -> {
			// assertThat(aNewStack.empty());
		});
	}


}

class Grouping {
	static void group(String label, Consumer<Stack> code){}
}
