package net.jqwik.api.configurators;

import java.lang.annotation.*;
import java.util.*;

import net.jqwik.api.*;

public interface ArbitraryConfigurator {

	Arbitrary<?> configure(Arbitrary<?> arbitrary, List<Annotation> annotations);
}
