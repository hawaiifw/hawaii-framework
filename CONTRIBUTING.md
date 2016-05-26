## Contributing

Follow the guidelines below for contributing to the Hawaii Framework.

## Code Style

All Java code should use the Hawaii Framework Code Style which is based on the [Google Java Style][]
with the most important exception that we use +4 spaces for block indentation instead of +2.

Eclipse users should simply import the Eclipse [Hawaii Framework Java Style code formatter][].
IntelliJ IDEA users should first install the [Eclipse Code Formatter plugin][] to import the same Eclipse code formatter file.

This [README][Hawaii Framework Java Style code formatter README] contains a full list of adjustments
made to the code formatter.

### Apache License Header

Add the following Apache license header to all new Java classes.

```java
/*
 * Copyright 2015-2016 the original author or authors.
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

TODO

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
