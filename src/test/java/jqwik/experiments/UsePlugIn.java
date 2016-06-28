package jqwik.experiments;

public @interface UsePlugIn {
	Class<? extends SpecPlugin> value();
}
