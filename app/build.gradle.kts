plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id (Deps.Dagger.DaggerKaptPlugin)
    id (Deps.KolinSerialization.plugin)
    //id (Deps.Firebase.crashlyticsPlugin)
}

android {
    namespace = Config.packageName
    compileSdk = Config.compileSdk

    defaultConfig {
        applicationId = Config.packageName
        minSdk = Config.minSdk
        targetSdk = Config.compileSdk
        versionCode = 1
        versionName = Config.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = Config.isR8ProGuardEnableForRelease
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
            testProguardFile("test-proguard-rules.pro")

        }

        debug {
            isMinifyEnabled = Config.isR8ProGuardEnableForDebug
            proguardFiles("proguard-android-optimize.txt","proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = Config.sourceCompatibility
        targetCompatibility = Config.targetCompatibility
    }
    kotlinOptions {
        jvmTarget = Config.jvmTarget
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Deps.Compose.ComposeKotlinCompiler
    }
    kapt {
        arguments {
            arg("room.schemaLocation","$projectDir/schemas")
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    implementation(project(Project.CoreCompose))
    implementation(project(Project.BottomBarScreen))
    implementation(project(Project.RecordAudioScreen))
    implementation(project(Project.RecordVideoScreen))
    implementation(project(Project.AudioRecordService))
    implementation(project(Project.RecordVideoService))
    implementation(project(Project.StorageScreen))
    implementation(project(Project.AudioPlayer))
    implementation(project(Project.PreferencesStorage))
    implementation(project(Project.VideoPlayerScreen))
    implementation(project(Project.SettingsScreen))
    implementation(project(Project.AdmobManager))

    //Database
    implementation(Deps.Room.RoomKTX)
    implementation(Deps.Room.RoomRuntime)
    kapt (Deps.Room.KaptCompiler)

    //Dagger
    kapt(Deps.Dagger.DaggerKaptCompiler)

    //Compose
    implementation(Deps.Compose.Navigation)
    implementation(Deps.Compose.SystemUiController)

    //Serialization
    implementation(Deps.KolinSerialization.serialization)

    //Firebase
//    implementation(platform(Deps.Firebase.FirebaseBom))
//    implementation(Deps.Firebase.analytics)
//    implementation(Deps.Firebase.crashlytics)

    //Instrumental Test
    androidTestImplementation (Deps.InstrumentalTest.espresso)
    androidTestImplementation (Deps.InstrumentalTest.testRunner)
    androidTestImplementation (Deps.InstrumentalTest.testCore)
    androidTestImplementation (Deps.InstrumentalTest.jUnit)
    androidTestImplementation (Deps.InstrumentalTest.testRules)
    androidTestImplementation(Deps.TestAndroid.MockkAndroid)
    androidTestImplementation(Deps.TestAndroid.MockkAgent)
    androidTestImplementation(Deps.Room.Test.RoomTest)

    //Test
    testImplementation(Deps.TestAndroid.MockkAndroid)
    testImplementation(Deps.TestAndroid.MockkAgent)
    testImplementation(Deps.Test.Testing)
    testImplementation(Deps.Coroutines.Test.CoroutinesTest)


}