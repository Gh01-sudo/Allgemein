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
        // Spiegel von Maven Central (Google), falls Maven Central drosselt
        maven("https://maven-central.storage-download.googleapis.com/maven2/")
    }
}
rootProject.name = "K7Fahrwerkslabor"
include(":app")
