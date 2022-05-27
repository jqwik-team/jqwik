package examples.bugs;

import java.util.*;

import net.jqwik.api.*;

class ShrinkingListElementsNotEnough {

	static <E> List<E> brokenReverse(List<E> aList) {
		if (aList.size() < 4) {
			aList = new ArrayList<>(aList);
			Collections.reverse(aList);
		}
		return aList;
	}

	@Property
	boolean reverseShouldSwapFirstAndLast(@ForAll List<Integer> aList) {
		Assume.that(!aList.isEmpty());
		List<Integer> reversed = brokenReverse(aList);
		return aList.get(0).equals(reversed.get(aList.size() - 1));
	}

	private <T> List<T> reverseWithoutDuplicates(List<T> original) {
		List<T> clone = new ArrayList<>(new LinkedHashSet<>(original));
		Collections.reverse(clone);
		return clone;
	}

	@Property
	//@Report(Reporting.FALSIFIED)
	boolean shouldShrinkToListOf0and0(@ForAll List<Integer> original) {
		List<Integer> reversed = reverseWithoutDuplicates(original);
		return original.size() == reversed.size();
	}

}
