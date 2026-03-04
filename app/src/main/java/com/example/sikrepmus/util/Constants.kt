package com.example.sikrepmus.util

object Constants {
    // API URLs
    const val DEEZER_BASE_URL = "https://api.deezer.com/"

    // Permissions
    const val PERMISSION_READ_AUDIO = android.Manifest.permission.READ_MEDIA_AUDIO
    const val PERMISSION_READ_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE

    // Navigation routes
    const val ROUTE_HOME = "home"
    const val ROUTE_SEARCH = "search"
    const val ROUTE_PLAYER = "player"
    const val ROUTE_LIBRARY = "library"

    // Database
    const val DATABASE_NAME = "sikrepmus_db"
    const val PREFERENCES_NAME = "sikrepmus_prefs"

    // Deezer search defaults
    const val DEFAULT_SEARCH_LIMIT = 25
}
