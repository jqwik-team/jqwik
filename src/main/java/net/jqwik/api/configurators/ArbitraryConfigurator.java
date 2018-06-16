package net.jqwik.api.configurators;

import java.lang.annotation.*;
import java.util.*;

import net.jqwik.api.*;

public interface ArbitraryConfigurator {

	<T> Arbitrary<T> configure(Arbitrary<T> arbitrary, List<Annotation> annotations);
}
