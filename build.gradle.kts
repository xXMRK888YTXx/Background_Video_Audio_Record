buildscript {
    dependencies {
        classpath(Deps.KolinSerialization.classPath)
        //classpath(Deps.Firebase.crashlyticsClassPath)
    }
}
plugins {
    id ("com.android.application") version "8.0.1" apply false
    id ("com.android.library") version "8.0.1" apply false
    id ("org.jetbrains.kotlin.android") version Deps.KotlinVersion apply false
}