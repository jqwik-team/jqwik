__jqwik__ is an alternative test engine for the
[JUnit 5 platform](https://junit.org/junit5/docs/current/user-guide/#launcher-api-engines-custom).
That means that you can use it either stand-alone or combine it with any other JUnit 5 engine, e.g.
[Jupiter (the standard engine)](https://junit.org/junit5/docs/current/api/org.junit.jupiter.engine/org/junit/jupiter/engine/JupiterTestEngine.html) or
[Vintage (aka JUnit 4)](https://junit.org/junit5/docs/current/api/org.junit.vintage.engine/org/junit/vintage/engine/VintageTestEngine.html).
All you have to do is add all needed engines to your `testImplementation` dependencies as shown in the
[gradle file](#gradle) below.

The latest release of __jqwik__ is deployed to [Maven Central](https://search.maven.org/search?q=g:net.jqwik).
Snapshot releases are created on a regular basis and can be fetched from 
[jqwik's snapshot repository](https://s01.oss.sonatype.org/content/repositories/snapshots). 

### Required Version of JUnit Platform

The minimum required version of the JUnit platform is `${junitPlatformVersion}`.

### Gradle

Since version 4.6, Gradle has
[built-in support for the JUnit platform](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.testing.Test.html).
Set up is rather simple; here are the relevant parts of a project's `build.gradle` file:


```
repositories {
    ...
    mavenCentral()

    # For snapshot releases only:
    maven { url 'https://s01.oss.sonatype.org/content/repositories/snapshots' }

}

ext.junitJupiterVersion = '${junitJupiterVersion}'
ext.jqwikVersion = '${version}'

compileTestJava {
    // To enable argument names in reporting and debugging
	options.compilerArgs += '-parameters'
}

test {
	useJUnitPlatform {
		includeEngines 'jqwik'
        
        // Or include several Junit engines if you use them
        // includeEngines 'jqwik', 'junit-jupiter', 'junit-vintage'

		// includeTags 'fast', 'medium'
		// excludeTags 'slow'
	}

	include '**/*Properties.class'
	include '**/*Test.class'
	include '**/*Tests.class'
}

dependencies {
    ...

    // aggregate jqwik dependency
    testImplementation "net.jqwik:jqwik:\${jqwikVersion}"

    // Add if you also want to use the Jupiter engine or Assertions from it
    testImplementation "org.junit.jupiter:junit-jupiter:\${junitJupiterVersion}"

    // Add any other test library you need...
    testImplementation "org.assertj:assertj-core:3.23.1"

    // Optional but recommended to get annotation related API warnings
	compileOnly("org.jetbrains:annotations:23.0.0")

}
```

With version 1.0.0 `net.jqwik:jqwik` has become an aggregating module to
simplify jqwik integration for standard users. If you want to be more explicit
about the real dependencies you can replace this dependency with

```
    testImplementation "net.jqwik:jqwik-api:\${jqwikVersion}"
    testImplementation "net.jqwik:jqwik-web:\${jqwikVersion}"
    testImplementation "net.jqwik:jqwik-time:\${jqwikVersion}"
    testRuntime "net.jqwik:jqwik-engine:\${jqwikVersion}"
```

In jqwik's samples repository you can find a rather minimal
[starter example for jqwik with Gradle](https://github.com/jlink/jqwik-samples/tree/main/jqwik-starter-gradle).

See [the Gradle section in JUnit 5's user guide](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle)
for more details on how to configure Gradle for the JUnit 5 platform.
There is also a comprehensive
[list of options for Gradle's `test` task](https://docs.gradle.org/current/userguide/java_plugin.html#sec:java_test).

#### Seeing jqwik Reporting in Gradle Output

Since Gradle does not yet support JUnit platform reporting --
[see this Github issue](https://github.com/gradle/gradle/issues/4605) --
jqwik has switched to do its own reporting by default. This behaviour
[can be configured](#jqwik-configuration) through parameter `jqwik.reporting.usejunitplatform`
(default: `false`).

If you want to see jqwik's reports in the output use Gradle's command line option `--info`:

```
> gradle clean test --info
...
mypackage.MyClassProperties > myPropertyMethod STANDARD_OUT
    timestamp = 2019-02-28T18:01:14.302, MyClassProperties:myPropertyMethod = 
                                  |-----------------------jqwik-----------------------
    tries = 1000                  | # of calls to property
    checks = 1000                 | # of not rejected calls
    generation = RANDOMIZED       | parameters are randomly generated
    after-failure = PREVIOUS_SEED | use the previous seed
    when-fixed-seed = ALLOW       | fixing the random seed is allowed
    edge-cases#mode = MIXIN       | edge cases are generated first
    edge-cases#total = 0          | # of all combined edge cases
    edge-cases#tried = 0          | # of edge cases tried in current run
    seed = 1685744359484719817    | random seed to reproduce generated values
```

### Maven

Starting with version 2.22.0, Maven Surefire and Maven Failsafe provide native support
for executing tests on the JUnit Platform and thus for running _jqwik_ properties.
The configuration of Maven Surefire is described in
[the Maven section of JUnit 5's user guide](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-maven).

Additionally you have to add the following dependency to your `pom.xml` file:

```
<dependencies>
    ...
    <dependency>
        <groupId>net.jqwik</groupId>
        <artifactId>jqwik</artifactId>
        <version>${version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

In jqwik's samples repository you can find a rather minimal
[starter example for jqwik with Maven](https://github.com/jlink/jqwik-samples/tree/main/jqwik-starter-maven).

### Snapshot Releases

Snapshot releases are available through jqwik's
[snapshot repositories](#https://s01.oss.sonatype.org/content/repositories/snapshots).

Adding

```
https://s01.oss.sonatype.org/content/repositories/snapshots
``` 

as a maven repository
will allow you to use _jqwik_'s snapshot release which contains all the latest features.

### Project without Build Tool

I've never tried it but using jqwik without gradle or some other tool to manage dependencies should also work.
You will have to add _at least_ the following jars to your classpath:

- `jqwik-api-${version}.jar`
- `jqwik-engine-${version}.jar`
- `junit-platform-engine-${junitPlatformVersion}.jar`
- `junit-platform-commons-${junitPlatformVersion}.jar`
- `opentest4j-${opentest4jVersion}.jar`

Optional jars are:
- `jqwik-web-${version}.jar`
- `jqwik-time-${version}.jar`
