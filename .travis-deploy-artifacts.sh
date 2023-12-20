#!/bin/bash

echo "Publishing..."

HAWAII_FRAMEWORK_VERSION=`cat gradle.properties | grep "version" | cut -d'=' -f2`

# Do not deploy archives when building pull request
if [ "$TRAVIS_BRANCH" != "master" ] && [ "$TRAVIS_BRANCH" != "2.x" ] && [ "$TRAVIS_BRANCH" != "3.x" ] || [ "$TRAVIS_PULL_REQUEST" == "true" ] || [[ $HAWAII_FRAMEWORK_VERSION == *SNAPSHOT* ]]; then
  echo "Do not publish to sonatype..."
  exit 0
fi

# Decrypt secring.gpg

openssl aes-256-cbc -K $encrypted_30f4b87b093a_key -iv $encrypted_30f4b87b093a_iv -in secring.gpg.enc -out secring.gpg -d

# Deploy jar artifacts to Sonatype OSSRH

echo "Publishing archives..."

# The parameter org.gradle.internal.publish.checksums.insecure=true disables signing with sha256 and sha512 hashes.
# These maven-metadata hashes prevent releasing on Sonatype, because they can't be overwritten in the releases
# repository.
./gradlew publish -Dorg.gradle.internal.publish.checksums.insecure=true -Psigning.keyId="$SIGNING_KEY" -Psigning.password="$SIGNING_PASSWORD" -Psigning.secretKeyRingFile="${TRAVIS_BUILD_DIR}/secring.gpg"

# Deploy api and reference documentation to gh-pages

echo "Publishing Documentation..."

GH_PAGES_DIR=.gh-pages
GH_REF=github.com/hawaiifw/hawaii-framework

rm -rf $GH_PAGES_DIR
git clone --quiet --branch=gh-pages https://${GH_TOKEN}@${GH_REF} $GH_PAGES_DIR > /dev/null 2>&1

rm -rf $GH_PAGES_DIR/docs/$HAWAII_FRAMEWORK_VERSION
mkdir -p $GH_PAGES_DIR/docs/$HAWAII_FRAMEWORK_VERSION
unzip -o build/distributions/hawaii-framework-${HAWAII_FRAMEWORK_VERSION}-docs.zip -d $GH_PAGES_DIR/docs/$HAWAII_FRAMEWORK_VERSION

git -C $GH_PAGES_DIR config user.email "travis@travis-ci.org"
git -C $GH_PAGES_DIR config user.name "Travis"
git -C $GH_PAGES_DIR add --all
git -C $GH_PAGES_DIR commit --allow-empty -m "Travis build $TRAVIS_BUILD_NUMBER pushed docs to gh-pages"
git -C $GH_PAGES_DIR push origin gh-pages > /dev/null 2>&1

rm -rf $GH_PAGES_DIR
