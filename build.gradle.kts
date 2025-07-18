plugins {
    alias(libs.plugins.agp) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint.plugin) apply false
    alias(libs.plugins.shadow) apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
}

apply {
    from("gradle/translators.gradle.kts")
}
