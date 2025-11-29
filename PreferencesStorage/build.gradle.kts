
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.xxmrk888ytxx.preferencesstorage"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf(libs.versions.javaCompatibilityVersion.get())
        targetCompatibility = JavaVersion.valueOf(libs.versions.javaCompatibilityVersion.get())
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}

dependencies {
    api (libs.datastore)

    //Instrumental Test
    androidTestImplementation (libs.instrumental.test.espresso)
    androidTestImplementation (libs.instrumental.test.runner)
    androidTestImplementation (libs.instrumental.test.core)
    androidTestImplementation (libs.instrumental.test.junit)
    androidTestImplementation (libs.instrumental.test.rules)
    androidTestImplementation(libs.test.android.mockk)
    androidTestImplementation(libs.test.android.mockk.agent)
}
