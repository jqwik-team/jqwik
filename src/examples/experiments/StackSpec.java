
package experiments;


import java.util.EmptyStackException;
import java.util.Stack;

@Spec("A Stack")
public class StackSpec {

	@Fact("can be created with constructor")
	void canBeCreatedWithNew() {

//		assertAll:
//			assertThat(new Stack()).isInstanceOf(Stack.class);
//			assertThat(new Stack()).isInstanceOf(Stack.class);
	}

	@Group("when it is new")
	class NewStack {

		@Fixture
		Stack aNewStack() {
			return new Stack();
		}

		@Fact
		boolean itIsEmpty(Stack aNewStack) {
			return aNewStack.isEmpty();
		}

		@Fact
		void poppingThrowsException(Stack aNewStack) {
//			assertThatExceptionOfType(EmptyStackException.class).isThrownBy(()-> aNewStack.pop());
		}
		@Fact
		void twoThingsHappen(Stack aNewStack) {
//			all:
//				assertThat(aNewStack).isEmpty();
//				assertThatExceptionOfType(EmptyStackException.class).isThrownBy(()-> aNewStack.pop());
		}

	}

}
