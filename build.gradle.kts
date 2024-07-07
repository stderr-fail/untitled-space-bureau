val godotKotlinVersion by extra("0.9.1-4.2.2")
val jacksonVersion by extra("2.17.2")

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
