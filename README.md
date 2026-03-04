# SikRepMus 🎵

A modern Android music player app built with Jetpack Compose, MVVM architecture, and Deezer API integration.

## Stack Tecnológico

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material3
- **Architecture**: MVVM + Repository Pattern
- **Local Storage**: Room Database
- **Networking**: Retrofit2 + OkHttp3
- **Image Loading**: Coil
- **Media Playback**: Media3 ExoPlayer
- **Async**: Kotlin Coroutines + StateFlow
- **Build**: Gradle KTS + KSP

## Features

### Implementadas ✅
- Reproducción de música local (MediaStore)
- Búsqueda local por título, artista y álbum
- **Búsqueda remota con Deezer API** (nuevo)
- Biblioteca con vista por artistas, álbumes, géneros y carpetas
- Mini player y pantalla Now Playing
- Modo aleatorio y repetición
- Gestión de carpetas personalizadas

### Búsqueda Deezer API
La pantalla de búsqueda soporta dos modos:
- **Local**: Busca entre las canciones almacenadas en el dispositivo
- **Deezer**: Realiza búsquedas en tiempo real contra `https://api.deezer.com/search`

## Estructura del Proyecto

```
app/src/main/java/com/example/sikrepmus/
├── data/
│   ├── local/          # Room DB, DAOs, Entities
│   ├── model/          # Domain models (Song, SearchResult)
│   ├── remote/         # Retrofit, DTOs, DeezerApi
│   └── repository/     # MusicRepository, DeezerRepository
├── presentation/
│   └── ui/search/      # SearchViewModel (Deezer)
├── ui/
│   ├── screens/        # Compose screens
│   ├── theme/          # Material3 theme
│   └── viewmodel/      # MusicViewModel (local)
└── util/               # Constants, Extensions
```

## Setup

1. Clonar el repositorio
2. Abrir en Android Studio
3. Sincronizar Gradle
4. Ejecutar en dispositivo/emulador con Android 7.0+ (API 24+)

> **Nota**: La búsqueda Deezer requiere conexión a internet. No se necesita API key.

## Permisos

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
```
