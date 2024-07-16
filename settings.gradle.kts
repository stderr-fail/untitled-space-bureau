rootProject.name = "untitled-space-bureau"

include(":codegen")
include("solarsystem")
include("lib")
include("godot")

include(":orbital-lines-stuff")
project(":orbital-lines-stuff").projectDir = File("sandbox/orbital-lines-stuff")


pluginManagement {

  val kspVersion: String by settings
  val godotKotlinVersion: String by settings

  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    google()
  }

  plugins {
    id("com.utopia-rise.godot-kotlin-jvm") version godotKotlinVersion
  }

// not sure why they suggested this resolutionStrategy, the above pluginManagement id version stuff seems to work OK

//  resolutionStrategy.eachPlugin {
//    if (requested.id.id == "com.utopia-rise.godot-kotlin-jvm") {
////      useModule("com.utopia-rise:godot-gradle-plugin:${requested.version}")
//      useModule("com.utopia-rise:godot-gradle-plugin:${godotKotlinVersion}")
//    }
//  }

}
