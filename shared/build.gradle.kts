plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
}

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    // iOS targets with framework output
    iosX64 {
        binaries.framework {
            baseName = "shared"
        }
    }
    iosArm64 {
        binaries.framework {
            baseName = "shared"
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "shared"
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotest.runner.junit5)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.property)
                implementation(libs.mockk)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.retrofit)
                implementation("com.squareup.retrofit2:converter-gson:2.9.0")
                implementation(libs.okhttp)
                implementation(libs.okhttp.logging)
                implementation(libs.koin.core)
                implementation(libs.koin.android)
                
                // Import Firebase BoM
                implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
                
                // Firebase dependencies (versions managed by BOM)
                implementation("com.google.firebase:firebase-firestore")
                implementation("com.google.firebase:firebase-analytics")
            }
        }
    }
}

android {
    namespace = "com.cashi.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
