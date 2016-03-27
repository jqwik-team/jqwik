
package net.jqwik;

import org.junit.gen5.commons.util.ExceptionUtils;
import org.junit.gen5.engine.TestSource;
import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.gen5.engine.support.hierarchical.Leaf;

public class JqwikPropertyDescriptor extends AbstractTestDescriptor implements Leaf<JqwikExecutionContext> {

	private final PropertyStatement propertyStatement;

	public JqwikPropertyDescriptor(UniqueId uniqueId, PropertyStatement propertyStatement,
								   TestSource source) {
		super(uniqueId);
		this.propertyStatement = propertyStatement;
		setSource(source);
	}

	@Override
	public String getName() {
		return propertyStatement.getDisplayName();
	}

	@Override
	public String getDisplayName() {
		return propertyStatement.getDisplayName();
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
		}
		catch (Throwable throwable) {
			ExceptionUtils.throwAsUncheckedException(throwable);
		}
		return context;
	}

	@Override
	public SkipResult shouldBeSkipped(JqwikExecutionContext context) throws Exception {
		if (propertyStatement.hasAcceptedReturnType())
			return SkipResult.dontSkip();
		else
			return SkipResult.skip("Wrong Return Type");
	}

}
