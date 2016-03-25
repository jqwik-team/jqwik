package net.jqwik;

import org.junit.gen5.engine.TestSource;
import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.gen5.engine.support.hierarchical.Leaf;
import org.opentest4j.AssertionFailedError;

public class JqwikPropertyDescriptor extends AbstractTestDescriptor implements Leaf<JqwikExecutionContext> {

    private final UniqueId uniqueId;
    private final String name;
    private final TestSource source;

    public JqwikPropertyDescriptor(UniqueId uniqueId, String name, TestSource source) {
        super(uniqueId);
        this.uniqueId = uniqueId;
        this.name = name;
        this.source = source;
        setSource(source);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return name;
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
        return context;
    }
}
