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
    id("io.spring.dependency-management") version ("1.0.14.RELEASE")
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
//        extra.set("springBootVersion", "3.0.0-M5")
        extra.set("springBootVersion", "3.0.0-SNAPSHOT")
        extra.set("apacheCxfVersion", "3.5.4")
        extra.set("bouncycastleVersion", "1.70")
        extra.set("commonsIoVersion", "2.11.0")
        extra.set("commonsTextVersion", "1.10.0")
        extra.set("dataSourceProxyVersion", "1.8")
        extra.set("hamcrestVersion", "2.2")
        extra.set("hibernatorValidatorVersion", "8.0.0.Final")
        extra.set("jasyptVersion", "1.9.3")
        extra.set("orgJsonVersion", "20220924")
        extra.set("nimbusJoseJwtVersion", "9.25.6")
        extra.set("opentelemetryVersion", "1.19.0")
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
        options.isDeprecation = true

        options.encoding = Charsets.UTF_8.name()

        options.compilerArgs.addAll(arrayOf("-Xlint:all", "-Xlint:-processing", "-Werror"))
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
//        val springBootVersion = "3.0.0-M5"
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${project.extra["springBootVersion"]}")
        }
    }

    configurations.all {
        exclude("commons-logging", "commons-logging")
    }

    dependencies {
        // https://mvnrepository.com/artifact/org.apache.httpcomponents.client5/httpclient5
        implementation("org.apache.httpcomponents.client5:httpclient5:5.1.3")
        implementation("commons-io:commons-io:${project.extra["commonsIoVersion"]}")
        implementation("org.apache.commons:commons-lang3")
        implementation("org.slf4j:jcl-over-slf4j")
        implementation("org.slf4j:slf4j-api")
        implementation("org.json:json:${project.extra["orgJsonVersion"]}")

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
        //        options["notree"]
        // Disabled, no equivalent found (yet)
        // options.author = true
        // options.links(javadocLinks)
        // options.addBooleanOption("html5", true)
        // options.addStringOption("Xdoclint:none", "-quiet")

        // Suppress warnings due to cross-module @see and @link references;
        // Note that global 'api' task does display all warnings.
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
    }

//        register("mavenJava", MavenPublication::class) {
//            from(components["java"])
//            artifact(tasks["sourcesJar"])
//            artifact(tasks["javadocJar"])
//        }
//        mavenJava(MavenPublication) {
//            from components.java
//                    artifact tasks.sourcesJar
//                    artifact tasks.javadocJar
//
//                    pom {
//                        name = "Hawaii Framework"
//                        packaging "jar"
//                        description = "Hawaii Framework"
//                        url = "https://github.com/hawaiifw/hawaii-framework"
//
//                        scm {
//                            connection = "scm:git@github.com/hawaiifw/hawaii-framework.git"
//                            developerConnection = "scm:git@github.com:hawaiifw/hawaii-framework.git"
//                            url = "https://github.com/hawaiifw/hawaii-framework"
//                        }
//
//                        licenses {
//                            license {
//                                name = "The Apache License, Version 2.0"
//                                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
//                            }
//                        }
//
//                        developers {
//                            developer {
//                                id = "marceloverdijk"
//                                name = "Marcel Overdijk"
//                                email = "marcel@overdijk.me"
//                            }
//                        }
//                    }
//        }
}


////    jar {
////        manifest.attributes["Created-By"] = "${System.getProperty("java.version")} (${System.getProperty("java.specification.vendor")})"
////        manifest.attributes["Implementation-Title"] = subproject.name
////        manifest.attributes["Implementation-Version"] = subproject.version
////
////        from("${rootProject.projectDir}/src/dist") {
////            include "license.txt"
////            include "notice.txt"
////            include "release-notes.md"
////            into "META-INF"
////            expand(copyright: new Date().format("yyyy"), version: project. version)
////        }
////    }
////
////    javadoc {
////        description = "Generates project-level Javadoc API documentation."
////
////        options.memberLevel = org.gradle.external.javadoc.JavadocMemberLevel.PROTECTED
////        options.author = true
////        options.header = project.name
////        options.links(javadocLinks)
////        options.addBooleanOption("html5", true)
////        options.addStringOption("Xdoclint:none", "-quiet")
////
////        // Suppress warnings due to cross-module @see and @link references;
////        // Note that global 'api' task does display all warnings.
////        logging.captureStandardError LogLevel . INFO
////                logging.captureStandardOutput LogLevel . INFO // suppress "## warnings" message
////    }
//
//    task sourcesJar (type: Jar, dependsOn: classes) {
//    classifier = "sources"
//    from sourceSets . main . allSource
//}
//
//    task javadocJar (type: Jar, dependsOn: javadoc) {
//    classifier = "javadoc"
//    from javadoc . destinationDir
//}
//
//    artifacts {
//        archives sourcesJar
//                archives javadocJar
//    }
//
//    checkstyle {
//        configDir = file("${rootProject.projectDir}/src/quality/config/checkstyle")
//        ignoreFailures = false
//        sourceSets = [sourceSets.main]
//        toolVersion = checkstyleToolVersion
//    }
//
//    pmd {
//        ignoreFailures = false
//        ruleSetFiles = files("${rootProject.projectDir}/src/quality/config/pmd/pmd.xml")
//        ruleSets = [] // https://github.com/pmd/pmd/issues/876
//        sourceSets = [sourceSets.main]
//        toolVersion = pmdToolVersion
//    }
//
//    spotbugs {
//        excludeFilter = file("${rootProject.projectDir}/src/quality/config/spotbugs/exclude.xml")
//        ignoreFailures = false
//        sourceSets = [sourceSets.main]
//        toolVersion = spotbugsToolVersion
//    }
//
//    if (project.hasProperty("signing.keyId")) {
//        signing {
//            sign publishing . publications
//        }
//    }
//
//    publishing.publications {
//        mavenJava(MavenPublication) {
//            from components . java
//                    artifact tasks . sourcesJar
//                    artifact tasks . javadocJar
//
//                    pom {
//                        name = "Hawaii Framework"
//                        packaging "jar"
//                        description = "Hawaii Framework"
//                        url = "https://github.com/hawaiifw/hawaii-framework"
//
//                        scm {
//                            connection = "scm:git@github.com/hawaiifw/hawaii-framework.git"
//                            developerConnection = "scm:git@github.com:hawaiifw/hawaii-framework.git"
//                            url = "https://github.com/hawaiifw/hawaii-framework"
//                        }
//
//                        licenses {
//                            license {
//                                name = "The Apache License, Version 2.0"
//                                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
//                            }
//                        }
//
//                        developers {
//                            developer {
//                                id = "marceloverdijk"
//                                name = "Marcel Overdijk"
//                                email = "marcel@overdijk.me"
//                            }
//                        }
//                    }
//        }
//    }
//
//    //io.codearte.gradle
//    nexusStaging {
//        packageGroup = 'org.hawaiiframework'
//        numberOfRetries = 20
//        delayBetweenRetriesInMillis = 3000
//    }
//
//    nexusPublishing {
//        repositories {
//            sonatype {
//                packageGroup = 'org.hawaiiframework'
//                username = System.getenv("SONATYPE_OSSRH_USERNAME")
//                password = System.getenv("SONATYPE_OSSRH_PASSWORD")
//            }
//        }
//        connectTimeout = Duration.ofSeconds(1000)
//        clientTimeout = Duration.ofSeconds(1000)
//    }
//
//    tasks.withType(com.github.spotbugs.SpotBugsTask) {
//        reports {
//            xml.enabled = false
//            html.enabled = true
//        }
//    }
//}
//