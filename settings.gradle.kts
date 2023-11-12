rootProject.name = "airflux-functional"

include(":airflux-functional-core")
project(":airflux-functional-core").projectDir = file("./core")

include(":airflux-functional-test")
project(":airflux-functional-test").projectDir = file("./test")

dependencyResolutionManagement {
    versionCatalogs {
        create("testLibs") {
            from(files("gradle/test-libs.versions.toml"))
        }
    }
}
