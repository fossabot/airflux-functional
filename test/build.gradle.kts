plugins {
    id("kotlin-library-convention")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":airflux-functional-core"))
    implementation(testLibs.bundles.kotest)
}
