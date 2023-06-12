description = "Hawaii Cache"

dependencies {
    implementation(project(":hawaii-core"))

    compileOnly("org.springframework.boot:spring-boot")
    compileOnly("org.springframework.data:spring-data-redis")
    testImplementation("org.springframework.data:spring-data-redis")
}
