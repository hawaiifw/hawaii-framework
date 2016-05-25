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
 * Create a new branch from `master`. Branch names should be concise, lower-case, dash (-) delimited names.
   They should only contain changes related to the name of the branch. For unrelated changes another
   branch should be created and a separate pull request.
 * Code you changes.
 * Build and test the project locally using `./gradlew clean build`.
   Verify the build succeeded, all the tests passed and the checkstyle gave no errors.
 * Push the changes to your remote repository.
 * **Submit the pull request**. Probably the easiest way is to login into GitHub and navigate to the forked
   repository in your account and click the pull request button.

[Google Java Style]: https://google.github.io/styleguide/javaguide.html
[Eclipse code style formatter]: https://github.com/hawaiifw/hawaii-framework/blob/master/src/eclipse/hawaii-framework-java-style.xml
[IDEA code style formatter]: https://github.com/hawaiifw/hawaii-framework/blob/master/src/idea/hawaii-framework-java-style.xml
[GitHub]: https://github.com/
[Hawaii Framework GitHub repository]: https://github.com/hawaiifw/hawaii-framework
