## Contributing

Follow the guidelines below for contributing to the Hawaii Framework.

## Code Style

All Java code should use the Hawaii Framework Code Style which is based on the [Google Java Style][].
We have [Eclipse][Eclipse code style formatter] and [IDEA][IDEA code style formatter] code style
formatters which should be imported and used in the IDE of your choice.

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
There is no guarantee your pull request will be merged so it is wise to keep the `master` branch clean.
The `master` branch of your forked repository should be in sync with the `master` branch of the
[Hawaii Framework GitHub repository] as much as possible. Please read further how to do this.

To keep your `master` branch in sync with the [Hawaii Framework GitHub repository] follow the below steps:

 * Add a new remote to your fork's local repository like
   `git remote add upstream https://github.com/hawaiifw/hawaii-framework.git`.
 * Fetch the upstream changes: `git fetch upstream`.
 * Checkout your fork's local `master` branch: `git checkout master`.
 * Merge the upstream changes: `git merge upstream/master`.
 * From here you can create a new branch from `master` (to start working on a new pull request) or
   merge the changes from `master` into one of your other local development branches.


[Google Java Style]: https://google.github.io/styleguide/javaguide.html
[Eclipse code style formatter]: https://github.com/hawaiifw/hawaii-framework/blob/master/src/eclipse/hawaii-framework-java-style.xml
[IDEA code style formatter]: https://github.com/hawaiifw/hawaii-framework/blob/master/src/idea/hawaii-framework-java-style.xml
[GitHub]: https://github.com/
[Hawaii Framework GitHub repository]: https://github.com/hawaiifw/hawaii-framework
