pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "BackgroundVideoVoiceRecord"
include (":app")
include(":CoreAndroid")
include(":CoreCompose")
include(":BottomBarScreen")
include(":RecordAudioScreen")
include(":RecordVideoScreen")
include(":AudioRecordService")
include(":StorageScreen")
include(":AudioPlayer")
include(":VideoRecorder")
include(":RecordVideoService")
include(":CameraPreviewCompose")
