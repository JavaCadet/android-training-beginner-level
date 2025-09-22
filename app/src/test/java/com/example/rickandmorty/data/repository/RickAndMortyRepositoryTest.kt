package com.example.rickandmorty.data.repository

import android.util.Log
import com.example.rickandmorty.data.model.Character
import com.example.rickandmorty.data.model.CharacterGender
import com.example.rickandmorty.data.model.CharacterStatus
import com.example.rickandmorty.data.model.CharactersResponse
import com.example.rickandmorty.data.model.Location
import com.example.rickandmorty.data.model.PageInfo
import com.example.rickandmorty.data.network.RickAndMortyApiService
import com.example.rickandmorty.utils.ApiResult
import kotlinx.coroutines.test.runTest
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import java.io.IOException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RickAndMortyRepositoryTest {

    private lateinit var log: MockedStatic<Log>
    private lateinit var apiService: RickAndMortyApiService
    private lateinit var repository: DefaultRickAndMortyRepository
    private val pageInfo = PageInfo(
        count = 99,
        pages = 5,
        next = "",
        prev = ""
    )
    private val characters = listOf(
        Character(
            id = 1,
            name = "Rick Sanchez",
            status = CharacterStatus.ALIVE,
            species = "Human",
            type = "",
            gender = CharacterGender.MALE,
            origin = Location(name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"),
            location = Location(name = "Earth (Replacement Dimension)", url = "https://rickandmortyapi.com/api/location/20"),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
        ),
        Character(
            id = 2,
            name = "Morty Smith",
            status = CharacterStatus.ALIVE,
            species = "Human",
            type = "",
            gender = CharacterGender.MALE,
            origin = Location(name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"),
            location = Location(name = "Earth (Replacement Dimension)", url = "https://rickandmortyapi.com/api/location/20"),
            image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
        )
    )
    private val response = CharactersResponse(
        info = pageInfo,
        results = characters
    )

    @BeforeTest
    fun setUp() {
        log = Mockito.mockStatic(Log::class.java)
        apiService = mock()
        repository = DefaultRickAndMortyRepository(apiService)
    }

    @AfterTest
    fun tearDown() {
        log.close()
    }

    @Test
    fun `getCharacters fetches from network on first call and caches`() = runTest {
        whenever(apiService.getCharactersResponse(any()))
            .thenReturn(response)

        // First call - fetch from network
        val result1 = repository.getCharacters()
        assertTrue(result1 is ApiResult.Success)
        assertEquals(characters, result1.data)

        // Second call - get from cache
        val result2 = repository.getCharacters()
        assertTrue(result2 is ApiResult.Success)
        assertEquals(characters, result2.data)
        verify(apiService, times(1)).getCharactersResponse(any())
    }

    @Test
    fun `getCharacters fetches next page when loadMore is true`() = runTest {
        val page1 = CharactersResponse(
            info = pageInfo,
            results = listOf(characters.first())
        )
        val page2 = CharactersResponse(
            info = pageInfo,
            results = listOf(characters.last())
        )

        whenever(apiService.getCharactersResponse(1))
            .thenReturn(page1)
        whenever(apiService.getCharactersResponse(2))
            .thenReturn(page2)

        repository.getCharacters(false)
        verify(apiService).getCharactersResponse(1)

        repository.getCharacters(true)
        verify(apiService).getCharactersResponse(2)
    }

    @Test
    fun `getCharacters handles IOException`() = runTest {
        whenever(apiService.getCharactersResponse(any()))
            .thenAnswer {
                throw IOException("No internet connection")
            }

        val result = repository.getCharacters()
        assertTrue(result is ApiResult.Failure)
        assertEquals("No internet connection", result.message)
    }

    @Test
    fun `getCharacters handles HttpException`() = runTest {
        val httpException = mock<HttpException>()
        whenever(httpException.code())
            .thenReturn(500)
        whenever(apiService.getCharactersResponse(any()))
            .thenThrow(httpException)

        val result = repository.getCharacters()
        assertTrue(result is ApiResult.Failure)
        assertTrue(result.message == "HTTP error: 500")
    }

    @Test
    fun `getCharacters handles unexpected error`() = runTest {
        whenever(apiService.getCharactersResponse(any()))
            .thenAnswer {
                throw IllegalStateException("This wasn't expected")
            }

        val result = repository.getCharacters()
        assertTrue(result is ApiResult.Failure)
        assertEquals("Unexpected error occurred", result.message)
    }

    @Test
    fun `getCharactersById fetches from network if not cached`() = runTest {
        whenever(apiService.getCharacterResponse(characters.first().id))
            .thenReturn(characters.first())

        val result = repository.getCharacterById(characters.first().id)
        assertTrue(result is ApiResult.Success)
        assertEquals(characters.first(), result.data)
        verify(apiService).getCharacterResponse(characters.first().id)
    }

    @Test
    fun `getCharacterById returns cached value without fetching from network`() = runTest {
        whenever(apiService.getCharactersResponse(any()))
            .thenReturn(response)

        // Pre-populate cache by fetching characters
        repository.getCharacters()

        val result = repository.getCharacterById(characters.first().id)
        assertTrue(result is ApiResult.Success)
        assertEquals(characters.first(), result.data)
        verify(apiService, never()).getCharacterResponse(characters.first().id)
    }

    @Test
    fun `getCharacterById handles IOException`() = runTest {
        whenever(apiService.getCharacterResponse(any()))
            .thenAnswer {
                throw IOException("No internet connection")
            }

        val result = repository.getCharacterById(99)
        assertTrue(result is ApiResult.Failure)
        assertEquals("No internet connection", result.message)
    }

    @Test
    fun `getCharacterById handles HttpException`() = runTest {
        val httpException = mock<HttpException>()
        whenever(httpException.code())
            .thenReturn(500)
        whenever(apiService.getCharacterResponse(any()))
            .thenThrow(httpException)

        val result = repository.getCharacterById(99)
        assertTrue(result is ApiResult.Failure)
        assertTrue(result.message == "HTTP error: 500")
    }

    @Test
    fun `getCharacterById handles unexpected error`() = runTest {
        whenever(apiService.getCharacterResponse(any()))
            .thenAnswer {
                throw IllegalStateException("This wasn't expected")
            }

        val result = repository.getCharacterById(99)
        assertTrue(result is ApiResult.Failure)
        assertEquals("Unexpected error occurred", result.message)
    }
}