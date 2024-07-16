val godotKotlinVersion: String by project
val jacksonVersion: String by project
val kspVersion: String by project

plugins {
  kotlin("jvm") version "1.9.23"
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation("com.squareup:kotlinpoet:1.18.1")
  implementation("com.google.devtools.ksp:symbol-processing-api:${kspVersion}")
}

repositories {
  mavenLocal()
  mavenCentral()
}
