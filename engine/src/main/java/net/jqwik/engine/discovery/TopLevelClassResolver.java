package net.jqwik.engine.discovery;

import java.util.function.*;

import org.junit.platform.engine.support.discovery.*;

public class TopLevelClassResolver implements SelectorResolver {
	private Predicate<String> classNameFilter;

	public TopLevelClassResolver(Predicate<String> classNameFilter) {this.classNameFilter = classNameFilter;}
}
