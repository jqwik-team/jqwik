package net.jqwik;

import org.junit.gen5.engine.TestSource;
import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.gen5.engine.support.hierarchical.Leaf;
import org.opentest4j.AssertionFailedError;

public class JqwikPropertyDescriptor extends AbstractTestDescriptor implements Leaf<JqwikExecutionContext> {

    private final ExecutableProperty property;

    public JqwikPropertyDescriptor(UniqueId uniqueId, ExecutableProperty property, TestSource source) {
        super(uniqueId);
        this.property = property;
        setSource(source);
    }

    @Override
    public String getName() {
        return property.name();
    }

    @Override
    public String getDisplayName() {
        return property.name();
    }

    @Override
    public boolean isTest() {
        return true;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public JqwikExecutionContext execute(JqwikExecutionContext context) throws Exception {
        property.evaluate();
        return context;
    }
}
