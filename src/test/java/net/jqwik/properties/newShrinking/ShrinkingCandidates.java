package net.jqwik.properties.newShrinking;

import java.util.*;

public interface ShrinkingCandidates<T> {
	Set<T> candidatesFor(T toShrink);
}
