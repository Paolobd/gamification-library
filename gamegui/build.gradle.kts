plugins {
    `java-library` // <1>
}

group = "com.github.paolobd"
version = "1.0"

repositories {
    mavenCentral() // <2>
}

val seleniumVersion = "4.14.1"
val jacksonVersion = "2.15.3"

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
}

tasks.test {
    useJUnitPlatform()
}
