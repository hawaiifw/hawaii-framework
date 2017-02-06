## Contributing

Follow the guidelines below for contributing to the Hawaii Framework.

## Code Style

All Java code should use the Hawaii Framework Code Style which is based on the [Google Java Style][]
with the most important exception that we use +4 spaces for block indentation instead of +2.

Users should simply import the Eclipse [Hawaii Framework Java Style code formatter][] file in their IDE.
The same code formatter file should also be imported in IntelliJ IDEA.

This [README][Hawaii Framework Java Style code formatter README] contains a full list of adjustments
made to the code formatter.

### Apache License Header

Add the following Apache license header to all new Java classes.

```java
/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ...;
```

For existing classes always check the date range in the license header and update it if needed.

### Javadoc tags

Always add your name to the `@author` tags and use `@since` tags for newly-added public API types
and methods. For example:

```java
/**
 * ...
 *
 * @author Marcel Overdijk
 * @author Wouter Eerdekens
 * @since 2.0.0
 */
```

## Development Guidelines

The following development guidelines should be applied when applying changes to the Hawaii Framework. These are
opinionated guidelines.

### Check your parameters

When a particular method or constructor accepts parameters and there are expectations around these parameters such
as not being null, we explicitly test these parameters using the
[Objects](https://docs.oracle.com/javase/7/docs/api/java/util/Objects.html) class introduced by JDK 7.

For example use `Objects.requireNonNull` methods to assure a value cannot be null and we get notified when it does.
Use `requireNonNull` instead of `notNull` as arequireNonNulla throws an `Exception` and `notNull` returns just a `boolean`.
Make sure you add a valuable message. Note that the Javadoc explicitly states that the value must not be null.

```java
/**
  * Sets a fixed clock to be used.
  *
  * @param clock the fixed clock, not null
  */
public void useFixedClock(Clock clock) {
     Objects.requireNonNull(clock, "'clock' must not be null");
     useFixedClock(clock.instant(), clock.getZone());
}
```

### When to add Javadoc

In short: Always.

The value of a Framework is hugely driven by its documentation. So every package, class, interface and
method needs proper javadoc. One could argue this is overkill for simple getters and setters,
but for the sake of completeness javadoc is added for those methods as well.

### How to Unit Test

Each functionality needs a Unit test to prove the functionality does what it needs to do. You need to make sure
your production code and your test code grow together in functionality (Test Driven Development). This will result
in better code, since you will need to test your code in isolation.

In order to test in isolation, [Mock Objects](https://en.wikipedia.org/wiki/Mock_object) are used
(e.g. with the [Mockito](http://mockito.org) framework), so the dependencies can be controlled.

The goal is not to have a 100% test coverage, but to add tests for relatively complex functionality.

In order to get readable tests, [hamcrest matchers](https://code.google.com/archive/p/hamcrest/wikis/Tutorial.wiki)
are the preferred way of writing your tests.

For example:

```java
@Test
public void testDefaultConstructorUsesSystemClock() {
    assertThat(new HawaiiTime().getClock(), is(equalTo(Clock.system(HawaiiTime.DEFAULT_ZONE))));
}
```

## Workflow

Ultimately, contributing code - or documentation - to the Hawaii Framework means creating a GitHub pull request.
The following steps give a detailed overview of how to create a pull request.

 * Login or create a [GitHub][] account.
 * Fork the [Hawaii Framework GitHub repository][].
 * Clone the forked repository. The forked repository is the repository under your own GitHub account.
 * Create a new branch from `master`. Branch names should be concise, lower-case, dash (`-`) delimited names.
   They should only contain changes related to the name of the branch. For unrelated changes another
   branch should be created and a separate pull request.
 * Code you changes.
 * Build and test the project locally using `./gradlew clean build`.
   Verify the build succeeded, all the tests passed and the checkstyle gave no errors.
 * Push the changes to your remote repository.
 * **Submit the pull request**. Probably the easiest way is to login into GitHub, navigate to the forked
   repository in your account and click the pull request button.

### Caveats & Further Instructions

Never code changes in the `master` branch or other branches forked from the [Hawaii Framework GitHub repository].
There is no guarantee your pull request will be merged, so it is wise to keep the `master` branch clean.

The `master` branch of your forked repository should be in sync with the `master` branch of the
[Hawaii Framework GitHub repository] as much as possible.

To keep your `master` branch in sync with the [Hawaii Framework GitHub repository] follow the below steps:

 * Add a new remote to your fork's local repository:
   `git remote add upstream https://github.com/hawaiifw/hawaii-framework.git`.
 * Fetch the upstream changes: `git fetch upstream`.
 * Checkout your fork's local `master` branch: `git checkout master`.
 * Merge the upstream changes: `git merge upstream/master`.
 * From here you can create a new branch from `master` (to start working on a new pull request) or
   merge the changes from `master` into one of your other local development branches.


[Google Java Style]: https://google.github.io/styleguide/javaguide.html
[Hawaii Framework Java Style code formatter]: https://github.com/hawaiifw/hawaii-framework/blob/master/src/eclipse/hawaii-framework-java-style.xml
[Hawaii Framework Java Style code formatter README]: https://github.com/hawaiifw/hawaii-framework/blob/master/src/eclipse/README.md
[Eclipse Code Formatter plugin]: http://plugins.jetbrains.com/plugin/6546
[GitHub]: https://github.com/
[Hawaii Framework GitHub repository]: https://github.com/hawaiifw/hawaii-framework
