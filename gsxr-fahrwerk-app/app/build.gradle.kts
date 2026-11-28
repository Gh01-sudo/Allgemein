import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "de.k7labor.fahrwerk"
    compileSdk = 37

    defaultConfig {
        applicationId = "de.k7labor.fahrwerk"
        minSdk = 31
        targetSdk = 37
        versionCode = 2
        versionName = "1.1.0"
    }

    // Signaturschlüssel liegt NICHT im Repository. Pfad über Umgebungsvariable K7_KEYSTORE_PROPS
    // oder eine lokale Datei keystore.properties (storeFile, storePassword, keyAlias, keyPassword).
    val propsFile = System.getenv("K7_KEYSTORE_PROPS")?.let { file(it) } ?: rootProject.file("keystore.properties")
    val signProps = Properties().apply { if (propsFile.exists()) propsFile.inputStream().use { load(it) } }
    signingConfigs {
        if (signProps.getProperty("storeFile") != null) create("release") {
            storeFile = file(signProps.getProperty("storeFile"))
            storePassword = signProps.getProperty("storePassword")
            keyAlias = signProps.getProperty("keyAlias")
            keyPassword = signProps.getProperty("keyPassword")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { compose = true; buildConfig = true }
    testOptions { unitTests.isReturnDefaultValues = true; unitTests.isIncludeAndroidResources = true }
}

dependencies {
    val bom = platform("androidx.compose:compose-bom:2026.09.00")
    implementation(bom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.activity:activity-compose:1.12.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20250517")
    testImplementation("org.robolectric:robolectric:4.18.1")
    testImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation("androidx.test:core:1.7.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
