val godotKotlinVersion: String by project

plugins {
  id("com.utopia-rise.godot-kotlin-jvm")
}

dependencies {
  implementation("ch.qos.logback:logback-classic:1.5.6")
  implementation("org.slf4j:slf4j-api:2.0.13")

  implementation("org.orekit:orekit:12.1.1")
  testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
  implementation("com.utopia-rise:godot-library-debug:${godotKotlinVersion}")

//  implementation(project(":lib"))
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
