import java.util.Calendar


plugins {
    id("java-library")

    // For publishing
    id("signing")
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version ("1.1.0")

    // Quality plugins. These are embedded plugins of gradle and their version come with the gradle version.
    id("checkstyle")
    id("com.github.spotbugs") version ("5.0.13")
    id("pmd")

    // Dependency management
    id("io.spring.dependency-management") version ("1.1.0")
}

apply(plugin = "io.github.gradle-nexus.publish-plugin")

nexusPublishing {
    repositories {
        sonatype {
            username.set(System.getenv("SONATYPE_OSSRH_USERNAME"))
            password.set(System.getenv("SONATYPE_OSSRH_PASSWORD"))
        }
    }
}

subprojects {
    project.group = "org.hawaiiframework"

    buildscript {
        extra.set("springBootVersion", "3.0.0")
        extra.set("apacheCxfVersion", "3.5.4")
        extra.set("bouncycastleVersion", "1.70")
        extra.set("commonsIoVersion", "2.11.0")
        extra.set("commonsTextVersion", "1.10.0")
        extra.set("dataSourceProxyVersion", "1.8")
        extra.set("hamcrestVersion", "2.2")
        extra.set("hibernatorValidatorVersion", "8.0.0.Final")
        extra.set("httpcomponentsClient5Version", "5.1.3")
        extra.set("jasyptVersion", "1.9.3")
        extra.set("nimbusJoseJwtVersion", "9.25.6")
        extra.set("opentelemetryVersion", "1.19.0")
        extra.set("orgJsonVersion", "20220924")
        extra.set("validationApiVersion", "3.0.2")
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
        maven {
            url = uri("https://repo.spring.io/milestone")
        }
        maven {
            url = uri("https://repo.spring.io/snapshot")
        }
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
                            id.set("marceloverdijk")
                            name.set("Marcel Overdijk")
                            email.set("marcel@overdijk.me")
                        }
                    }
                }
            }
        }
        repositories {
            maven {
                name = "ilionxartifactory"
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
        }
    }
}
