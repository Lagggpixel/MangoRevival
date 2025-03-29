/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "8.6"
    id("com.github.johnrengelman.shadow") version ("8.1.1")
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(libs.com.github.cryptomorin.xseries)
    compileOnly(libs.org.spigotmc.spigot.api)
    compileOnly(libs.com.github.milkbowl.vaultapi)
    compileOnly(libs.me.clip.placeholderapi)
}

group = "me.lagggpixel"
version = "1.1.1-SNAPSHOT3"
description = "MangoRevival"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks {
    shadowJar {
        relocate("com.cryptomorin.xseries", "me.lagggpixel.mango.impl.xseries")

        archiveClassifier.set("")
    }
}