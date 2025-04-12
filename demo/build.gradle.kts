plugins {
    id("jvm-conventions")
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