import java.util.*


plugins {
    id("java-library")

    // For publishing
    id("signing")
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version ("2.0.0-rc-1")

    // Quality plugins. These are embedded plugins of gradle and their version come with the gradle version.
    id("checkstyle")
    id("com.github.spotbugs") version ("6.0.4")
//    id("com.github.spotbugs") version ("5.0.14")
    id("pmd")

    // Dependency management
    id("io.spring.dependency-management") version ("1.1.4")
    id("com.github.ben-manes.versions") version "0.50.0"
}

apply(plugin = "io.github.gradle-nexus.publish-plugin")

nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set("1a44791a29d63")
            username.set(System.getenv("SONATYPE_OSSRH_USERNAME"))
            password.set(System.getenv("SONATYPE_OSSRH_PASSWORD"))
        }
    }
    // packageGroup.set("org.hawaiiframework")
}

subprojects {
    project.group = "org.hawaiiframework"

    buildscript {
        extra.set("springBootVersion", "3.2.0")
        extra.set("apacheCxfVersion", "4.0.3")
        extra.set("bouncycastleVersion", "1.70")
        extra.set("commonsIoVersion", "2.13.0")
        extra.set("commonsTextVersion", "1.11.0")
        extra.set("dataSourceProxyVersion", "1.10")
        extra.set("hamcrestVersion", "2.2")
        extra.set("hibernatorValidatorVersion", "8.0.1.Final")
        extra.set("httpcomponentsClient5Version", "5.3")
        extra.set("jasyptVersion", "1.9.3")
        extra.set("nimbusJoseJwtVersion", "9.37.3")
        extra.set("opentelemetryVersion", "1.33.0")
        extra.set("orgJsonVersion", "20231013")
        extra.set("springCloudVersion", "4.1.0")
        extra.set("validationApiVersion", "3.0.2")
        extra.set("graphqlJavaVersion", "20.2")
    }

    apply(plugin = "java-library")
    apply(plugin = "signing")
    apply(plugin = "maven-publish")

    apply(plugin = "checkstyle")
    apply(plugin = "com.github.spotbugs")
    apply(plugin = "pmd")

    apply(plugin = "io.spring.dependency-management")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        withJavadocJar()
        withSourcesJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()

        options.isDeprecation = true
        options.isWarnings = true

        // javac -X
        // javac --help-lint
        //-missing-explicit-ctor"
        options.compilerArgs.addAll(arrayOf("-Xlint:all", "-Werror", "-parameters" ))
    }

    repositories {
        mavenCentral()
        mavenLocal()
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${project.extra["springBootVersion"]}")
        }
    }

    configurations.all {
        exclude("commons-logging", "commons-logging")
    }

    dependencies {
        compileOnly("org.slf4j:slf4j-api")

        testImplementation("junit:junit")
        testImplementation("org.mockito:mockito-core")
        testImplementation("org.springframework.boot:spring-boot-starter-logging")
        testImplementation("org.springframework:spring-test")

        spotbugsSlf4j("org.slf4j:slf4j-simple")
        // optional "org.springframework.boot:spring-boot-configuration-processor"
    }
    tasks.withType<Jar> {
        manifest.attributes["Created-By"] = "${System.getProperty("java.version")} (${System.getProperty("java.specification.vendor")})"
        manifest.attributes["Implementation-Title"] = project.name
        manifest.attributes["Implementation-Version"] = project.version

        from("${rootProject.projectDir}/src/dist") {
            include("license.txt")
            include("notice.txt")
            include("release-notes.md")
            into("META-INF")
            val replace = mapOf("copyright" to Calendar.getInstance().get(Calendar.YEAR), "version" to rootProject.version)
            expand(replace)
        }
    }

    tasks.withType<Test> {
        testLogging {
            setEvents(listOf("passed", "skipped", "failed"))
            setExceptionFormat("full")
        }
    }

    tasks.withType<Javadoc> {
        description = "Generates project-level Javadoc API documentation."

        options.memberLevel = JavadocMemberLevel.PROTECTED
        options.header = project.name

        // Disabled, no equivalent found (yet)
        // options.author = true
        // options.links(javadocLinks)
        val javadocOpts = options as CoreJavadocOptions
        javadocOpts.addBooleanOption("html5", true)
        javadocOpts.addStringOption("Xdoclint:none", "-quiet")
        javadocOpts.addStringOption("Xlint:none")

        logging.captureStandardError(LogLevel.INFO)
        logging.captureStandardOutput(LogLevel.INFO)
    }

    /**
     * Configuration of PMD.
     */
    pmd {
        // as a development team we want pmd failures to break the build and keep the code clean.
        isIgnoreFailures = false
        // directly show the failures in the output
        isConsoleOutput = true
        // the configuration of the custom rules
        ruleSetConfig = resources.text.fromFile("${rootDir}/src/quality/config/pmd/pmd.xml")
        // clear the default list of rules, otherwise this will override our custom configuration.
        ruleSets = listOf<String>()
    }
    project.tasks["pmdTest"].enabled = false

    /**
     * Configuration of check style
     */
    checkstyle {
        configFile = file("${rootDir}/src/quality/config/checkstyle/checkstyle.xml")
        isIgnoreFailures = false
    }
    project.tasks["checkstyleTest"].enabled = false

    /**
     * Configuration of spotbugs.
     */
    spotbugs {
        excludeFilter.set(file("${rootDir}/src/quality/config/spotbugs/exclude.xml"))
        ignoreFailures.set(true)
        showProgress.set(true)
    }
    project.tasks["spotbugsTest"].enabled = false
    tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
        val format = findProperty("spotbugsReportFormat")
        val xmlFormat = (format == "xml")
        reports {
            maybeCreate("html").required.set(!xmlFormat)
            maybeCreate("xml").required.set(xmlFormat)
        }
    }

    /**
     * Sign
     */
    if (project.hasProperty("signing.keyId")) {
        signing {
            sign(publishing.publications)
        }
    }

    /**
     * Maven publications
     */
    publishing {
        publications {
            create<MavenPublication>("java") {
                artifactId = project.name
                groupId = project.group.toString()
                version = project.version.toString()

                from(components["java"])
                pom {
                    packaging = "jar"
                    name.set("Hawaii Framework - " + project.name)
                    description.set("Hawaii Framework - " + project.name)
                    url.set("https://github.com/hawaiifw/hawaii-framework")

                    scm {
                        connection.set("scm:git@github.com/hawaiifw/hawaii-framework.git")
                        developerConnection.set("scm:git@github.com:hawaiifw/hawaii-framework.git")
                        url.set("https://github.com/hawaiifw/hawaii-framework")
                    }

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    developers {
                        developer {
                            name.set("Rutger Lubbers")
                            email.set("rlubbers@ilionx.com")
                        }
                        developer {
                            name.set("Giuseppe Collura")
                            email.set("gcollura@ilionx.com")
                        }
                    }
                }
            }
        }
        repositories {
            maven {
                name = "ilionxArtifactory"
                if (rootProject.version.toString().endsWith("-SNAPSHOT")) {
                    url = uri("https://artifactory.ilionx.cloud/artifactory/libs-snapshot-local/")
                } else {
                    url = uri("https://artifactory.ilionx.cloud/artifactory/libs-release-local/")
                }
                credentials {
                    username = System.getenv("ILIONXARTIFACTORY_USER_USR")
                    password = System.getenv("ILIONXARTIFACTORY_USER_PSW")
                }
            }
            maven {
                name = "cdaasJfrogArtifactory"
                if (rootProject.version.toString().endsWith("-SNAPSHOT")) {
                    url = uri("https://vzcdaas.jfrog.io/artifactory/win-libs-snapshots/")
                } else {
                    url = uri("https://vzcdaas.jfrog.io/artifactory/win-libs-releases/")
                }
                credentials {
                    username = System.getenv("CDAASJFROGARTIFACTORY_USER_USR")
                    password = System.getenv("CDAASJFROGARTIFACTORY_USER_PSW")
                }
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
    }
}
