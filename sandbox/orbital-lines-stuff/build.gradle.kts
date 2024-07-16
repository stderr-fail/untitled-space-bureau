import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val godotKotlinVersion: String by project

plugins {
    // can't use godotKotlinVersion below because gradle is the biggest pile of excrement ever created
    id("com.utopia-rise.godot-kotlin-jvm")
}

//
// force various tasks to skip up-to-date checks
//

//tasks.withType<ShadowJar>().configureEach {
//    if (name == "shadowJar") {
//        outputs.upToDateWhen { false }
//    }
//}

//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//    // always run kspKotlin so Godot will detect updates
//    if (name == "kspKotlin") {
//        outputs.upToDateWhen { false }
//    }
//}


dependencies {
    implementation(project(":codegen"))
    ksp(project(":codegen"))

    implementation(project(":solarsystem"))

    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.slf4j:slf4j-api:2.0.13")

    implementation("org.orekit:orekit:12.1.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    implementation("com.utopia-rise:godot-library-debug:${godotKotlinVersion}")
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
