plugins {
    signing
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.0.21"
}

val ossrhUsername: String by project
val ossrhPassword: String by project

group = "com.yifeistudio"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://s01.oss.sonatype.org/content/groups/public/")
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

publishing {

    repositories {
        maven {
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            val isSnapshotVersion = version.toString().endsWith("SNAPSHOT")
            println(ossrhPassword)
            println(ossrhUsername)
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
            url = if (isSnapshotVersion) {
                uri(snapshotsRepoUrl)
            } else {
                uri(releasesRepoUrl)
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("jackal")
                description.set("grpc-java library for space system")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                url.set("https://github.com/yifeistudio-developer/animal")
                developers {
                    developer {
                        id.set("yifeistudio.com")
                        name.set("hongyi")
                        email.set("develop@yifeistudio.com")
                    }
                    scm {
                        connection.set("scm:git://github.com/yifeistudio-developer/animal.git")
                        developerConnection.set("scm:git://github.com/yifeistudio-developer/animal.git")
                        url.set("https://github.com/yifeistudio-developer/animal")
                    }
                }
            }

        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}