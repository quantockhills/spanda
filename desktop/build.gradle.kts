import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    jvmToolchain(17)
    sourceSets.main {
        // reuse the Android app's UI/data sources as-is (screens, models, repository,
        // app settings, meditations). Android-only files are excluded by ABSOLUTE path
        // (glob patterns would also match the desktop overrides in this module), and
        // desktop replacements live in desktop/src/main/kotlin.
        kotlin.srcDir("../app/src/main/java")
        kotlin.exclude(org.gradle.api.specs.Spec { e ->
            val abs = e.file.absolutePath
            if (!abs.contains("/app/src/main/java/")) return@Spec false
            abs.endsWith("MainActivity.kt") ||
                abs.endsWith("notify/ReminderScheduler.kt") ||
                abs.endsWith("notify/ReminderWorker.kt") ||
                abs.endsWith("ui/Assets.kt") ||
                abs.endsWith("ui/theme/Theme.kt") ||
                abs.endsWith("ui/HomeScreen.kt")
        })
        // same JSON + image assets, straight from the app module (no duplication)
        resources.srcDir("../app/src/main/assets")
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
    implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
    implementation("org.json:json:20240303")
}

compose.desktop {
    application {
        mainClass = "com.madhav.bhairava.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Dmg)
            packageName = "Spanda"
            packageVersion = "1.0.0"
            description = "Spanda — Śivabodhaviṃśikā · Amṛtādistavaḥ · Saṃvarta Stavaḥ · Bhagavad Gītā"
            vendor = "Spanda"
            windows {
                // MSI built on a Windows runner (WiX); see .github/workflows/desktop-msi.yml
            }
        }
    }
}
