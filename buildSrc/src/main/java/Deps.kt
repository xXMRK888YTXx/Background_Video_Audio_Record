

object Deps {
    private const val MockkVersion = "1.13.4"

    const val KotlinVersion = "1.8.20"

    object Compose {
        const val ComposeKotlinCompiler = "1.4.5"
        const val ComposeBom = "androidx.compose:compose-bom:2023.04.00"
        const val Ui = "androidx.compose.ui:ui"
        const val Graphics = "androidx.compose.ui:ui-graphics"
        const val ToolingPreview = "androidx.compose.ui:ui-tooling-preview"
        const val Material = "androidx.compose.material:material"
        const val LifeCycle = "androidx.lifecycle:lifecycle-runtime-compose:2.6.1"
        const val Navigation = "androidx.navigation:navigation-compose:2.5.3"
        const val SystemUiController = "com.google.accompanist:accompanist-systemuicontroller:0.29.2-rc"
    }

    object TestAndroid {
        const val AndroidJUnit = "androidx.test.ext:junit:1.1.4"
        const val Espresso = "androidx.test.espresso:espresso-core:3.5.0"
        const val MockkAndroid = "io.mockk:mockk-android:$MockkVersion"
        const val MockkAgent = "io.mockk:mockk-agent:$MockkVersion"
    }

    object AndroidX {
        const val AndroidCore = "androidx.core:core-ktx:1.10.0"
        const val LifeCycle = "androidx.lifecycle:lifecycle-runtime-ktx:2.6.0"
        const val ComposeActivity =  ("androidx.activity:activity-compose:1.7.0")
    }

    object ViewModel {
        private const val version = "2.6.0"
        const val ViewModel = ("androidx.lifecycle:lifecycle-viewmodel:$version")
        const val ViewModelKotlin =  ("androidx.lifecycle:lifecycle-viewmodel-ktx:$version")
    }

    object Dagger { //https://dagger.dev/
        const val version = "2.44"
        const val DaggerCore = "com.google.dagger:dagger:$version"
        const val DaggerKaptCompiler = "com.google.dagger:dagger-compiler:$version"
        const val DaggerKaptPlugin = "kotlin-kapt"
    }

    object Coroutines {
        private const val version = "1.6.4"
        const val CoroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        object Test {
            const val CoroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
        }
    }

    object Test {
        const val JUnit = "junit:junit:4.13.2"
        const val Mockk = "io.mockk:mockk:$MockkVersion"
        const val Testing = "org.testng:testng:6.9.6"
        const val MockkJMVAgent = "io.mockk:mockk-agent-jvm:1.12.5"
    }

    object InstrumentalTest {
        private const val testVersion = "1.5.0"
        const val espresso = "androidx.test.espresso:espresso-core:3.5.0"
        const val testRunner = "androidx.test:runner:1.5.1"
        const val testCore = "androidx.test:core:$testVersion"
        const val jUnit = "androidx.test.ext:junit-ktx:1.1.4"
        const val testRules = "androidx.test:rules:$testVersion"
    }

    object Coil {
        const val coil = "io.coil-kt:coil-compose:2.2.2"
    }

    object DataStore {
        const val dataStore = "androidx.datastore:datastore-preferences:1.0.0"
    }

    object Room {
        private const val version = "2.5.0"
        const val RoomRuntime =  "androidx.room:room-runtime:$version"
        const val KaptCompiler = "androidx.room:room-compiler:$version"
        const val RoomKTX = "androidx.room:room-ktx:$version"
        object Test {
            const val RoomTest = "androidx.room:room-testing:$version"
        }
    }

    object AppCompat {
        private const val version = "1.6.1"
        const val appCompat = "androidx.appcompat:appcompat:$version"
        const val appCompatRes = "androidx.appcompat:appcompat-resources:$version"
    }

    object AdMob {
        const val adMob = "com.google.android.gms:play-services-ads:21.4.0"
    }

    object Media3 {
        private const val media3_version = "1.0.1"
        const val exoPlayer = "androidx.media3:media3-exoplayer:$media3_version"
        const val ui = "androidx.media3:media3-ui:$media3_version"
    }

    object CameraX {
        private val camerax_version = "1.3.0-alpha06"
        val core = "androidx.camera:camera-core:${camerax_version}"
        val camera2 = "androidx.camera:camera-camera2:${camerax_version}"
        val lifecycle = "androidx.camera:camera-lifecycle:${camerax_version}"
        val video = "androidx.camera:camera-video:${camerax_version}"
        val view = "androidx.camera:camera-view:${camerax_version}"
        val extensions = "androidx.camera:camera-extensions:${camerax_version}"
    }

    object MaterialDialog {
        const val materialDialogs = "io.github.vanpra.compose-material-dialogs:datetime:0.9.0"
    }

    object KolinSerialization {
        private const val version = "1.4.1"
        const val plugin = "kotlinx-serialization"
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
        const val classPath = "org.jetbrains.kotlin:kotlin-serialization:$KotlinVersion"
    }

    object ImmutableCollection {
        const val collectionsImmutable = "org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5"
    }


}