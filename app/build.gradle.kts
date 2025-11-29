
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.room)
}

android {
    namespace = libs.versions.packageName.get()
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.packageName.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = 5
        versionName = "1.0.1r"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = libs.versions.minifyEnabledRelease.get().toBoolean()
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
            testProguardFile("test-proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")

        }

        debug {
            isMinifyEnabled = libs.versions.minifyEnabledDebug.get().toBoolean()
            proguardFiles("proguard-android-optimize.txt","proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf(libs.versions.javaCompatibilityVersion.get())
        targetCompatibility = JavaVersion.valueOf(libs.versions.javaCompatibilityVersion.get())
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    room { schemaDirectory("$projectDir/schemas") }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    implementation(project(":CoreCompose"))
    implementation(project(":BottomBarScreen"))
    implementation(project(":RecordAudioScreen"))
    implementation(project(":RecordVideoScreen"))
    implementation(project(":AudioRecordService"))
    implementation(project(":RecordVideoService"))
    implementation(project(":StorageScreen"))
    implementation(project(":AudioPlayer"))
    implementation(project(":PreferencesStorage"))
    implementation(project(":VideoPlayerScreen"))
    implementation(project(":SettingsScreen"))
    implementation(project(":AutoExportToExternalStorageScreen"))
    implementation(project(":Worker"))
    implementation(project(":FastOpenAppQuickSettingsService"))


    //Database
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    //Dagger
    ksp(libs.dagger.compiler)

    //Compose
    implementation(libs.compose.navigation)
    implementation(libs.compose.system.ui.controller)

    //Serialization
    implementation(libs.kotlin.serialization.json)

    //Firebase
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    //Instrumental Test
    androidTestImplementation (libs.instrumental.test.espresso)
    androidTestImplementation (libs.instrumental.test.runner)
    androidTestImplementation (libs.instrumental.test.core)
    androidTestImplementation (libs.instrumental.test.junit)
    androidTestImplementation (libs.instrumental.test.rules)
    androidTestImplementation(libs.test.android.mockk)
    androidTestImplementation(libs.test.android.mockk.agent)
    androidTestImplementation(libs.room.test)

    //Test
    testImplementation(libs.test.android.mockk)
    testImplementation(libs.test.android.mockk.agent)
    testImplementation(libs.test.testng)
    testImplementation(libs.coroutines.test)

    //DocumentFile Api
    implementation(libs.document.file)
}
