description = "Hawaii Starter Rest"

dependencies {
    api(project(":hawaii-starter"))
    api("org.json:json:${project.extra["orgJsonVersion"]}")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-json-org") {
        exclude("org.json", "json")
    }
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}
