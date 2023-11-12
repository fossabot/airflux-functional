plugins {
    id("kover-merge-convention")
    id("licenses-convention")
    id("org.sonarqube") version "4.4.1.3373"
}

repositories {
    mavenCentral()
}

allprojects {
    repositories {
        mavenCentral()
    }

    version = "0.0.1-SNAPSHOT"
    group = "io.github.airflux"
}

dependencies {
    kover(project(":airflux-functional-core"))
    kover(project(":airflux-functional-test"))
}

sonar {
    properties {
        property("sonar.projectKey", "airflux_airflux-functional")
        property("sonar.organization", "airflux")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${projectDir.path}/build/reports/jacoco/test/jacocoTestReport.xml"
        )
    }
}
