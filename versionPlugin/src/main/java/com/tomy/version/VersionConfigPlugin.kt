package com.tomy.version

import com.android.build.gradle.*
import com.tomy.version.Testing.androidTestImplementation
import com.tomy.version.Testing.testImplementation
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.PluginContainer
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


/**@author Tomy
 * Created by Tomy on 2022/2/21.
 */
const val api = "api"
const val implementation = "implementation"

class VersionConfigPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.config(project)
    }

    private fun PluginContainer.config(project: Project) {
        whenPluginAdded {
            when (this) {
                //com.android.application
                is AppPlugin -> {
                    //公共插件
                    project.configCommonPlugin()
                    //公共 android 配置项
                    project.extensions.getByType<AppExtension>().applyAppCommons(project)
                    //公共依赖
                    project.configAppDependencies()
                }
                //com.android.library
                is LibraryPlugin -> {
                    //公共插件
                    project.configCommonPlugin()
                    //公共 android 配置项
                    project.extensions.getByType<LibraryExtension>().applyLibraryCommons(project)
                    //公共依赖
                    project.configLibraryDependencies()
                }
                is KotlinAndroidPluginWrapper -> {
                    /**
                     * 根据Project的build.gradle.kts中的配置来动态设置kotlinVersion
                     */
                    project.getKotlinPluginVersion().also {
                    }
                }
            }
        }
    }

    /**
     * 配置公共Plugin插件
     * @receiver Project
     */
    private fun Project.configCommonPlugin() {
        plugins.apply(PLUGIN_KOTLIN_ANDROID)
        plugins.apply(PLUGIN_KAPT)
        plugins.apply(PLUGIN_KOTLIN_EXTENSIONS)
    }

    /**
     * Android Module配置公共依赖.
     * @receiver Project
     * @param baseLib String?
     */
    private fun Project.configAppDependencies(baseLib: String? = null) {
        dependencies.apply {
            add(implementation, fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
            add(implementation, BuildConfig.Kotlin.stdlib)
            // 统一引入baseLib,当有baseLib作为library时
            baseLib?.let {
                add(implementation, (project(":$baseLib")))
            }
            configTestDependencies()
        }
    }

    private fun Project.configLibraryDependencies() {
        dependencies.apply {
            add(api, fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
            add(implementation, BuildConfig.Kotlin.stdlib)
            configTestDependencies()
        }
    }

    /**
     * 配置Test依赖
     * @receiver DependencyHandler
     */
    private fun DependencyHandler.configTestDependencies() {
        testImplementation(Testing.testLibraries)
        androidTestImplementation(Testing.androidTestLibraries)
    }

    private fun AppExtension.applyAppCommons(project: Project) {
        defaultConfig {

        }
        applyBaseCommons(project)
    }

    /**
     * Library Module配置项
     * @receiver LibraryExtension
     * @param project Project
     */
    private fun LibraryExtension.applyLibraryCommons(project: Project) {
        applyBaseCommons(project)
    }

    /**
     * 配置公共需要添加的设置,如sdk版本,jdk版本等
     * @receiver BaseExtension
     * @param project Project
     */
    private fun BaseExtension.applyBaseCommons(project: Project) {
        compileSdkVersion(BuildConfig.compileSdkVersion)

        defaultConfig {
            targetSdk = BuildConfig.targetSdkVersion
            minSdk = BuildConfig.minSdkVersion
            testInstrumentationRunner = BuildConfig.testInstrumentationRunner
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        project.tasks.withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    companion object {
        const val PLUGIN_KAPT   = "kapt"
        const val PLUGIN_KOTLIN_ANDROID = "kotlin-android"
        const val PLUGIN_KOTLIN_EXTENSIONS = "kotlin-android-extensions"
    }
}