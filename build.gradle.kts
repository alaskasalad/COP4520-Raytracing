plugins {
    `java-library`
    id("jvm-conventions")
}

dependencies {
    api(libs.joml)
    api(libs.fastutil)
    testImplementation(libs.junit.jupiter)
}

// Make base-level `run` task
tasks.maybeCreate("run").let {
    it.dependsOn(":demo:run")
    it.group = "application"
}

tasks.build {
    dependsOn(tasks.named("spotlessApply"))
}

tasks.test {
    useJUnitPlatform()
}