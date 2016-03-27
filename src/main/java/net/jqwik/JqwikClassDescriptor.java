package net.jqwik;

import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.gen5.engine.support.descriptor.JavaSource;
import org.junit.gen5.engine.support.hierarchical.Container;

public class JqwikClassDescriptor extends AbstractTestDescriptor implements Container<JqwikExecutionContext> {


	private final Class<?> testClass;

    public JqwikClassDescriptor(UniqueId uniqueId, Class<?> testClass) {
        super(uniqueId);
        this.testClass = testClass;
        setSource(new JavaSource(testClass));
    }

    @Override
    public String getName() {
        return testClass.getName();
    }

    @Override
    public String getDisplayName() {
        return testClass.getSimpleName();
    }

    @Override
    public boolean isTest() {
        return false;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

	public Class<?> getTestClass() {
		return testClass;
	}

}
