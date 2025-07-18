/*
 * Copyright (C) 2016-2021 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Daily Loop Tracker.
 *
 * Daily Loop Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Daily Loop Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

plugins {
    alias(libs.plugins.agp)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint.plugin)
    id("com.google.gms.google-services")
}

tasks.compileLint {
    dependsOn("updateTranslators")
}

/*
Added on top of kotlinOptions to work around this issue:
https://youtrack.jetbrains.com/issue/KTIJ-24311/task-current-target-is-17-and-kaptGenerateStubsProductionDebugKotlin-task-current-target-is-1.8-jvm-target-compatibility-should#focus=Comments-27-6798448.0-0
Updating gradle might fix this, so try again in the future to remove this and run:
./gradlew --rerun-tasks :dohabits-android:kaptGenerateStubsReleaseKotlin
If this doesn't produce any warning, try to remove it.
 */
kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.dodo.dohabits"
    compileSdk = 36

    defaultConfig {
        versionCode = 1
        versionName = "1.0.0"
        minSdk = 28
        targetSdk = 36
        applicationId = "com.dodo.myhabits"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (System.getenv("LOOP_KEY_ALIAS") != null) {
            create("release") {
                keyAlias = System.getenv("LOOP_KEY_ALIAS")
                keyPassword = System.getenv("LOOP_KEY_PASSWORD")
                storeFile = file(System.getenv("LOOP_KEY_STORE"))
                storePassword = System.getenv("LOOP_STORE_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
            if (signingConfigs.findByName("release") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }

        debug {
            enableUnitTestCoverage = true
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        targetCompatibility(JavaVersion.VERSION_17)
        sourceCompatibility(JavaVersion.VERSION_17)
    }

    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    buildFeatures.viewBinding = true
    lint.abortOnError = false
    flavorDimensions += "channel"
    productFlavors {
        create("gp") {
            dimension = "channel"
        }
        create("mi") {
            dimension = "channel"
        }
    }
}

dependencies {
    compileOnly(libs.jsr250.api)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.appIntro)
    implementation(libs.jsr305)
    implementation(libs.dagger)
    implementation(libs.guava)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.jackson)
    implementation(libs.ktor.client.json)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.appcompat)
    implementation(libs.legacy.preference.v14)
    implementation(libs.legacy.support.v4)
    implementation(libs.material)
    implementation(libs.opencsv)
    implementation(libs.konfetti.xml)
    implementation(project(":dohabits-core"))
    ksp(libs.dagger.compiler)

    // AdMob（Google 移动广告 SDK）
    add("gpImplementation", "com.google.android.gms:play-services-ads:22.5.0")
    
    add("miImplementation", "com.google.android.gms:play-services-ads:22.5.0")
    add("miImplementation", "com.tradplusad:tradplus:14.3.30.1")
    add("miImplementation", "androidx.legacy:legacy-support-v4:1.0.0")
    add("miImplementation", "androidx.appcompat:appcompat:1.3.0-alpha02")
    add("miImplementation", "androidx.core:core-ktx:1.5.0")
    add("miImplementation", "androidx.recyclerview:recyclerview:1.1.0")
    add("miImplementation", "com.google.code.gson:gson:2.8.6")

    add("miImplementation", "com.google.android.gms:play-services-ads:22.5.0")
    add("miImplementation", "com.google.android.gms:play-services-ads-identifier:18.2.0")
    add("miImplementation", "com.google.android.gms:play-services-appset:16.0.0")
    add("miImplementation", "com.google.android.gms:play-services-basement:17.5.0")

    add("miImplementation", "com.applovin:applovin-sdk:13.3.0")
    add("miImplementation", "com.tradplusad:tradplus-applovin:9.14.3.30.1")

    add("miImplementation", "com.ironsource.sdk:mediationsdk:8.9.1")
    add("miImplementation", "com.tradplusad:tradplus-ironsource:10.14.3.30.1")

    add("miImplementation", "com.pangle.global:pag-sdk:7.2.0.5")
    add("miImplementation", "com.tradplusad:tradplus-pangle:19.14.3.30.1")

    add("miImplementation", "com.inmobi.monetization:inmobi-ads-kotlin:10.8.3")
    add("miImplementation", "com.inmobi.omsdk:inmobi-omsdk:1.3.17.1")
    add("miImplementation", "com.tradplusad:tradplus-inmobix:23.14.3.30.1")

    add("miImplementation", "com.mbridge.msdk.oversea:mbridge_android_sdk:16.9.71")
    add("miImplementation", "com.tradplusad:tradplus-mintegralx_overseas:18.14.3.30.1")

    add("miImplementation", "com.yandex.android:mobileads:7.13.0")
    add("miImplementation", "com.tradplusad:tradplus-yandex:50.14.3.30.1")

    add("miImplementation", "com.bigossp:bigo-ads:5.3.0")
    add("miImplementation", "com.tradplusad:tradplus-bigo:57.14.3.30.1")

    add("miImplementation", "com.vungle:vungle-ads:7.5.0")
    add("miImplementation", "com.tradplusad:tradplus-vunglex:7.14.3.30.1")

    add("miImplementation", "com.tradplusad:tradplus-crosspromotion:27.14.3.30.1")

    add("miImplementation", "com.tradplusad:tp_exchange:40.14.3.30.1")

    add("miImplementation", "com.mi.ads:columbus-sdk:3.5.1.9")
    add("miImplementation", "com.tradplusad:tradplus-columbus:76.14.3.30.1")


    // Facebook Audience Network（如需 Facebook 广告混合填充）
    implementation("androidx.annotation:annotation:1.7.0")
    implementation("com.facebook.android:audience-network-sdk:6.16.0")

    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    // TODO: 按需添加 Firebase 具体依赖

    androidTestImplementation(libs.bundles.androidTest)
    testImplementation(libs.bundles.test)
}
