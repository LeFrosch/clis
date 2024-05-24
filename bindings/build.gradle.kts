val kspVersion: String by project

plugins {
    id("org.jetbrains.kotlin.jvm")
}

group = project.property("group").toString()
version = project.property("version").toString()

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
}