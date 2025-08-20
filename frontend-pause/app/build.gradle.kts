plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
}

android {
    namespace = "com.pause.frontend"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pause.frontend"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "BASE_URL",
                "\"${project.findProperty("BASE_URL_DEV") ?: "http://10.0.2.2:8080/api/pause"}\""
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "BASE_URL",
                "\"${project.findProperty("BASE_URL_DEV") ?: "http://10.0.2.2:8080/api/pause"}\""
            )
        }
    }

    // Recomendado: Java 17 para toolchain moderna (puedes dejar 11 si prefieres)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core + Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // ---- NUEVO: Navegación, imágenes, red, DataStore ----
    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Coil (imágenes locales/remotas)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Retrofit + Moshi + OkHttp (con logs)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines / lifecycle ya las tienes; añadimos DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // --- Kotlinx Serialization JSON ---
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")


    // --- Converter de Retrofit para Kotlinx Serialization ---
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0")
}