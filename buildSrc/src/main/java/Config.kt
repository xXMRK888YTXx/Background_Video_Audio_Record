import org.gradle.api.JavaVersion

object Config {
    const val compileSdk = 33
    const val minSdk = 26
    const val packageName = "com.xxmrk888ytxx.backgroundvideovoicerecord"
    const val isR8ProGuardEnableForRelease = false
    const val isR8ProGuardEnableForDebug = false
    const val versionName = "devBuild"

    //JDK Config
    val sourceCompatibility = JavaVersion.VERSION_17
    val targetCompatibility = JavaVersion.VERSION_17
    const val jvmTarget = "17"
}