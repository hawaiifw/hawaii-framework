FROM eclipse-temurin:17-jdk-jammy

RUN mkdir /workspace
COPY . /workspace
RUN cd /workspace && sh gradlew --no-daemon clean build publishAllPublicationsToIlionxArtifactoryRepository publishAllPublicationsToCdaasJfrogArtifactoryRepository
