import org.gradle.internal.enterprise.test.FileProperty

val javaVersion: String by project

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij")
    id("com.google.devtools.ksp")
}

group = project.property("group").toString()
version = project.property("version").toString()

intellij {
    version.set("2024.1")
    type.set("CL")

    plugins.set(listOf("PythonCore"))
}

tasks {
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

    setupDependencies {
        ksp{
            idea.orNull?.sources?.let {
                arg("sources", it.path)
            }
        }
    }
}

dependencies {
    ksp(project(":bindings"))

    runtimeOnly("org.jetbrains.kotlin:kotlin-scripting-jsr223:2.0.0")
    runtimeOnly("org.python:jython-standalone:2.7.3")
}

ksp {
}

tasks.runIde {
    jvmArgs = listOf("-Xms4G", "-Xmx4096m", "-ea")
}

@CacheableTask
abstract class BindingsGen : DefaultTask() {
    @get:InputFile
    abstract val intellijSources: FileProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generateBindings() {
       println("Hello there!")
    }
}

abstract class BindingsGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        tasks {
            register("generateBindings", BindingsGen::class) {
                description = "Generates Java bindings from source jars"
            }
        }
    }
}

apply<BindingsGeneratorPlugin>()