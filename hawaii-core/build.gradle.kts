description = "Hawaii Core"

dependencies {
    api("org.jasypt:jasypt:${project.extra["jasyptVersion"]}")
    api("org.hamcrest:hamcrest:${project.extra["hamcrestVersion"]}")
    api("org.bouncycastle:bcprov-jdk15on:${project.extra["bouncycastleVersion"]}")
    implementation("commons-codec:commons-codec")
    implementation("org.apache.commons:commons-lang3")

    compileOnly("com.fasterxml.jackson.core:jackson-databind")
    compileOnly("com.fasterxml.jackson.datatype:jackson-datatype-json-org")
    compileOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
    compileOnly("org.aspectj:aspectjweaver")
    compileOnly("org.springframework.cloud:spring-cloud-context:${project.extra["springCloudVersion"]}")
    compileOnly("org.springframework:spring-aop")
    compileOnly("org.springframework:spring-core")
    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-jdbc")
    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-webmvc")
    compileOnly("org.springframework.security:spring-security-core")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.hibernate:hibernate-validator:${project.extra["hibernatorValidatorVersion"]}")
    compileOnly("net.ttddyy:datasource-proxy:${project.extra["dataSourceProxyVersion"]}")

    testImplementation("jakarta.servlet:jakarta.servlet-api")
    testImplementation("org.springframework:spring-core")
    testImplementation("org.springframework:spring-web")
    testImplementation("org.springframework:spring-webmvc")
    testImplementation("jakarta.servlet:jakarta.servlet-api")
}
