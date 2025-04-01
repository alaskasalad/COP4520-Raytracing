plugins {
    `java-library`
    id("jvm-conventions")
}

dependencies {
    api(libs.joml)
    api(libs.fastutil)
}

// Make base-level `run` task
tasks.maybeCreate("run").let {
    it.dependsOn(":demo:run")
    it.group = "application"
}

tasks.build {
    dependsOn(tasks.named("spotlessApply"))
}