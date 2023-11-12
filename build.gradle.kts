plugins {
    id("kover-merge-convention")
    id("licenses-convention")
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

dependencies{
    kover(project(":airflux-functional-core"))
    kover(project(":airflux-functional-test"))
}
