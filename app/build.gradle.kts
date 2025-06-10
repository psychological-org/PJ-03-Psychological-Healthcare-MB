import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.beaceful"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.beaceful"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "AWS_ACCESS_KEY", "\"${rootProject.extra["awsAccessKey"]}\"")
        buildConfigField("String", "AWS_SECRET_KEY", "\"${rootProject.extra["awsSecretKey"]}\"")
        buildConfigField("String", "CLIENT_SECRET", "\"${rootProject.extra["clientSecret"]}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.storage)
    implementation(libs.androidx.core.i18n)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

//    Add-on
    implementation (libs.androidx.navigation.compose)
    implementation (libs.androidx.material.icons.extended)
    implementation (libs.androidx.foundation)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.dagger.compiler)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compile)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    implementation(libs.aws.core)
    implementation(libs.aws.s3)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.accompanist.permissions)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.accompanist.permissions.v0360)
    implementation(libs.coil.compose)

    implementation(libs.androidx.media3.exoplayer.v131)
    implementation(libs.androidx.media3.ui.v131)

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.auth0:java-jwt:4.4.0")
}