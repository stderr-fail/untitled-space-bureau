val godotKotlinVersion: String by rootProject.extra
val jacksonVersion: String by rootProject.extra

plugins {
  java
  kotlin("jvm") version "1.9.23"
}

dependencies {

  implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${jacksonVersion}")

  implementation("ch.qos.logback:logback-classic:1.5.6")
  implementation("org.slf4j:slf4j-api:2.0.13")
  implementation("org.orekit:orekit:12.1.1")
  implementation("com.networknt:json-schema-validator:1.5.0")

  testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.3")
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}


repositories {
  mavenLocal()
  mavenCentral()
}
