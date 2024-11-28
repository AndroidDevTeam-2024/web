plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.atry"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.atry"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes +=
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF"

        }
    }
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }
}

dependencies {

    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit 核心库
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Gson 转换器
    implementation("com.squareup.okhttp3:okhttp:4.9.3") // OkHttp 网络库
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3") // OkHttp 日志拦截器
    implementation ("androidx.compose.ui:ui:1.4.0") // UI 组件
    implementation ("androidx.compose.material3:material3:1.2.0") // Material3 组件库
    implementation ("androidx.compose.ui:ui-tooling-preview:1.4.0") // 预览支持
    implementation ("androidx.compose.foundation:foundation:1.4.0") // 基础组件库，如 Box、Column、Row 等
    implementation ("androidx.compose.material:material:1.4.0") // Material 组件库
    implementation ("androidx.compose.runtime:runtime:1.4.0") // Compose runtime，支持状态管理
    implementation ("androidx.compose.ui:ui-tooling:1.4.0") // Compose UI Tooling，用于调试和预览
    implementation ("androidx.compose.foundation:foundation-layout:1.4.0") // 用于布局相关的功能
    implementation ("androidx.compose.material:material-icons-core:1.4.0") // 如果使用图标
    implementation("androidx.compose.material:material-icons-extended:1.5.2") // 替换为最新版本

    // Kotlin 标准库和扩展
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.8.0") // Kotlin 标准库
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4") // 支持协程的 Android 库

    // 如果使用 LiveData 和 ViewModel，可以使用这些依赖
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0") // ViewModel 与 Compose 的集成
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0") // LiveData、Lifecycle 支持

    implementation("io.coil-kt:coil-compose:2.4.0") // 替换为最新版本
    implementation("androidx.navigation:navigation-compose:2.7.3") // 替换为最新版本

    implementation("androidx.activity:activity-ktx:1.9.2")  // 或更高版本
    implementation("androidx.fragment:fragment-ktx:1.6.0")  // 或更高版本
    //
    implementation ("com.google.accompanist:accompanist-coil:0.15.0")
    implementation(files("libs\\SparkChain.aar"))

    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.identity.doctypes.jvm)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.storage)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.play.services.nearby)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}