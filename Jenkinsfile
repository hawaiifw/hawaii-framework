import org.apache.commons.lang.RandomStringUtils

pipeline {
    environment {
        // General environment variables
        ARTIFACT_NAME = "hawaii-framework"
        BUILD_IMAGE = "eclipse-temurin:17-jdk-jammy"
        TZ = "Europe/Amsterdam"
        // Credentials needed to push to Artifactory
        // This will automatically populate CDAASJFROGARTIFACTORY_USER_USR and CDAASJFROGARTIFACTORY_USER_PSW too
        CDAASJFROGARTIFACTORY_USER = credentials('vzcdaas-jfrogio-user')
    }
    agent any
    triggers {
        pollSCM 'H/5 * * * *'
    }
    options{
        buildDiscarder(
            logRotator(
                numToKeepStr: '10'
            )
        )
        disableConcurrentBuilds()
    }
    stages {
        stage('Prepare docker environment') {
            when {
                allOf {
                    not { equals expected: 'FAILURE', actual: currentBuild.result }
                    not { equals expected: 'ABORTED', actual: currentBuild.result }
                }
            }
            steps {
                script{
                    catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                        env.DOCKER_RANDOM_ID = RandomStringUtils.random(10, true, true)
                    }
                }
            }
        }
        stage('Build code and publish to artifactory') {
            when {
                allOf {
                    not { equals expected: 'FAILURE', actual: currentBuild.result }
                    not { equals expected: 'ABORTED', actual: currentBuild.result }
                }
            }
            steps {
                script {
                    catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                        env.BUILD_CONTAINER = "hfw-build-" + env.DOCKER_RANDOM_ID
                        docker.image(env.BUILD_IMAGE).inside("--name=" + env.BUILD_CONTAINER + " -e GRADLE_USER_HOME=/tmp -e TZ=" + env.TZ) { c ->
                            sh "chmod +x gradlew && ./gradlew -Djava.util.prefs.userRoot=/tmp --no-daemon clean build publishAllPublicationsToCdaasJfrogArtifactoryRepository"
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            // Save log files of the containers before we shut them down
            sh "docker logs " + env.BUILD_CONTAINER + " > container-" + env.BUILD_CONTAINER + ".log 2>&1 || true"

            // Clean up the various stuff we've created along the way
            sh "docker rm -f " + env.BUILD_CONTAINER + " " + " || true"

            // Save the log files as artifacts so we can look at them later
            archiveArtifacts artifacts: "container-*.log"

            // Clean up the container logs to they don't linger around for the next build
            sh "rm -rf container-*.log || true"
        }
        cleanup {
            script {
                cleanWs()
            }
        }
    }
}