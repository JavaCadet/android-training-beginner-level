package com.example.rickandmorty

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Custom [Application] class for the Rick and Morty app.
 *
 * This sets up the dependency injection container at the application level, making Hilt available
 * throughout the entire app lifecycle.
 *
 * Hilt will:
 * - Generate a base class that this class extends.
 * - Install application-level bindings into the [dagger.hilt.components.SingletonComponent]
 */
@HiltAndroidApp
class RickAndMortyApplication : Application()
