import com.google.protobuf.gradle.id

plugins {
    signing
    `java-library`
    `maven-publish`
    id("com.google.protobuf") version "0.9.4"
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
    implementation("io.grpc:grpc-core:1.69.0")
    implementation("io.grpc:grpc-testing:1.69.0")
    implementation("io.grpc:grpc-protobuf:1.69.0")
    implementation("io.grpc:grpc-inprocess:1.69.0")
    api("io.grpc:grpc-kotlin-stub:1.4.1")
    implementation("com.google.protobuf:protobuf-java:4.29.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

sourceSets {
    main {
        proto {
            srcDir("${projectDir.parent}/protos")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.29.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.69.0"
        }
        id("grpc-kt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc-kt") {
                    option("lite")
                }
                id("grpc") { }
            }
        }
    }
}

publishing {

    repositories {
        maven {
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            val isSnapshotVersion = version.toString().endsWith("SNAPSHOT")
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
