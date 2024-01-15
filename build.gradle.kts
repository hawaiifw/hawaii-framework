import net.ltgt.gradle.errorprone.errorprone
import com.diffplug.gradle.spotless.SpotlessTask
import java.util.*

plugins {
    id("java-library")

    // For publishing
    id("signing")
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version ("2.0.0-rc-1")

    // Quality plugins. These are embedded plugins of gradle and their version come with the gradle version.
    id("checkstyle")
    id("net.ltgt.errorprone") version ("3.1.0")
    id("pmd")
    id("com.diffplug.spotless") version ("6.12.0")

    // Dependency management
    id("io.spring.dependency-management") version ("1.1.4")
    id("com.github.ben-manes.versions") version "0.50.0"
}

apply(plugin = "io.github.gradle-nexus.publish-plugin")
apply(plugin = "com.diffplug.spotless")

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
        extra.set("springBootVersion", "3.2.1")
        extra.set("apacheCxfVersion", "4.0.3")
        extra.set("bouncycastleVersion", "1.77")
        extra.set("commonsIoVersion", "2.15.1")
        extra.set("commonsTextVersion", "1.11.0")
        extra.set("dataSourceProxyVersion", "1.10")
        extra.set("hamcrestVersion", "2.2")
        extra.set("hibernatorValidatorVersion", "8.0.1.Final")
        extra.set("httpcomponentsClient5Version", "5.3")
        extra.set("jasyptVersion", "1.9.3")
        extra.set("nimbusJoseJwtVersion", "9.37.3")
        extra.set("opentelemetryVersion", "1.34.1")
        extra.set("orgJsonVersion", "20231013")
        extra.set("springCloudVersion", "4.1.0")
        extra.set("validationApiVersion", "3.0.2")
        extra.set("graphqlJavaVersion", "20.2")
    }

    val checkstyleVersion = "10.12.7"
    val errorProneVersion = "2.24.1"
    val errorProneSupportVersion = "0.14.0"
    val pmdVersion = "7.0.0-rc4"
    val findbugsJsrVersion = "3.0.2"

    apply(plugin = "java-library")
    apply(plugin = "signing")
    apply(plugin = "maven-publish")

    apply(plugin = "checkstyle")
    apply(plugin = "net.ltgt.errorprone")
    apply(plugin = "pmd")
    apply(plugin = "com.diffplug.spotless")

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
        // options.isWarnings = true

        // javac -X
        // javac --help-lint
        //-missing-explicit-ctor"
        options.compilerArgs.addAll(arrayOf("-Xlint:all", "-parameters" ))


        options.errorprone {
            disableWarningsInGeneratedCode.set(true)
            allDisabledChecksAsWarnings.set(true)
            allErrorsAsWarnings.set(true)

            // For now disable, discuss
            disable("Var", "CollectorMutability")
            disable("Varifier")
            // The pattern constant first is always null proof, discuss
            disable("YodaCondition")

            // String.format allows more descriptive texts than String.join.
            disable("StringJoin")
            // Disabled, clashes with settings in IntelliJ
            disable("UngroupedOverloads")
            // Disabled, since IntelliJ does this for us:
            disable("BooleanParameter")
            // Disabled, since we do not require to be compliant with:
            disable("Java7ApiChecker", "Java8ApiChecker", "AndroidJdkLibsChecker")

            disable("TimeZoneUsage")

            // The auto patch is disabled for now, it _seems_ that having this patching in place makes error-prone
            // only check the checks that can be patched. Can be enabled to fix bugs if there are too many.
            //
            errorproneArgs.addAll(
                "-XepPatchChecks:AutowiredConstructor,CanonicalAnnotationSyntax,DeadException,DefaultCharset,LexicographicalAnnotationAttributeListing,LexicographicalAnnotationListing,MethodCanBeStatic,MissingOverride,MutableConstantField,RemoveUnusedImports,StaticImport,UnnecessaryFinal,UnnecessarilyFullyQualified",
                "-XepPatchLocation:IN_PLACE"
            )
        }
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
        compileOnly("com.google.code.findbugs:jsr305:${findbugsJsrVersion}")

        testImplementation("junit:junit")
        testImplementation("org.mockito:mockito-core")
        testImplementation("org.springframework.boot:spring-boot-starter-logging")
        testImplementation("org.springframework:spring-test")

        errorprone("com.google.errorprone:error_prone_core:${errorProneVersion}")
        // Error Prone Support's additional bug checkers.
        errorprone("tech.picnic.error-prone-support:error-prone-contrib:${errorProneSupportVersion}")

        pmd("net.sourceforge.pmd:pmd-ant:${pmdVersion}")
        pmd("net.sourceforge.pmd:pmd-java:${pmdVersion}")

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
     * Configuration of Spotless.
     */
    spotless {
        java {
            googleJavaFormat()
            // custom("replace Javax", { it.replace("import javax.", "import jakarta.") })
        }
    }
    project.tasks["spotlessJavaCheck"].enabled = false

    /**
     * Configuration of PMD.
     */
    pmd {
        toolVersion = pmdVersion
        isConsoleOutput = true
        isIgnoreFailures = false
        //    rulesMinimumPriority.set(5)
        threads.set(4)
        ruleSetConfig = resources.text.fromFile("${rootDir}/src/quality/config/pmd/pmd.xml")
        // clear the default list of rules, otherwise this will override our custom configuration.
        ruleSets = listOf<String>()
    }

    project.tasks["pmdTest"].enabled = false

    /**
     * Configuration of check style.
     */
    checkstyle {
        toolVersion = checkstyleVersion
        // configProperties = mapOf(
        //         "checkstyle.cache.file" to file("checkstyle.cache")
        // )
        configFile = file("${rootDir}/src/quality/config/checkstyle/checkstyle.xml")
    }

    tasks.withType<Checkstyle>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(true)
            html.stylesheet = resources.text.fromFile("${rootDir}/src/quality/config/checkstyle/checkstyle-no-frames-severity-sorted.xsl")
        }
        isShowViolations = false
    }

    project.tasks["checkstyleTest"].enabled = false

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
