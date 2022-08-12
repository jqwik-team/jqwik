---
title: jqwik User Guide - ${version}
---
<h1>The jqwik User Guide
<span style="padding-left:1em;font-size:50%;font-weight:lighter">${version}</span>
</h1>

<h3>Table of Contents
<span style="padding-left:1em;font-size:50%;font-weight:lighter">
    <a href="#detailed-table-of-contents">Detailed Table of Contents</a>
</span>
</h3>

- [How to Use](#how-to-use)
- [Writing Properties](#writing-properties)
- [Default Parameter Generation](#default-parameter-generation)
- [Customized Parameter Generation](#customized-parameter-generation)
- [Recursive Arbitraries](#recursive-arbitraries)
- [Using Arbitraries Directly](#using-arbitraries-directly)
- [Contract Tests](#contract-tests)
- [Stateful Testing](#stateful-testing)
- [Stateful Testing (Old Approach)](#stateful-testing-old-approach)
- [Assumptions](#assumptions)
- [Result Shrinking](#result-shrinking)
- [Collecting and Reporting Statistics](#collecting-and-reporting-statistics)
- [Providing Default Arbitraries](#providing-default-arbitraries)
- [Domain and Domain Context](#domain-and-domain-context)
- [Generation from a Type's Interface](#generation-from-a-types-interface)
- [Generation of Edge Cases](#generation-of-edge-cases)
- [Exhaustive Generation](#exhaustive-generation)
- [Data-Driven Properties](#data-driven-properties)
- [Rerunning Falsified Properties](#rerunning-falsified-properties)
- [jqwik Configuration](#jqwik-configuration)
- [Web Module](#web-module)
- [Time Module](#time-module)
- [Kotlin Module](#kotlin-module)
- [Advanced Topics](#advanced-topics)
- [API Evolution](#api-evolution)


<!-- use `doctoc --maxlevel 4 user-guide.md` to recreate the TOC -->
<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
### Detailed Table of Contents  


<!-- END doctoc generated TOC please keep comment here to allow auto update -->


## How to Use

${new File("src/docs/include/how-to-use.md").text}


## Writing Properties

${new File("src/docs/include/writing-properties.md").text}


## Default Parameter Generation

${new File("src/docs/include/default-parameter-generation.md").text}


## Customized Parameter Generation

${new File("src/docs/include/customized-parameter-generation.md").text}


## Recursive Arbitraries

${new File("src/docs/include/recursive-arbitraries.md").text}


## Using Arbitraries Directly

${new File("src/docs/include/using-arbitraries-directly.md").text}


## Contract Tests

${new File("src/docs/include/contract-tests.md").text}


## Stateful Testing

${new File("src/docs/include/stateful-testing.md").text}


## Stateful Testing (Old Approach)

${new File("src/docs/include/stateful-testing-old.md").text}


## Assumptions

${new File("src/docs/include/assumptions.md").text}


## Result Shrinking

${new File("src/docs/include/result-shrinking.md").text}


## Collecting and Reporting Statistics

${new File("src/docs/include/collecting-and-reporting-statistics.md").text}


## Providing Default Arbitraries

${new File("src/docs/include/providing-default-arbitraries.md").text}

 
## Domain and Domain Context

${new File("src/docs/include/domain-and-domain-context.md").text}


## Generation from a Type's Interface

${new File("src/docs/include/generation-from-type.md").text}


## Generation of Edge Cases

${new File("src/docs/include/generation-of-edge-cases.md").text}


## Exhaustive Generation

${new File("src/docs/include/exhaustive-generation.md").text}


## Data-Driven Properties

${new File("src/docs/include/data-driven-properties.md").text}


## Rerunning Falsified Properties

${new File("src/docs/include/rerunning-falsified-properties.md").text}


## jqwik Configuration

${new File("src/docs/include/jqwik-configuration.md").text}


## Additional Modules

_jqwik_ comes with a few additional modules:

- The [`web` module](#web-module)
- The [`time` module](#time-module)
- The [`testing` module](#testing-module)
- The [`kotlin` module](#kotlin-module)

### Web Module

${new File("src/docs/include/web-module.md").text}

### Time Module

${new File("src/docs/include/time-module.md").text}

### Kotlin Module

${new File("src/docs/include/kotlin-module.md").text}

### Testing Module

${new File("src/docs/include/testing-module.md").text}


## Advanced Topics

${new File("src/docs/include/implement-your-own.md").text}

${new File("src/docs/include/lifecycle-hooks.md").text}

## API Evolution

${new File("src/docs/include/api-evolution.md").text}


## Release Notes

Read this version's [release notes](/release-notes.html#${releaseNotesVersion}).
