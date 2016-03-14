#!/bin/bash
set -e
set +x  # make sure we're not echoing any sensitive data

HAWAII_FRAMEWORK_VERSION=`cat gradle.properties | grep "version" | cut -d'=' -f2`
DOCDIR=.gh-pages

echo "Deploying archives for Hawaii Framework $HAWAII_FRAMEWORK_VERSION"

echo "Deploying jars to Sonatype OSSRH"

./gradlew uploadArchives

echo "Deploying docs to GitHub Pages"
echo "..<todo>.."

