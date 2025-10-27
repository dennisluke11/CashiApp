plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    ios("ios") {
        binaries {
            framework {
                baseName = "CashiApp"
            }
        }
    }
    
    sourceSets {
        val iosMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
        }
    }
}

// iOS-specific tasks
tasks.register("iosSimulatorArm64") {
    dependsOn("linkDebugFrameworkIosSimulatorArm64")
}
