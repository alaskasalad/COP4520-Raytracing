plugins {
    id("jvm-conventions")
}

gradle.projectsEvaluated {
    tasks {
        create("run") {
            // Build demo
            dependsOn(":demo:build")

            // & run it
            doLast {
                val jar = project(":demo").buildDir.resolve("libs")
                    .listFiles { _, fileName ->  fileName.contains("Git") }
                    ?.firstOrNull()
                    ?: error("Failed to find demo jar")

                javaexec {
                    mainClass.set("-jar")
                    args(jar.absolutePath)
                }
            }
        }
    }
}