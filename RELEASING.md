## Releasing

Follow the steps below for publishing a new release.

* [ ] Set the new version in `gradle.properties`. E.g. change `version=2.0.0.BUILD-SNAPSHOT` to `version=2.0.0.M3`.
* [ ] Execute `./gradlew clean build install` and verify the build is successful.
* [ ] Push the changes. This will trigger a Travis build which uploads the artifacts to the Sonatype OSSRH repository.
      This will also upload the api docs and userguide to the hawaiiframeork.org github pages.
* [ ] Login into https://oss.sonatype.org/ and navigate to the Staging Repositories.
      Scroll to the bottom and verify the contents of the `orghawaiiframework-xxxx` repository.
      It should contain the new uploaded artifacts.
      Close and then Release the repository to activate the Maven Central synchronization (which will approx. 1 hour).
* [ ] Create a new tag and push it to origin:
      `git tag -a v[version] -m "Release v[version]"`
      `git push origin v[version]`
* [ ] Change back the version in `gradle.properties` to the `BUILD-SNAPSHOT` and push the change.
* [ ] After the new version is synchronized to Maven Central update the links in https://github.com/hawaiifw/hawaii-framework/blob/gh-pages/_includes/releases.html (in the `gh-pages` branch) to display the new version on http://www.hawaiiframework.org/. 
