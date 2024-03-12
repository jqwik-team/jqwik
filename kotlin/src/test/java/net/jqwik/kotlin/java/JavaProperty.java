package net.jqwik.kotlin.java;

import net.jqwik.api.*;

class JavaProperty {

	@Property
	void aProperty(@ForAll int anInt) {
	}
}
