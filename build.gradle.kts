import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.20"
	kotlin("plugin.spring") version "1.9.20"
}


group = "com.github.gridlts"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}


tasks.bootJar {
	mainClass = "com.github.gridlts.tbtasksync.TbTaskSyncApplicationKt"
	enabled = true
}
tasks.jar {enabled = false}

val exposedVersion: String by project

dependencies {
	implementation("org.apache.commons:commons-lang3:3.12.0")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.hibernate.orm:hibernate-community-dialects")
	implementation("org.xerial:sqlite-jdbc:3.39.2.0")
	implementation("com.google.http-client:google-http-client-jackson2:1.40.1")
	implementation("com.google.oauth-client:google-oauth-client-jetty:1.32.1")
	implementation("com.google.apis:google-api-services-tasks:v1-rev20210709-1.32.1")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("com.h2database:h2:2.0.202")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation(kotlin("stdlib-jdk8"))
	testImplementation("io.mockk:mockk:1.13.5")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
	testImplementation("org.assertj:assertj-core:3.24.2")
}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}

}

tasks.withType<Test> {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain(21)
	sourceSets {
		main.get().kotlin.srcDirs("src/main/kotlin")
		test.get().kotlin.srcDirs("src/test/kotlin")
	}
}

