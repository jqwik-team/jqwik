package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.util.*;

public interface ArbitraryBuilder {
	Arbitrary build(Object testInstance, Map<TypeUsage, ArbitraryBuilder> subtypeBuilders);
}
