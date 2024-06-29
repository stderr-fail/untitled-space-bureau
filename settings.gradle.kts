rootProject.name = "untitled-space-bureau"

include("godot")

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