import com.diffplug.gradle.spotless.SpotlessExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("io.freefair.lombok")
    id("com.github.johnrengelman.shadow")
    id("net.kyori.indra.git")
    id("com.diffplug.spotless")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    build {
        dependsOn(spotlessCheck, shadowJar)
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
        options.compilerArgs.add("-parameters")
    }

    withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }

    withType<ShadowJar> {
        archiveFileName.set("${project.name}-Git-${indraGit.commit()?.name?.take(7) ?: "unknown"}.jar")
    }
}

configure<SpotlessExtension> {
    encoding = Charsets.UTF_8

    format("misc") {
        target("*.gradle", "*.md", ".gitignore", ".gitattributes", ".editorconfig")

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    json {
        target("src/**/*.json")
        gson().indentWithSpaces(4).sortByKeys()
    }

    pluginManager.withPlugin("java") {
        java {
            removeUnusedImports()
            trimTrailingWhitespace()
            indentWithSpaces(4)
        }
    }
}
