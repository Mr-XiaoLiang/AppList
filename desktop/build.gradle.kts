import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {

    dependencies {
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material)
        implementation(compose.ui)
        implementation(compose.components.resources)
        implementation(compose.components.uiToolingPreview)

        // system
        implementation(compose.desktop.currentOs)
        implementation(compose.desktop.linux_x64)
        implementation(compose.desktop.linux_arm64)
        implementation(compose.desktop.windows_x64)
        implementation(compose.desktop.macos_x64)
        implementation(compose.desktop.macos_arm64)


        // https://mvnrepository.com/artifact/org.json/json
        implementation(libs.json)
    }

}
compose.desktop {
    application {
        mainClass = "AppListDesktopKt"
        jvmArgs += listOf("-Xmx2G")
        val appName = "AppList"
        val versionName = "1.0.0"
        val pkgName = "com.lollipop.applist.desktop"
        val sdf = SimpleDateFormat("yyyyMMdd-HHmmss")
        val buildVersion = "${versionName}-${sdf.format(Date(System.currentTimeMillis()))}"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = appName
            packageVersion = versionName
            description = appName
            macOS {
                dockName = appName
                bundleID = pkgName
                pkgPackageVersion = versionName
                pkgPackageBuildVersion = buildVersion
                iconFile.set(project.file("src/main/resources/icon.icns"))
            }
            windows {
                menuGroup = appName
                dirChooser = true
                iconFile.set(project.file("src/main/resources/icon.ico"))
            }
            linux {
                packageName = appName
                menuGroup = appName
                iconFile.set(project.file("src/main/resources/icon.png"))
            }
        }
    }
}