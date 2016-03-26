package net.jqwik;

import org.junit.gen5.commons.util.ExceptionUtils;
import org.junit.gen5.engine.TestSource;
import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.gen5.engine.support.hierarchical.Leaf;

public class JqwikPropertyDescriptor extends AbstractTestDescriptor implements Leaf<JqwikExecutionContext> {

    private final String name;
	private final PropertyStatement propertyStatement;

    public JqwikPropertyDescriptor(UniqueId uniqueId, String name, PropertyStatement propertyStatement, TestSource source) {
        super(uniqueId);
        this.name = name;
		this.propertyStatement = propertyStatement;
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
		try {
			propertyStatement.evaluate();
		} catch (Throwable throwable) {
			ExceptionUtils.throwAsUncheckedException(throwable);
		}
		return context;
    }
}
