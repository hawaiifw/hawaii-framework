description = "Hawaii Logging"

dependencies {
    api(project(":hawaii-core"))

    implementation("jakarta.servlet:jakarta.servlet-api")
    implementation("org.springframework:spring-web")
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    compileOnly("org.apache.cxf:cxf-rt-features-logging:${project.extra["apacheCxfVersion"]}")
    compileOnly("org.aspectj:aspectjrt")
    compileOnly("ch.qos.logback:logback-core")
    compileOnly("ch.qos.logback:logback-classic")
    compileOnly("org.springframework.cloud:spring-cloud-context:4.0.0-M5")
    compileOnly("org.springframework.security:spring-security-core")
    compileOnly("org.springframework:spring-webmvc")
    compileOnly("net.ttddyy:datasource-proxy:${project.extra["dataSourceProxyVersion"]}")
    compileOnly("io.opentelemetry:opentelemetry-api:${project.extra["opentelemetryVersion"]}")
    compileOnly("com.nimbusds:nimbus-jose-jwt:${project.extra["nimbusJoseJwtVersion"]}")

    testImplementation("org.apache.commons:commons-text:${project.extra["commonsTextVersion"]}")
    testImplementation("org.springframework.security:spring-security-core")
}
