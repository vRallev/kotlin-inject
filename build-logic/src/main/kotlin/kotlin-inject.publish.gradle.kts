plugins {
    `maven-publish`
    signing
}

version = rootProject.version
group = rootProject.group

fun MavenPublication.mavenCentralPom() {
    pom {
        name.set("kotlin-inject")
        description.set("A compile-time dependency injection library for kotlin")
        url.set("https://github.com/evant/kotlin-inject")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("evant")
                name.set("Evan Tatarka")
            }
        }
        scm {
            connection.set("https://github.com/evant/kotlin-inject.git")
            developerConnection.set("https://github.com/evant/kotlin-inject.git")
            url.set("https://github.com/evant/kotlin-inject")
        }
    }
}

publishing {
    if (plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
        // already has publications, just need to add javadoc task
        val javadocJar by tasks.creating(Jar::class) {
            from("javadoc")
            archiveClassifier.set("javadoc")
        }
        publications.all {
            if (this is MavenPublication) {
                artifact(javadocJar)
                mavenCentralPom()
            }
        }
        // create task to publish all apple (macos, ios, tvos, watchos) artifacts
        val publishApple by tasks.registering {
            publications.all {
                if (name.contains(Regex("macos|ios|tvos|watchos"))) {
                    val publicationNameForTask = name.replaceFirstChar(Char::uppercase)
                    dependsOn("publish${publicationNameForTask}PublicationToSonatypeRepository")
                }
            }
        }
    } else {
        // Need to create source, javadoc & publication
        val java = extensions.getByType<JavaPluginExtension>()
        java.withSourcesJar()
        java.withJavadocJar()
        publications {
            create<MavenPublication>("lib") {
                from(components["java"])
                mavenCentralPom()
            }
        }
    }

    repositories {
        maven {
            name = "CodeArtifact"
            setUrl("https://amazon-149122183214.d.codeartifact.us-west-2.amazonaws.com/maven/LastMileAppPlatform_mainline/")

            val authToken = rootProject.ext.get("code_artifact_auth_token")?.toString()

            credentials {
                username = "aws"

                if (authToken != null) {
                    credentials.password = authToken
                }
            }

            tasks.withType(PublishToMavenRepository::class.java).configureEach {
                if ("codeartifact" in name.lowercase()) {
                    doFirst {
                        checkNotNull(authToken) {
                            "The auth token for CodeArtifact was not set. " +
                                "See PUBLISHING.md for instructions."
                        }
                    }
                }
            }
        }
    }
}

signing {
    setRequired {
        false
    }

    publishing.publications.all {
        // sign(this)
    }
}

// TODO: remove after https://youtrack.jetbrains.com/issue/KT-46466 is fixed
project.tasks.withType(AbstractPublishToMaven::class.java).configureEach {
    dependsOn(project.tasks.withType(Sign::class.java))
}
