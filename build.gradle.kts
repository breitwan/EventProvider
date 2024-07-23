plugins {
    `java-library`
}

group = "event-provider"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:8.0.1")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("org.jetbrains:annotations:20.1.0")
    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}