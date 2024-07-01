val godotKotlinVersion: String by rootProject.extra

plugins {
  java
  kotlin("jvm") version "1.9.23"
}

dependencies {
  implementation("org.orekit:orekit:12.1.1")
  compileOnly("com.utopia-rise:godot-library-debug:${godotKotlinVersion}")
}

repositories {
  mavenLocal()
  mavenCentral()
}
