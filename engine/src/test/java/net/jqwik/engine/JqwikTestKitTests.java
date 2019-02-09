package net.jqwik.engine;

import java.nio.file.*;
import java.util.*;

import examples.packageWithSingleContainer.*;
import org.junit.platform.engine.discovery.*;
import org.junit.platform.testkit.engine.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.testkit.engine.EventConditions.*;

/**
 * Migrating tests from {@linkplain TestEngineIntegrationTests}
 */
class JqwikTestKitTests {

	@Example
	void runTestsFromRootDir() {
		Set<Path> classpathRoots = JqwikReflectionSupport.getAllClasspathRootDirectories();
		ClasspathRootSelector[] classpathRootSelectors = selectClasspathRoots(classpathRoots)
															 .toArray(new ClasspathRootSelector[classpathRoots.size()]);
		Events events = EngineTestKit
							.engine("jqwik")
							.selectors(classpathRootSelectors)
							.filters(PackageNameFilter.includePackageNames("examples.packageWithSingleContainer"))
							.execute()
							.all();

		assertSimpleExampleTests(events);
	}

	@Example
	void runTestsFromPackage() {
		Events events = EngineTestKit
							.engine("jqwik")
							.selectors(selectPackage("examples.packageWithSingleContainer"))
							.execute()
							.all();

		assertSimpleExampleTests(events);
	}

	@Example
	void runTestsFromClass() {
		Events events = EngineTestKit
							.engine("jqwik")
							.selectors(selectClass(SimpleExampleTests.class))
							.execute()
							.all();

		assertSimpleExampleTests(events);
	}

	private void assertSimpleExampleTests(Events events) {
		events.assertEventsMatchExactly(
			event(engine(), started()),
			event(container(SimpleExampleTests.class), started()),
			event(test("failing"), started()),
			event(test("failing"), finishedWithFailure()),
			event(test("withJupiterAnnotation"), skippedWithReason(s -> true)),
			event(test("staticExample"), skippedWithReason(s -> true)),
			event(test("succeeding"), started()),
			event(test("succeeding"), finishedSuccessfully()),
			event(container(SimpleExampleTests.class), finishedSuccessfully()),
			event(engine(), finishedSuccessfully())
		);
	}

}
