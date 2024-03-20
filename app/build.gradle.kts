import org.jetbrains.kotlin.gradle.utils.toSetOrEmpty
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Inet4Address
import java.net.NetworkInterface

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-kapt")

//    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.kagg886.medicine_getter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kagg886.medicine_getter"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        //调试包使用本机ip，正式包需改成生产域名。
        debug {
//            buildConfigField("String", "AI_HOST", "\"\"")
            buildConfigField("String", "AI_HOST", "\"http://${ip()}:8080\"")
        }
        release {
            buildConfigField("String", "AI_HOST", "\"\"") //置空
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

fun ip():String {
    val a = NetworkInterface.getNetworkInterfaces().toList().filter {
        return@filter it.run {
            !(isLoopback || isVirtual || !isUp)
        }
    }.sortedBy { it.index }[0].inetAddresses.toList()
        .filterIsInstance<Inet4Address>()[0].hostAddress
    println("本机ip：${a}，请保证手机与服务器在同一wifi里")
    return a
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.1")

    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.github.rroohit:ImageCropView:2.1.0")
    implementation("com.google.accompanist:accompanist-permissions:0.23.1")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    val camerax_version = "1.3.2"
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")

//    implementation("com.google.dagger:hilt-android:2.x")
//    kapt("com.google.dagger:hilt-android-compiler:2.x")

}