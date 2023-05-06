buildscript {
    dependencies {
        classpath(Deps.KolinSerialization.classPath)
    }
}
plugins {
    id ("com.android.application") version "8.0.0" apply false
    id ("com.android.library") version "8.0.0" apply false
    id ("org.jetbrains.kotlin.android") version Deps.KotlinVersion apply false
}