plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "com.jetbrains.cidr.clsi"
version = "0.0.1"

repositories {
    mavenCentral()
}

intellij {
    version.set("2024.1")
    type.set("CL")

    plugins.set(listOf("JavaScript"))
}

tasks {
    val javaVersion = "17"

    withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("242.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

dependencies {
    val graalvmVersion = "23.0.4"

    implementation("org.graalvm.js:js:${graalvmVersion}")
    runtimeOnly("org.graalvm.compiler:compiler:${graalvmVersion}")
}

tasks.runIde {
    intellij.plugins.add("IdeaVIM:2.10.2")

    jvmArgs = listOf("-Xms4G", "-Xmx4096m", "-XX:+UnlockExperimentalVMOptions", "-XX:+EnableJVMCI", "-ea", "-Didea.ProcessCanceledException=disabled")
}

tasks.test {
    jvmArgs = listOf("-Xms4G", "-Xmx4096m", "-XX:+UnlockExperimentalVMOptions", "-XX:+EnableJVMCI", "-ea")
}