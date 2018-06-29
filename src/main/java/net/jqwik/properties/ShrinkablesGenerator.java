package net.jqwik.properties;

import net.jqwik.api.*;

import java.util.*;

public interface ShrinkablesGenerator {
	List<Shrinkable> next(Random random);
}
