package net.jqwik.engine.properties.stateful;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

public class ActionSequenceReportingFormat implements SampleReportingFormat {
	@Override
	public boolean appliesTo(Object value) {
		return value instanceof ActionSequence;
	}

	@Override
	public Object report(Object value) {
		ActionSequence sequence = (ActionSequence) value;
		return sequence.runActions();
	}

	@Override
	public Optional<String> label(Object value) {
		ActionSequence sequence = (ActionSequence) value;
		String label = null;
		if (sequence.runState() == ActionSequence.RunState.NOT_RUN) {
			label = String.format("ActionSequence[%s]: %s actions intended ", sequence.runState().name(), sequence.size());
		} else {
			label = String.format("ActionSequence[%s]: %s actions run ", sequence.runState().name(), sequence.size());
		}
		return Optional.ofNullable(label);
	}
}
