plugins {
    id("java")
}

group = "org.whatsaterminal"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.google.api-client:google-api-client:1.32.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.2.0")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20200424-1.30.9")
}

tasks.test {
    useJUnitPlatform()
}