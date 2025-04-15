plugins {
    id("jvm-conventions")
//    id("org.beryx.jlink") version "3.1.2-snapshot"
    application
}

dependencies {
    implementation(rootProject)
}

application {
    mainClass = "edu.ucf.cop4520raytracing.demo.DemoBootstrapper"
}

tasks.named<JavaExec>("run").configure {
    this.environment("joml.fastmath", "true")
}