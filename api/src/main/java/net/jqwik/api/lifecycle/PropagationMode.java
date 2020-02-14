package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.2.4")
public enum PropagationMode {
	DEFAULT, ALL_DESCENDANTS, NO_ONE
}
