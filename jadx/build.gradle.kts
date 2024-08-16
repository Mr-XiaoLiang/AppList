plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    dependencies {
        // jadx
        implementation("io.github.skylot:jadx-core:1.5.0")
        implementation("io.github.skylot:jadx-dex-input:1.5.0")
        implementation("io.github.skylot:jadx-java-input:1.5.0")
        implementation("io.github.skylot:jadx-java-convert:1.5.0")
        implementation("io.github.skylot:jadx-smali-input:1.5.0")
        implementation("io.github.skylot:jadx-raung-input:1.5.0")

        implementation(project(":sdkList"))
    }
}
