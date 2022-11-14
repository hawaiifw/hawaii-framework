plugins {
    `java-library`
}

dependencies {
    implementation(project(":hawaii-starter-rest"))
    implementation("org.springframework.boot:spring-boot-actuator")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation(project(":hawaii-starter-test"))
    testImplementation("jakarta.xml.bind:jaxb-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeClasspath("com.h2database:h2")
}
