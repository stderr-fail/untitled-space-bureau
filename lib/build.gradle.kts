val godotKotlinVersion: String by project

plugins {
  // can't use godotKotlinVersion below because gradle is the biggest pile of excrement ever created
  id("com.utopia-rise.godot-kotlin-jvm")
}

dependencies {
  implementation(project(":solarsystem"))
}

godot {
  registrationFileBaseDir.set(projectDir.resolve("scripts"))
  isRegistrationFileHierarchyEnabled.set(true)
  isRegistrationFileGenerationEnabled.set(true)

  // To enable Android Export.
  //isAndroidExportEnabled.set(true)

  // To enable iOS export and Graal Native Image export.
  //isGraalNativeImageExportEnabled.set(true)
}

repositories {
  mavenLocal()
  mavenCentral()
}
