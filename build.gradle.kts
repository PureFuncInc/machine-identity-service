import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun mustHaveProp(key: String): String {
    var result: String? = System.getenv(key)
    var source = "env properties"
    if (result == null) {
        source = "system properties"
        result = System.getProperty(key)
    }
    if (result == null) {
        source = "project properties"
        result = project.properties[key] as String?
    }
    return if (result != null) {
        logger.lifecycle("using prop ${String.format("%-40s", key)} from $source")
        result
    } else {
        throw IllegalArgumentException("property with key '$key' not found")
    }
}

plugins {
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.google.cloud.tools.jib") version "3.2.0"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "net.purefunc"
version = "1.1.0"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/PureFuncInc/pure-platform-core")
        credentials {
            username = "Pure-Func-Inc"
            password = mustHaveProp("GITHUB_PUBLISH_TOKEN")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:postgresql:1.17.1")
    testImplementation("org.testcontainers:junit-jupiter:1.17.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {
    from {
        image = "openjdk:11-jre-slim"
    }
    to {
        auth {
            username = "PureFuncInc"
            password = mustHaveProp("GITHUB_PUBLISH_TOKEN")
        }
        image = "ghcr.io/purefuncinc/${project.name}:$version"
    }
    container {
        creationTime = "USE_CURRENT_TIMESTAMP"
        mainClass = "net.purefunc.voice.VoiceServiceApplicationKt"
        jvmFlags = listOf(
            "-Xms1g",
            "-Xmx1g"
        )
    }
    setAllowInsecureRegistries(true)
}
