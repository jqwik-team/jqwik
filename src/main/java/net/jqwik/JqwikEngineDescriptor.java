/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package net.jqwik;

import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.support.descriptor.EngineDescriptor;

public class JqwikEngineDescriptor extends EngineDescriptor {

	public JqwikEngineDescriptor(UniqueId uniqueId) {
		super(uniqueId, "Jqwik");
	}

}
