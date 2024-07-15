rootProject.name = "untitled-space-bureau"

include("solarsystem")
include("lib")
include("godot")
//include(":sandbox:orbital-lines-stuff")
//project(":sandbox:orbital-lines-stuff").projectDir = File("sandbox/orbital-lines-stuff")
include(":orbital-lines-stuff")
project(":orbital-lines-stuff").projectDir = File("sandbox/orbital-lines-stuff")


pluginManagement {

  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    google()
  }
  resolutionStrategy.eachPlugin {
    if (requested.id.id == "com.utopia-rise.godot-kotlin-jvm") {
      useModule("com.utopia-rise:godot-gradle-plugin:${requested.version}")
    }
  }
}
