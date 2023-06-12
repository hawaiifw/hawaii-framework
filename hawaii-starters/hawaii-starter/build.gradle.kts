description = "Hawaii Starter"

dependencies {
    api(project(":hawaii-autoconfigure"))
    api(project(":hawaii-core"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
}