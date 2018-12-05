package net.jqwik.properties.shrinking;

import java.util.*;

public interface ShrinkingCandidates<T> {
	Set<T> candidatesFor(T toShrink);
}
