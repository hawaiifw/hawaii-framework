rootProject.name = "hawaii-framework"

pluginManagement {
    repositories {
        maven {
            url = uri("https://repo.spring.io/milestone")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

val modules = arrayOf(
        "hawaii-async",
        "hawaii-autoconfigure",
        "hawaii-cache",
        "hawaii-core",
        "hawaii-logging",
)

val starters = arrayOf(
        "hawaii-starter",
        "hawaii-starter-async",
        "hawaii-starter-boot",
        "hawaii-starter-cache",
        "hawaii-starter-logging",
        "hawaii-starter-rest",
        // "hawaii-starter-test"   (deprecated + removed)
)

//val samples = arrayOf(
//        "hawaii-recipes",
//        "hawaii-validation",
//        "hello-hawaii"
//)

modules.forEach { name -> include(name)}
starters.forEach { name -> include(name)}
//samples.forEach { name -> include(name)}


starters.forEach { name ->
    val p = findProject(":${name}")
    p?.projectDir = File ("hawaii-starters/${name}");
}
