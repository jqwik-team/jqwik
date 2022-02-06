package experiments;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

import static net.jqwik.api.Arbitraries.*;

class CompositeActionExperiments {

	@Property
	void neverLongerThan10(@ForAll("actions") ActionSequence<String> actions) {
		String result = actions.run("");
		// This will fail since strings of length 10 can be created
		Assertions.assertThat(result).hasSizeLessThan(10);
	}

	@Provide
	Arbitrary<ActionSequence<String>> actions() {
		Arbitrary<String> toAdd = strings().alpha().ofMinLength(1).ofMaxLength(3);
		Arbitrary<Integer> toRemove = integers().between(1, 2);
		Arbitrary<Action<String>> addThenRemove =
			Combinators.combine(toAdd, toRemove)
					   .as((add, remove) -> new CompositeAction<>(new AddAction(add), new RemoveAction(remove)));

		return sequences(oneOf(
			toAdd.map(AddAction::new),
			toRemove.map(RemoveAction::new),
			addThenRemove
		));
	}
}

class CompositeAction<T> implements Action<T> {

	private final Action<T> first;
	private final Action<T> second;

	public CompositeAction(Action<T> first, Action<T> second) {this.first = first;
		this.second = second;
	}

	@Override
	public boolean precondition(T state) {
		return first.precondition(state);
	}

	@Override
	public T run(T state) {
		T stateAfterFirst = first.run(state);
		if (second.precondition(stateAfterFirst)) {
			return second.run(stateAfterFirst);
		} else {
			return stateAfterFirst;
		}
	}

	@Override
	public String toString() {
		return String.format("[%s then %s]", first, second);
	}
}

class AddAction implements Action<String> {

	private final String letter;

	AddAction(String toAdd) {
		this.letter = toAdd;
	}

	@Override
	public boolean precondition(String state) {
		return state.length() + letter.length() <= 10;
	}

	@Override
	public String run(String state) {
		return state + letter;
	}

	@Override
	public String toString() {
		return String.format("Add: %s", letter);
	}
}

class RemoveAction implements Action<String> {

	private final int count;

	RemoveAction(int count) {
		this.count = count;
	}

	@Override
	public boolean precondition(String state) {
		return state.length() >= count;
	}

	@Override
	public String run(String state) {
		return state.substring(0, state.length() - count);
	}

	@Override
	public String toString() {
		return String.format("Remove: %s", count);
	}

}