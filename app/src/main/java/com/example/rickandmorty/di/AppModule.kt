package com.example.rickandmorty.di

import com.example.rickandmorty.data.network.RickAndMortyApiService
import com.example.rickandmorty.data.repository.RickAndMortyRepository
import com.example.rickandmorty.data.repository.DefaultRickAndMortyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

/**
 * Hilt module that provides application-level dependencies.
 *
 * This module is installed in [SingletonComponent], meaning all provided dependencies have an
 * application-wide lifecycle.
 *
 * Provides:
 * - [RickAndMortyApiService]: Rick and Morty API service
 * - [RickAndMortyRepository]: Repository for fetching and caching character data.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    /**
     * Provides a singleton Retrofit instance for network calls.
     */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    /**
     * Provides the Rick and Morty API service.
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): RickAndMortyApiService {
        return retrofit.create(RickAndMortyApiService::class.java)
    }

    /**
     * Provides the repository for fetching and caching characters data.
     */
    @Provides
    @Singleton
    fun provideRepository(apiService: RickAndMortyApiService): RickAndMortyRepository {
        return DefaultRickAndMortyRepository(apiService)
    }
}