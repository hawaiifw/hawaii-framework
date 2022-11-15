description = "Hawaii Async"

dependencies {
    api(project(":hawaii-logging"))
    implementation("org.apache.commons:commons-lang3")
    implementation("org.springframework:spring-core")
    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.yaml:snakeyaml")

    compileOnly("net.ttddyy:datasource-proxy:${project.extra["dataSourceProxyVersion"]}")
    compileOnly("org.apache.httpcomponents.client5:httpclient5:${project.extra["httpcomponentsClient5Version"]}")

    compileOnly("org.springframework:spring-web")

    testImplementation("org.springframework:spring-context")
}

tasks.withType<JavaCompile> {
    options.isDeprecation = true

    options.encoding = Charsets.UTF_8.name()

    // "remove" -Werror .... :-(
    options.compilerArgs.clear()
    options.compilerArgs.addAll(arrayOf("-Xlint:all", "-Xlint:-processing"))
}
