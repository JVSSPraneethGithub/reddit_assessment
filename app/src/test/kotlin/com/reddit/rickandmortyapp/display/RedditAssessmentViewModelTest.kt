package com.reddit.rickandmortyapp.display

import androidx.lifecycle.SavedStateHandle
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.testing.asPagingSourceFactory
import com.reddit.rickandmortyapp.data.api.RickAndMortyCharacter
import com.reddit.rickandmortyapp.data.api.RickAndMortyResponse
import com.reddit.rickandmortyapp.domain.entities.RickAndMortyCharacterEntity
import com.reddit.rickandmortyapp.domain.usecase.RedditAssessmentUseCase
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalSerializationApi::class)
class RedditAssessmentViewModelTest {

    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var response: RickAndMortyResponse
    private lateinit var responseList: List<RickAndMortyCharacter>
    private lateinit var testList: List<RickAndMortyCharacterEntity>
    private lateinit var viewModel: RedditAssessmentViewModel

    @MockK
    private lateinit var pagingUseCase: RedditAssessmentUseCase
    private val savedStateHandle = SavedStateHandle()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(StandardTestDispatcher())

        response = this::class.java.classLoader
            ?.getResourceAsStream("test_page.json")?.let {
                json.decodeFromStream<RickAndMortyResponse>(it)
            }!!.also {
                responseList = it.results
            }

        testList = responseList.map {
            RickAndMortyCharacterEntity(
                id = it.id,
                name = it.name,
                image = it.image
            )
        }

        coEvery {
            pagingUseCase.invoke()
        } returns Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 5
            ),
            pagingSourceFactory = testList.asPagingSourceFactory()
        ).flow

        viewModel = RedditAssessmentViewModel(
            savedStateHandle = savedStateHandle,
            useCase = pagingUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `test viewModel initialization`() = runTest {
        assertTrue(
            savedStateHandle.get<List<RickAndMortyCharacterEntity>>(viewModel.KEY_ENTITY_LIST)!!
                .isEmpty()
        )
        assertEquals(
            false,
            savedStateHandle.get<Boolean>(viewModel.KEY_PAGING_DATA)!!
        )
        assertEquals(2, savedStateHandle.keys().size)
        assertTrue(
            savedStateHandle.keys().containsAll(
                setOf(
                    viewModel.KEY_ENTITY_LIST,
                    viewModel.KEY_PAGING_DATA
                )
            )
        )
    }
}