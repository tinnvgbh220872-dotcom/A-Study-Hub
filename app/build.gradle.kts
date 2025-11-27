    plugins {
        alias(libs.plugins.android.application)
        id("com.google.gms.google-services")
    }

    android {
        namespace = "com.example.final_project"
        compileSdk = 36

        defaultConfig {
            applicationId = "com.example.final_project"
            minSdk = 30
            targetSdk = 36
            versionCode = 1
            versionName = "1.0"
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        signingConfigs {
            getByName("debug") {
                storeFile = rootProject.file("debug-new.keystore")
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
            getByName("debug") {
                signingConfig = signingConfigs.getByName("debug")
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        buildFeatures {
            viewBinding = true
        }

        packaging {
            resources {
                excludes += listOf(
                    "META-INF/NOTICE.md",
                    "META-INF/LICENSE.md"
                )
            }
        }
    }


    dependencies {
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.constraintlayout)
        implementation(libs.navigation.fragment)
        implementation(libs.navigation.ui)
        implementation("com.google.firebase:firebase-functions-ktx")
        implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
        implementation("com.sun.mail:android-mail:1.6.7")
        implementation("com.sun.mail:android-activation:1.6.7")
        implementation(libs.firebase.storage)
        implementation(libs.firebase.messaging)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
        implementation("com.google.android.gms:play-services-auth:20.6.0")
        implementation("com.google.firebase:firebase-database:20.3.0")
        implementation("com.google.firebase:firebase-auth:22.3.0")
        implementation("com.google.firebase:firebase-messaging:23.3.2")
        implementation("com.squareup.okhttp3:okhttp:4.11.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
        implementation("org.json:json:20230227")
    }