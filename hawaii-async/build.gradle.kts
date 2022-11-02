description = "Hawaii Async"

dependencies {
    api(project(":hawaii-logging"))
    implementation("jakarta.validation:jakarta.validation-api:${project.extra["validationApiVersion"]}")
    implementation("net.ttddyy:datasource-proxy:${project.extra["dataSourceProxyVersion"]}")
    implementation("org.springframework:spring-core")
    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot")
    implementation("org.apache.httpcomponents:httpclient")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.yaml:snakeyaml")
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