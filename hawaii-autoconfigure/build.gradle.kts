description = "Hawaii Autoconfigure"

dependencies {
    api(project(":hawaii-core"))
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.yaml:snakeyaml")

    compileOnly(project(":hawaii-async"))
    compileOnly(project(":hawaii-logging"))
    compileOnly(project(":hawaii-cache"))
    compileOnly("com.fasterxml.jackson.core:jackson-databind")
    compileOnly("com.fasterxml.jackson.datatype:jackson-datatype-json-org")
    compileOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
    compileOnly("org.springframework:spring-webmvc")
    compileOnly("org.springframework.data:spring-data-redis")
    compileOnly("org.springframework.security:spring-security-core")
    compileOnly("jakarta.validation:jakarta.validation-api:3.0.2")

}

tasks.withType<JavaCompile> {
    options.isDeprecation = true

    options.encoding = Charsets.UTF_8.name()

    // "remove" -Werror .... :-(
    /*
    org.springframework.boot/spring-boot-test/3.0.0-M5/b55c5d2ed3d8bd7820f5544b77962d52ee8bb7db/spring-boot-test-3.0.0-M5.jar(/org/springframework/boot/test/context/SpringBootTest.class): warning: Cannot find annotation method 'value()' in type 'ExtendWith': class file for org.junit.jupiter.api.extension.ExtendWith not found
     */
    options.compilerArgs.clear()
    options.compilerArgs.addAll(arrayOf("-Xlint:all", "-Xlint:-processing"))
}
