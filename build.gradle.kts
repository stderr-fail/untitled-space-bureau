val godotKotlinVersion: String by project
val jacksonVersion: String by project

plugins {
  java
  kotlin("jvm") version "1.9.23"
}

tasks {
  withType<JavaCompile> {
    options.release = 21
  }
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

kotlin {
  jvmToolchain(21)
}

repositories {
  mavenLocal()
  mavenCentral()
}
