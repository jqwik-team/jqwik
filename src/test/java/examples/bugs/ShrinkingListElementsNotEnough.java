package examples.bugs;

import java.util.*;

import net.jqwik.api.*;

import static java.util.Collections.reverse;

class ShrinkingListElementsNotEnough {

	static <E> List<E> brokenReverse(List<E> aList) {
		if (aList.size() < 4) {
			aList = new ArrayList<>(aList);
			reverse(aList);
		}
		return aList;
	}

	@Property
	boolean reverseShouldSwapFirstAndLast(@ForAll List<Integer> aList) {
		Assume.that(!aList.isEmpty());
		List<Integer> reversed = brokenReverse(aList);
		return aList.get(0).equals(reversed.get(aList.size() - 1));
	}
}
