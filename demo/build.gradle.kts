plugins {
    id("jvm-conventions")
}

dependencies {
    implementation(project(":core"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "edu.ucf.cop4520raytracing.demo.DemoBootstrapper"
    }
}
